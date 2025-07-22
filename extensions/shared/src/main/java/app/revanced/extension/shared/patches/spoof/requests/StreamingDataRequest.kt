package app.revanced.extension.shared.patches.spoof.requests

import androidx.annotation.GuardedBy
import app.revanced.extension.shared.innertube.client.YouTubeAppClient
import app.revanced.extension.shared.innertube.requests.InnerTubeRequestBody.createApplicationRequestBody
import app.revanced.extension.shared.innertube.requests.InnerTubeRequestBody.createTVRequestBody
import app.revanced.extension.shared.innertube.requests.InnerTubeRequestBody.getInnerTubeResponseConnectionFromRoute
import app.revanced.extension.shared.innertube.requests.InnerTubeRoutes.GET_ADAPTIVE_FORMATS
import app.revanced.extension.shared.innertube.requests.InnerTubeRoutes.GET_STREAMING_DATA
import app.revanced.extension.shared.innertube.utils.JavaScriptUtils
import app.revanced.extension.shared.patches.components.ByteArrayFilterGroup
import app.revanced.extension.shared.requests.Requester
import app.revanced.extension.shared.settings.BaseSettings
import app.revanced.extension.shared.settings.EnumSetting
import app.revanced.extension.shared.utils.Logger
import app.revanced.extension.shared.utils.StringRef.str
import app.revanced.extension.shared.utils.Utils
import org.apache.commons.lang3.StringUtils
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.util.Collections
import java.util.Objects
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


/**
 * Video streaming data.  Fetching is tied to the behavior YT uses,
 * where this class fetches the streams only when YT fetches.
 *
 * Effectively the cache expiration of these fetches is the same as the stock app,
 * since the stock app would not use expired streams and therefor
 * the extension replace stream hook is called only if YT
 * did use its own client streams.
 */
class StreamingDataRequest private constructor(
    videoId: String,
    requestHeader: Map<String, String>,
    reasonSkipped: String,
) {
    private val videoId: String
    private val future: Future<Pair<ByteBuffer?, ArrayList<String>?>?>

    init {
        Objects.requireNonNull(requestHeader)
        this.videoId = videoId
        this.future = Utils.submitOnBackgroundThread {
            fetch(
                videoId,
                requestHeader,
                reasonSkipped,
            )
        }
    }

    fun fetchCompleted(): Boolean {
        return future.isDone
    }

    // first: StreamingData data to be used by ProtoBuf MessageParser
    // second: ArrayList containing the deobfuscated streamingUrl
    val streamPair: Pair<ByteBuffer?, ArrayList<String>?>?
        get() {
            try {
                return future[MAX_MILLISECONDS_TO_WAIT_FOR_FETCH.toLong(), TimeUnit.MILLISECONDS]
            } catch (ex: TimeoutException) {
                Logger.printInfo(
                    { "getStream timed out" },
                    ex
                )
            } catch (ex: InterruptedException) {
                Logger.printException(
                    { "getStream interrupted" },
                    ex
                )
                Thread.currentThread().interrupt() // Restore interrupt status flag.
            } catch (ex: ExecutionException) {
                Logger.printException(
                    { "getStream failure" },
                    ex
                )
            }

            return null
        }

    override fun toString(): String {
        return "StreamingDataRequest{videoId='$videoId'}"
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val PAGE_ID_HEADER = "X-Goog-PageId"
        private const val MAX_MILLISECONDS_TO_WAIT_FOR_FETCH = 25 * 1000

        private val SPOOF_STREAMING_DATA_TYPE: EnumSetting<YouTubeAppClient.ClientType> =
            BaseSettings.SPOOF_STREAMING_DATA_TYPE
        private val CLIENT_ORDER_TO_USE: Array<YouTubeAppClient.ClientType> =
            YouTubeAppClient.availableClientTypes(SPOOF_STREAMING_DATA_TYPE)
        private val DEFAULT_CLIENT_IS_ANDROID_VR_NO_AUTH: Boolean =
            SPOOF_STREAMING_DATA_TYPE.get() == YouTubeAppClient.ClientType.ANDROID_VR_NO_AUTH
        private val liveStreams: ByteArrayFilterGroup =
            ByteArrayFilterGroup(
                null,
                "yt_live_broadcast",
                "yt_premiere_broadcast"
            )
        private var lastSpoofedClientFriendlyName: String? = null

        // When this value is not empty, it is used as the preferred language when creating the RequestBody.
        private var overrideLanguage: String = ""

        @GuardedBy("itself")
        val cache: MutableMap<String, StreamingDataRequest> = Collections.synchronizedMap(
            object : LinkedHashMap<String, StreamingDataRequest>(100) {
                private val CACHE_LIMIT = 50

                override fun removeEldestEntry(eldest: Map.Entry<String, StreamingDataRequest>): Boolean {
                    return size > CACHE_LIMIT // Evict the oldest entry if over the cache limit.
                }
            })

        @JvmStatic
        val lastSpoofedClientName: String
            get() {
                return if (lastSpoofedClientFriendlyName != null) {
                    lastSpoofedClientFriendlyName!!
                } else {
                    "Unknown"
                }
            }

        @JvmStatic
        val lastSpoofedClientIsAndroidVRNoAuth: Boolean
            get() = lastSpoofedClientFriendlyName != null
                    && lastSpoofedClientFriendlyName!! == YouTubeAppClient.ClientType.ANDROID_VR_NO_AUTH.friendlyName

        @JvmStatic
        val lastSpoofedClientIsIOS: Boolean
            get() = lastSpoofedClientFriendlyName != null
                    && lastSpoofedClientFriendlyName!!.startsWith("iOS")

        @JvmStatic
        val lastSpoofedClientIsTV: Boolean
            get() = lastSpoofedClientFriendlyName != null
                    && lastSpoofedClientFriendlyName!!.startsWith("TV")

        @JvmStatic
        fun overrideLanguage(language: String) {
            overrideLanguage = language
        }

        @JvmStatic
        fun fetchRequest(
            videoId: String,
            fetchHeaders: Map<String, String>,
            reasonSkipped: String,
        ) {
            // Always fetch, even if there is an existing request for the same video.
            cache[videoId] =
                StreamingDataRequest(
                    videoId,
                    fetchHeaders,
                    reasonSkipped,
                )
        }

        @JvmStatic
        fun getRequestForVideoId(videoId: String): StreamingDataRequest? {
            return cache[videoId]
        }

        private fun handleConnectionError(
            toastMessage: String,
            ex: Exception?,
            showToast: Boolean = false,
        ) {
            if (showToast) Utils.showToastShort(toastMessage)
            Logger.printInfo({ toastMessage }, ex)
        }

        /**
         * In Android YouTube client, Protobuf Message related to video streaming does not have signatureCipher field.
         * Even if the proto buffer contains signatureCipher, Protobuf MessageParser cannot parse it.
         *
         * For this reason, the url protected by signatureCipher must first be decrypted in the extensions.
         * Then, the decrypted url must be injected into the url field of YouTube's video streaming class.
         */
        private fun getDeobfuscatedUrlArrayList(
            clientType: YouTubeAppClient.ClientType,
            videoId: String,
            requestHeader: Map<String, String>,
        ): ArrayList<String>? {
            Objects.requireNonNull(clientType)
            Objects.requireNonNull(videoId)
            Objects.requireNonNull(requestHeader)

            val startTime = System.currentTimeMillis()
            val clientTypeName = clientType.name
            Logger.printDebug { "Fetching formats request for: $videoId, clientType: $clientTypeName" }

            try {
                val connection =
                    getInnerTubeResponseConnectionFromRoute(
                        GET_ADAPTIVE_FORMATS,
                        clientType,
                        requestHeader
                    )

                // Javascript is used to get the signatureTimestamp
                val requestBody = createTVRequestBody(
                    clientType = clientType,
                    videoId = videoId,
                )

                connection.setFixedLengthStreamingMode(requestBody.size)
                connection.outputStream.write(requestBody)

                val responseCode = connection.responseCode
                if (connection.contentLength != 0 && responseCode == 200) {
                    val json = Requester.parseJSONObject(connection)
                    val streamingData = json.getJSONObject("streamingData")

                    if (streamingData.has("initialAuthorizedDrmTrackTypes")) {
                        // Age-regulated video or purchased video.
                        // The patch does not yet support decrypting DRM-protected videos on the TV client.
                        Logger.printDebug { "Since the video is DRM protected, legacy client will be used" }
                        return null
                    }

                    val adaptiveFormats = streamingData.getJSONArray("adaptiveFormats")

                    // Deobfuscated streaming urls are added to the ArrayList in order.
                    val deobfuscatedUrlArrayList: ArrayList<String> = ArrayList(adaptiveFormats.length())

                    for (i in 0..<adaptiveFormats.length()) {
                        var streamUrl: String? = null
                        val formatData = adaptiveFormats.getJSONObject(i)
                        if (formatData.has("url")) {
                            // The response contains a streamingUrl.
                            // The 'n' query parameter of streamingUrl is obfuscated.
                            streamUrl = formatData.getString("url")
                        } else if (formatData.has("signatureCipher")) {
                            // The response contains a signatureCipher.
                            // The 'url' query parameter of signatureCipher contains streamingUrl.
                            // The 'n' query parameter of streamingUrl is obfuscated.
                            val signatureCipher = formatData.getString("signatureCipher")
                            if (StringUtils.isNotEmpty(signatureCipher)) {
                                streamUrl = JavaScriptUtils.getUrlWithThrottlingParameterObfuscated(videoId, signatureCipher)
                            }
                        } else {
                            // Neither streamingUrl nor signatureCipher are present in the response.
                            // In this case, serverAbrStreamingUrl will be present, which can be used to deobfuscate.
                            // Currently, serverAbrStreamingUrl is only used for clients that require a PoToken.
                            Logger.printDebug { "Neither streamingUrl nor signatureCipher were found, legacy client will be used" }
                            return null
                        }
                        // streamUrl not found, trying to fetch with legacy client.
                        if (StringUtils.isEmpty(streamUrl)) {
                            Logger.printDebug { "StreamUrl was not found, legacy client will be used" }
                            return null
                        }
                        val url = JavaScriptUtils.getUrlWithThrottlingParameterDeobfuscated(videoId, streamUrl!!)
                        if (StringUtils.isEmpty(url)) {
                            Logger.printDebug { "Failed to decrypt n-sig or signatureCipher, please check if latest regular expressions are being used" }
                            return null
                        }
                        deobfuscatedUrlArrayList.add(i, url!!)
                        Logger.printDebug { "deobfuscatedUrl added to ArrayList, videoId: $videoId, index: $i" }
                    }

                    return deobfuscatedUrlArrayList
                }

                handleConnectionError(
                    (clientTypeName + " not available with response code: "
                            + responseCode + " message: " + connection.responseMessage),
                    null
                )
            } catch (ex: SocketTimeoutException) {
                handleConnectionError("Connection timeout", ex)
            } catch (ex: IOException) {
                handleConnectionError("Network error", ex)
            } catch (ex: Exception) {
                Logger.printException({ "url check failed" }, ex)
            } finally {
                Logger.printDebug { "Fetching video formats request end, video: " + videoId + " took: " + (System.currentTimeMillis() - startTime) + "ms" }
            }

            return null
        }

        private fun send(
            clientType: YouTubeAppClient.ClientType,
            videoId: String,
            requestHeader: Map<String, String>,
        ): HttpURLConnection? {
            Objects.requireNonNull(clientType)
            Objects.requireNonNull(videoId)
            Objects.requireNonNull(requestHeader)

            val startTime = System.currentTimeMillis()
            Logger.printDebug { "Fetching video streams for: $videoId using client: $clientType" }

            try {
                val connection =
                    getInnerTubeResponseConnectionFromRoute(
                        GET_STREAMING_DATA,
                        clientType,
                        requestHeader
                    )

                val requestBody = if (clientType.requireJS) {
                    // Javascript is used to get the signatureTimestamp
                    createTVRequestBody(
                        clientType = clientType,
                        videoId = videoId,
                    )
                } else {
                    createApplicationRequestBody(
                        clientType = clientType,
                        videoId = videoId,
                        setLocale = DEFAULT_CLIENT_IS_ANDROID_VR_NO_AUTH,
                        language = overrideLanguage.ifEmpty { BaseSettings.SPOOF_STREAMING_DATA_VR_LANGUAGE.get().language }
                    )
                }

                connection.setFixedLengthStreamingMode(requestBody.size)
                connection.outputStream.write(requestBody)

                val responseCode = connection.responseCode
                if (responseCode == 200) return connection

                // This situation likely means the patches are outdated.
                // Use a toast message that suggests updating.
                handleConnectionError(
                    ("Playback error (App is outdated?) " + clientType + ": "
                            + responseCode + " response: " + connection.responseMessage),
                    null
                )
            } catch (ex: SocketTimeoutException) {
                handleConnectionError("Connection timeout", ex)
            } catch (ex: IOException) {
                handleConnectionError("Network error", ex)
            } catch (ex: Exception) {
                Logger.printException({ "send failed" }, ex)
            } finally {
                Logger.printDebug { "Fetching video streams request end, video: " + videoId + " took: " + (System.currentTimeMillis() - startTime) + "ms" }
            }

            return null
        }

        private fun fetch(
            videoId: String,
            requestHeader: Map<String, String>,
            reasonSkipped: String,
        ): Pair<ByteBuffer?, ArrayList<String>?>? {
            lastSpoofedClientFriendlyName = null

            // MutableMap containing the deobfuscated streamingUrl.
            // This is used for clients where streamingUrl is obfuscated.
            var deobfuscatedUrlArrayList: ArrayList<String>? = null

            // Retry with different client if empty response body is received.
            for (clientType in CLIENT_ORDER_TO_USE) {
                if (clientType.requireAuth &&
                    StringUtils.isAllEmpty(requestHeader[AUTHORIZATION_HEADER], requestHeader[PAGE_ID_HEADER])
                ) {
                    Logger.printDebug { "Skipped login-required client (incognito mode or not logged in), Client: $clientType, Video: $videoId" }
                    continue
                }
                // Javascript is used to deobfuscate streamingUrl
                if (clientType.requireJS) {
                    // Decrypting signatureCipher takes time, so videos start 5 to 15 seconds late.
                    // Unlike regular videos, no one would be willing to wait 5 to 15 seconds to play a 10-second Shorts.
                    // For this reason, Shorts uses legacy clients that are not protected by signatureCipher.
                    if (StringUtils.isNotEmpty(reasonSkipped)) {
                        Logger.printDebug { "Skipped javascript required client, Reason: $reasonSkipped, Client: $clientType, Video: $videoId" }
                        continue
                    }
                    // ArrayList containing the deobfuscated streamingUrl
                    deobfuscatedUrlArrayList = getDeobfuscatedUrlArrayList(clientType, videoId, requestHeader)
                    if (deobfuscatedUrlArrayList.isNullOrEmpty()) {
                        Logger.printDebug { "Skipped javascript required client, Reason: Failed to deobfuscate streamingUrl, Client: $clientType Video: $videoId" }
                        continue
                    }
                }

                val connection = send(clientType, videoId, requestHeader)
                if (connection != null) {
                    try {
                        // gzip encoding doesn't response with content length (-1),
                        // but empty response body does.
                        if (connection.contentLength == 0) {
                            Logger.printDebug { "Received empty response, Client: $clientType, Video: $videoId" }
                        } else {
                            BufferedInputStream(connection.inputStream).use { inputStream ->
                                ByteArrayOutputStream().use { stream ->
                                    val buffer = ByteArray(2048)
                                    var bytesRead: Int
                                    while ((inputStream.read(buffer)
                                            .also { bytesRead = it }) >= 0
                                    ) {
                                        stream.write(buffer, 0, bytesRead)
                                    }
                                    // Android Creator can't play livestreams, but it doesn't have an empty response (no formats available).
                                    // Since it doesn't have an empty response, the app doesn't try to fetch with another client, and it tries to play the livestream.
                                    // However, the response doesn't contain any formats available, so an exception is thrown.
                                    // As a workaround for this issue, if Android Creator is used for fetching, it should check if the video is a livestream.
                                    if (clientType == YouTubeAppClient.ClientType.ANDROID_CREATOR
                                        && liveStreams.check(buffer).isFiltered //
                                    ) {
                                        Logger.printDebug { "Ignore Android Studio spoofing as it is a livestream (video: $videoId)" }
                                    } else {
                                        lastSpoofedClientFriendlyName = clientType.friendlyName
                                        return Pair(ByteBuffer.wrap(stream.toByteArray()), deobfuscatedUrlArrayList)
                                    }
                                }
                            }
                        }
                    } catch (ex: IOException) {
                        Logger.printException({ "Fetch failed while processing response data" }, ex)
                    }
                }
            }

            val showToast = BaseSettings.DEBUG_TOAST_ON_ERROR.get()
            handleConnectionError(
                str("revanced_spoof_streaming_data_failed_forbidden"),
                null,
                showToast
            )
            handleConnectionError(
                str("revanced_spoof_streaming_data_failed_forbidden_suggestion"),
                null,
                showToast
            )
            return null
        }
    }
}
