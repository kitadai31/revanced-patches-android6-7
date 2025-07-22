-dontobfuscate
-dontoptimize
-keepattributes *
-keep class app.revanced.** {
  *;
}
-keep class com.google.** {
  *;
}
## Rules for OkHttp. Copy pasted from https://github.com/square/okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
