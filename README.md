# YouTube ReVanced for Android 6.0-7.1
ReVanced Extended fork for YouTube 17.34.36. (unofficial)  
A fork of inotia00's [ReVanced Extended](https://github.com/inotia00/revanced-patches) (RVX) Patches.

## How to patch
See [[How to build]](https://github.com/kitadai31/revanced-patches-android6-7/wiki/How-to-build) page in documentations.

Other information is also available on [[documentations]](https://github.com/kitadai31/revanced-patches-android6-7/wiki) (GitHub Wiki).

## About
The last YouTube app that supports Android 6-7 is **17.34.36**.

However, the latest ReVanced/RVX Patches doesn't support YouTube 17.34.36.  
By using this fork, you can patch YouTube 17.34.36 and you can use YouTube ReVanced on Android 6 or 7.

This fork is the latest RVX Patches with minimal changes to support 17.34.36.

## Announcement of major update
Since January 2025, this fork is based on the latest RVX Patches.

Previously, this patch was based on RVX v2.160.1 (Feb 13, 2023).  
But it was too old and has less features than now.  
For example, the current RVX Patches contains a lot of new features that should also be available in 17.34.36, but this fork didn't have such features.  
So I moved to latest version of RVX Patches and added only minimal changes for 17.34.36. 

Almost all RVX features are available now!  
And it will always follow the RVX's update.  
I keep the diff commits clean, so I can follow the RVX updates by git rebase the diff commits.

## 📋 List of patches in this repository

### [📦 `com.google.android.youtube`](https://play.google.com/store/apps/details?id=com.google.android.youtube)
<details>

| 💊 Patch | 📜 Description | 🏹 Target Version |
|:--------:|:--------------:|:-----------------:|
| `Alternative thumbnails` | Adds options to replace video thumbnails using the DeArrow API or image captures from the video. | 17.34.36 |
| `Bypass image region restrictions` | Adds an option to use a different host for static images, so that images blocked in some countries can be received. | 17.34.36 |
| `Change start page` | Adds an option to set which page the app opens in instead of the homepage. | 17.34.36 |
| `Custom Shorts action buttons` | Changes, at compile time, the icon of the action buttons of the Shorts player. | 17.34.36 |
| `Custom branding icon for YouTube` | Changes the YouTube app icon to the icon specified in patch options. | 17.34.36 |
| `Custom branding name for YouTube` | Renames the YouTube app to the name specified in patch options. | 17.34.36 |
| `Custom double tap length` | Adds Double-tap to seek values that are specified in patch options. | 17.34.36 |
| `Custom header for YouTube` | Applies a custom header in the top left corner within the app. | 17.34.36 |
| `Description components` | Adds options to hide and disable description components. | 17.34.36 |
| `Disable QUIC protocol` | Adds an option to disable CronetEngine's QUIC protocol. | 17.34.36 |
| `Disable auto audio tracks` | Adds an option to disable audio tracks from being automatically enabled. | 17.34.36 |
| `Disable auto captions` | Adds an option to disable captions from being automatically enabled. | 17.34.36 |
| `Disable haptic feedback` | Adds options to disable haptic feedback when swiping in the video player. | 17.34.36 |
| `Disable resuming Shorts on startup` | Adds an option to disable the Shorts player from resuming on app startup when Shorts were last being watched. | 17.34.36 |
| `Enable OPUS codec` | Adds an options to enable the OPUS audio codec if the player response includes. | 17.34.36 |
| `Enable debug logging` | Adds an option to enable debug logging. | 17.34.36 |
| `Enable external browser` | Adds an option to always open links in your browser instead of in the in-app-browser. | 17.34.36 |
| `Enable open links directly` | Adds an option to skip over redirection URLs in external links. | 17.34.36 |
| `Force hide player buttons background` | Removes, at compile time, the dark background surrounding the video player controls. | 17.34.36 |
| `Fullscreen components` | Adds options to hide or change components related to fullscreen. | 17.34.36 |
| `GmsCore support` | Allows patched Google apps to run without root and under a different package name by using GmsCore instead of Google Play Services. | 17.34.36 |
| `Hide Shorts dimming` | Removes, at compile time, the dimming effect at the top and bottom of Shorts videos. | 17.34.36 |
| `Hide action buttons` | Adds options to hide action buttons under videos. | 17.34.36 |
| `Hide ads` | Adds options to hide ads. | 17.34.36 |
| `Hide comments components` | Adds options to hide components related to comments. | 17.34.36 |
| `Hide feed components` | Adds options to hide components related to feeds. | 17.34.36 |
| `Hide feed flyout menu` | Adds the ability to hide feed flyout menu components using a custom filter. | 17.34.36 |
| `Hide layout components` | Adds options to hide general layout components. | 17.34.36 |
| `Hide player buttons` | Adds options to hide buttons in the video player. | 17.34.36 |
| `Hide player flyout menu` | Adds options to hide player flyout menu components. | 17.34.36 |
| `Hide shortcuts` | Remove, at compile time, the app shortcuts that appears when the app icon is long pressed. | 17.34.36 |
| `Hook YouTube Music actions` | Adds support for opening music in RVX Music using the in-app YouTube Music button. | 17.34.36 |
| `Hook download actions` | Adds support to download videos with an external downloader app using the in-app download button. | 17.34.36 |
| `Layout switch` | Adds an option to spoof the dpi in order to use a tablet or phone layout. | 17.34.36 |
| `MaterialYou` | Applies the MaterialYou theme for Android 12+ devices. | 17.34.36 |
| `Miniplayer` | Adds options to change the in app minimized player, and if patching target 19.16+ adds options to use modern miniplayers. | 17.34.36 |
| `Navigation bar components` | Adds options to hide or change components related to the navigation bar. | 17.34.36 |
| `Overlay buttons` | Adds options to display overlay buttons in the video player. | 17.34.36 |
| `Player components` | Adds options to hide or change components related to the video player. | 17.34.36 |
| `Remove background playback restrictions` | Removes restrictions on background playback, including for music and kids videos. | 17.34.36 |
| `Remove viewer discretion dialog` | Adds an option to remove the dialog that appears when opening a video that has been age-restricted by accepting it automatically. This does not bypass the age restriction. | 17.34.36 |
| `Return YouTube Dislike` | Adds an option to show the dislike count of videos using the Return YouTube Dislike API. | 17.34.36 |
| `Return YouTube Username` | Adds an option to replace YouTube handles with usernames in comments using YouTube Data API v3. | 17.34.36 |
| `Sanitize sharing links` | Adds an option to remove tracking query parameters from URLs when sharing links. | 17.34.36 |
| `Seekbar components` | Adds options to hide or change components related to the seekbar. | 17.34.36 |
| `Settings for YouTube` | Applies mandatory patches to implement ReVanced Extended settings into the application. | 17.34.36 |
| `Shorts components` | Adds options to hide or change components related to YouTube Shorts. | 17.34.36 |
| `Shorts seek` | Adds an option to replace toolbar buttons in Shorts player with skip/rewind buttons. | 17.34.36 |
| `SponsorBlock` | Adds options to enable and configure SponsorBlock, which can skip undesired video segments, such as sponsored content. | 17.34.36 |
| `Spoof app version` | [ONLY FOR DEBUG PURPOSE] Adds options to spoof the YouTube client version. But there is no useful target version for 17.34.36. (17.39.xx enables some new UI, but video description crashes if it contains YouTube links. 17.40.xx+ completely breaks the app.) | 17.34.36 |
| `Spoof streaming data` | Adds options to spoof the streaming data to allow playback. | 17.34.36 |
| `Swipe controls` | Adds options for controlling volume and brightness with swiping, and whether to enter fullscreen when swiping down below the player. | 17.34.36 |
| `Theme` | Changes the app's theme to the values specified in patch options. | 17.34.36 |
| `Toolbar components` | Adds options to hide or change components located on the toolbar, such as the search bar, header, and toolbar buttons. | 17.34.36 |
| `Translations for YouTube` | Add translations or remove string resources. | 17.34.36 |
| `Video playback` | Adds options to customize settings related to video playback, such as default video quality and playback speed. | 17.34.36 |
| `Visual preferences icons for YouTube` | Adds icons to specific preferences in the settings. | 17.34.36 |
| `Watch history` | Adds an option to change the domain of the watch history or check its status. | 17.34.36 |
</details>

### [📦 `com.dummy.dummyapp1`](https://play.google.com/store/apps/details?id=com.dummy.dummyapp1)
<details>

| 💊 Patch | 📜 Description | 🏹 Target Version |
|:--------:|:--------------:|:-----------------:|
| `Bitrate default value` | Sets the audio quality to 'Always High' when you first install the app. | 6.20.51 ~ 7.25.53 |
| `Bypass image region restrictions` | Adds an option to use a different host for static images, so that images blocked in some countries can be received. | 6.20.51 ~ 7.25.53 |
| `Certificate spoof` | Enables YouTube Music to work with Android Auto by spoofing the YouTube Music certificate. | 6.20.51 ~ 7.25.53 |
| `Change share sheet` | Adds an option to change the in-app share sheet to the system share sheet. | 6.20.51 ~ 7.25.53 |
| `Change start page` | Adds an option to set which page the app opens in instead of the homepage. | 6.20.51 ~ 7.25.53 |
| `Custom branding icon for YouTube Music` | Changes the YouTube Music app icon to the icon specified in patch options. | 6.20.51 ~ 7.25.53 |
| `Custom branding name for YouTube Music` | Renames the YouTube Music app to the name specified in patch options. | 6.20.51 ~ 7.25.53 |
| `Custom header for YouTube Music` | Applies a custom header in the top left corner within the app. | 6.20.51 ~ 7.25.53 |
| `Dark theme` | Changes the app's dark theme to the values specified in patch options. | 6.20.51 ~ 7.25.53 |
| `Disable Cairo splash animation` | Adds an option to disable Cairo splash animation. | 7.06.54 ~ 7.25.53 |
| `Disable DRC audio` | Adds an option to disable DRC (Dynamic Range Compression) audio. | 6.20.51 ~ 7.25.53 |
| `Disable auto captions` | Adds an option to disable captions from being automatically enabled. | 6.20.51 ~ 7.25.53 |
| `Disable dislike redirection` | Adds an option to disable redirection to the next track when clicking the Dislike button. | 6.20.51 ~ 7.25.53 |
| `Disable music video in album` | Adds option to redirect music videos from albums for non-premium users. | 6.20.51 ~ 7.25.53 |
| `Enable OPUS codec` | Adds an options to enable the OPUS audio codec if the player response includes. | 6.20.51 ~ 7.25.53 |
| `Enable debug logging` | Adds an option to enable debug logging. | 6.20.51 ~ 7.25.53 |
| `Enable landscape mode` | Adds an option to enable landscape mode when rotating the screen on phones. | 6.20.51 ~ 7.25.53 |
| `Flyout menu components` | Adds options to hide or change flyout menu components. | 6.20.51 ~ 7.25.53 |
| `GmsCore support` | Allows patched Google apps to run without root and under a different package name by using GmsCore instead of Google Play Services. | 6.20.51 ~ 7.25.53 |
| `Hide account components` | Adds options to hide components related to the account menu. | 6.20.51 ~ 7.25.53 |
| `Hide action bar components` | Adds options to hide action bar components and replace the offline download button with an external download button. | 6.20.51 ~ 7.25.53 |
| `Hide ads` | Adds options to hide ads. | 6.20.51 ~ 7.25.53 |
| `Hide layout components` | Adds options to hide general layout components. | 6.20.51 ~ 7.25.53 |
| `Hide overlay filter` | Removes, at compile time, the dark overlay that appears when player flyout menus are open. | 6.20.51 ~ 7.25.53 |
| `Hide player overlay filter` | Removes, at compile time, the dark overlay that appears when single-tapping in the player. | 6.20.51 ~ 7.25.53 |
| `Navigation bar components` | Adds options to hide or change components related to the navigation bar. | 6.20.51 ~ 7.25.53 |
| `Player components` | Adds options to hide or change components related to the player. | 6.20.51 ~ 7.25.53 |
| `Remove background playback restrictions` | Removes restrictions on background playback, including for kids videos. | 6.20.51 ~ 7.25.53 |
| `Remove viewer discretion dialog` | Adds an option to remove the dialog that appears when opening a video that has been age-restricted by accepting it automatically. This does not bypass the age restriction. | 6.20.51 ~ 7.25.53 |
| `Restore old style library shelf` | Adds an option to return the Library tab to the old style. | 6.20.51 ~ 7.25.53 |
| `Return YouTube Dislike` | Adds an option to show the dislike count of songs using the Return YouTube Dislike API. | 6.20.51 ~ 7.25.53 |
| `Return YouTube Username` | Adds an option to replace YouTube handles with usernames in comments using YouTube Data API v3. | 6.20.51 ~ 7.25.53 |
| `Sanitize sharing links` | Adds an option to remove tracking query parameters from URLs when sharing links. | 6.20.51 ~ 7.25.53 |
| `Settings for YouTube Music` | Applies mandatory patches to implement ReVanced Extended settings into the application. | 6.20.51 ~ 7.25.53 |
| `SponsorBlock` | Adds options to enable and configure SponsorBlock, which can skip undesired video segments, such as non-music sections. | 6.20.51 ~ 7.25.53 |
| `Spoof app version` | Adds options to spoof the YouTube Music client version. This can remove the radio mode restriction in Canadian regions or disable real-time lyrics. | 6.20.51 ~ 7.16.53 |
| `Spoof client` | Adds options to spoof the client to allow playback. | 6.20.51 ~ 7.16.53 |
| `Spoof streaming data` | Adds options to spoof the streaming data to allow playback. | 6.20.51 ~ 7.25.53 |
| `Translations for YouTube Music` | Add translations or remove string resources. | 6.20.51 ~ 7.25.53 |
| `Video playback` | Adds options to customize settings related to video playback, such as default video quality and playback speed. | 6.20.51 ~ 7.25.53 |
| `Visual preferences icons for YouTube Music` | Adds icons to specific preferences in the settings. | 6.20.51 ~ 7.25.53 |
</details>

### [📦 `com.dummy.dummyapp2`](https://play.google.com/store/apps/details?id=com.dummy.dummyapp2)
<details>

| 💊 Patch | 📜 Description | 🏹 Target Version |
|:--------:|:--------------:|:-----------------:|
| `Change package name` | Changes the package name for Reddit to the name specified in patch options. | ALL |
| `Custom branding name for Reddit` | Renames the Reddit app to the name specified in patch options. | ALL |
| `Disable screenshot popup` | Adds an option to disable the popup that appears when taking a screenshot. | ALL |
| `Hide Recently Visited shelf` | Adds an option to hide the Recently Visited shelf in the sidebar. | ALL |
| `Hide ads` | Adds options to hide ads. | ALL |
| `Hide navigation buttons` | Adds options to hide buttons in the navigation bar. | ALL |
| `Hide recommended communities shelf` | Adds an option to hide the recommended communities shelves in subreddits. | ALL |
| `Open links directly` | Adds an option to skip over redirection URLs in external links. | ALL |
| `Open links externally` | Adds an option to always open links in your browser instead of in the in-app-browser. | ALL |
| `Premium icon` | Unlocks premium app icons. | ALL |
| `Remove subreddit dialog` | Adds options to remove the NSFW community warning and notifications suggestion dialogs by dismissing them automatically. | ALL |
| `Sanitize sharing links` | Adds an option to remove tracking query parameters from URLs when sharing links. | ALL |
| `Settings for Reddit` | Applies mandatory patches to implement ReVanced Extended settings into the application. | ALL |
</details>



## Telegram <img height="24px" src="https://user-images.githubusercontent.com/13122796/178032213-faf25ab8-0bc3-4a94-a730-b524c96df124.png" />
[![TelegramChannel](https://img.shields.io/badge/Telegram_news_channel-2CA5E0?style=for-the-badge&logo=Telegram&logoColor=white)](https://t.me/rvx_for_a6_7)
[![TelegramChat](https://img.shields.io/badge/Telegram_chat_group-2CA5E0?style=for-the-badge&logo=Telegram&logoColor=white)](https://t.me/rvx_for_a6_7_chat)

Check the Telegram channel for the latest announcements!

Join the Telegram chat for discussions.  
(We also have [GitHub Discussions](https://github.com/kitadai31/revanced-patches-android6-7/discussions))

## About YouTube Music
This fork doesn't have patches for YT Music.

For YT Music, use inotia00's official RVX Patches.  
It supports old YT Music versions for Android 5.0-7.1.  
(YT Music `v6.20.51` for Android 5-6, `v6.42.55` for Android 7.x)

See [About YouTube Music](https://github.com/kitadai31/revanced-patches-android6-7/wiki/About-YouTube-Music)