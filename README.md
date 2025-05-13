# YouTube ReVanced for Android 6.0-7.1
ReVanced Extended fork for YouTube 17.34.36.  
A fork of inotia00's [ReVanced Extended](https://github.com/inotia00/revanced-patches) (RVX) Patches.

## How to patch
See [[How to build]](https://github.com/kitadai31/revanced-patches-android6-7/wiki/How-to-build) page in documentations.

Other information is also available on [[documentations]](https://github.com/kitadai31/revanced-patches-android6-7/wiki) (GitHub Wiki).

## About
The last YouTube app that supports Android 6-7 is **17.34.36**. (or 17.34.35)  
However, the latest ReVanced/RVX Patches doesn't support YouTube 17.34.36.

By using this fork, you can patch YouTube 17.34.36, and you can use YouTube ReVanced on Android 6 or 7.

This fork is based on the latest RVX Patches, and I added minimal changes to support 17.34.36.

## Warning: This project is almost dead!

### Current situation

In Feb 4, 2025, Google started to show the force update screen ("Update your app" / "Switch to YouTube.com") to old clients less than 19.01.xx.  

<img src="https://github.com/user-attachments/assets/ad3d5099-9e6a-4915-aa5b-04f1ee22477e" width="200">

This screen can be removed with RVX's "Disable update screen" patch.

After that, in March 2025, Google shut down old clients completely.  
The home tab only shows "Switch to YouTube.com" screen, and all accesses from old clients are blocked with [400] error.

<img src="https://github.com/user-attachments/assets/d5b8e1c7-0825-4530-a0ba-60cc7966713a" width="200">

This usually means the complete death of YouTube 17.34.36.  
"Spoof app version" is not useful because spoofing to 19.xx will break the app.

### However, a workaround exists!

Thankfully, the old server-side layout for 17.34.36 is still being served from YouTube's servers.  
This is our hope.  
"Fetching 17.34.36 layouts -> spoof app version to 19.01.34 -> freezing layout updates" is working as of May 13, 2025.

With our new patches `Spoof app version`, `Freeze layout updates`, and `Add missing resources`, YouTube 17.34.36 can still be available. (but half-broken)  
[See details](https://github.com/kitadai31/revanced-patches-android6-7/releases/tag/v5.6.2) 

We don't know how long this will work.  
If this no longer works, development will be discontinued and the repository will be archived.

## 📋 List of patches in this repository

### [📦 `com.google.android.youtube`](https://play.google.com/store/apps/details?id=com.google.android.youtube)
<details>

| 💊 Patch | 📜 Description | 🏹 Target Version |
|:--------:|:--------------:|:-----------------:|
| `Add missing resources` | Add missing resources to prevent crashes in 17.34.36. Currently, only a few resources are supported. Also, replace missing drawables with a transparent image to prevent crashes. | 17.34.36 |
| `Alternative thumbnails` | Adds options to replace video thumbnails using the DeArrow API or image captures from the video. | 17.34.36 |
| `Bypass URL redirects` | Adds an option to bypass URL redirects and open the original URL directly. | 17.34.36 |
| `Bypass image region restrictions` | Adds an option to use a different host for static images, so that images blocked in some countries can be received. | 17.34.36 |
| `Change form factor` | Adds an option to change the UI appearance to a phone, tablet, or automotive device. | 17.34.36 |
| `Change live ring click action` | Adds an option to open the channel instead of the live stream when clicking on the live ring. | 17.34.36 |
| `Change start page` | Adds an option to set which page the app opens in instead of the homepage. | 17.34.36 |
| `Custom Shorts action buttons` | Changes, at compile time, the icon of the action buttons of the Shorts player. | 17.34.36 |
| `Custom branding icon for YouTube` | Changes the YouTube app icon to the icon specified in patch options. | 17.34.36 |
| `Custom branding name for YouTube` | Changes the YouTube app name to the name specified in patch options. | 17.34.36 |
| `Custom double tap length` | Adds Double-tap to seek values that are specified in patch options. | 17.34.36 |
| `Custom header for YouTube` | Applies a custom header in the top left corner within the app. | 17.34.36 |
| `Description components` | Adds options to hide and disable description components. | 17.34.36 |
| `Disable QUIC protocol` | Adds an option to disable CronetEngine's QUIC protocol. | 17.34.36 |
| `Disable forced auto audio tracks` | Adds an option to disable audio tracks from being automatically enabled. | 17.34.36 |
| `Disable forced auto captions` | Adds an option to disable captions from being automatically enabled. | 17.34.36 |
| `Disable haptic feedback` | Adds options to disable haptic feedback when swiping in the video player. | 17.34.36 |
| `Disable resuming Shorts on startup` | Adds an option to disable the Shorts player from resuming on app startup when Shorts were last being watched. | 17.34.36 |
| `Disable update screen` | Disable the force update screen ("Switch to YouTube.com" or "Update your app") | 17.34.36 |
| `Enable OPUS codec` | Adds an option to enable the OPUS audio codec if the player response includes it. | 17.34.36 |
| `Enable debug logging` | Adds an option to enable debug logging. | 17.34.36 |
| `Force hide player buttons background` | Removes, at compile time, the dark background surrounding the video player controls. | 17.34.36 |
| `Freeze layout updates` | Freeze the current server-side layout. Mandatory for 17.34.36. | 17.34.36 |
| `Fullscreen components` | Adds options to hide or change components related to fullscreen. | 17.34.36 |
| `GmsCore support` | Allows patched Google apps to run without root and under a different package name by using GmsCore instead of Google Play Services. | 17.34.36 |
| `Hide Shorts dimming` | Removes, at compile time, the dimming effect at the top and bottom of Shorts videos. | 17.34.36 |
| `Hide accessibility controls dialog` | Removes, at compile time, accessibility controls dialog 'Turn on accessibility controls for the video player?'. | 17.34.36 |
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
| `MaterialYou` | Applies the MaterialYou theme for Android 12+ devices. | 17.34.36 |
| `Miniplayer` | Adds options to change the in-app minimized player, and if patching target 19.16+ adds options to use modern miniplayers. | 17.34.36 |
| `Navigation bar components` | Adds options to hide or change components related to the navigation bar. | 17.34.36 |
| `Open links externally` | Adds an option to always open links in your browser instead of the in-app browser. | 17.34.36 |
| `Overlay buttons` | Adds options to display useful overlay buttons in the video player. | 17.34.36 |
| `Player components` | Adds options to hide or change components related to the video player. | 17.34.36 |
| `Remove background playback restrictions` | Removes restrictions on background playback, including for music and kids videos. | 17.34.36 |
| `Remove viewer discretion dialog` | Adds an option to remove the dialog that appears when opening a video that has been age-restricted by accepting it automatically. This does not bypass the age restriction. | 17.34.36 |
| `Return YouTube Dislike` | Adds an option to show the dislike count of videos using the Return YouTube Dislike API. | 17.34.36 |
| `Return YouTube Username` | Adds an option to replace YouTube handles with usernames in comments using YouTube Data API v3. | 17.34.36 |
| `Sanitize sharing links` | Adds an option to sanitize sharing links by removing tracking query parameters. | 17.34.36 |
| `Seekbar components` | Adds options to hide or change components related to the seekbar. | 17.34.36 |
| `Settings for YouTube` | Applies mandatory patches to implement ReVanced Extended settings into the application. | 17.34.36 |
| `Shorts components` | Adds options to hide or change components related to YouTube Shorts. | 17.34.36 |
| `Shorts seek` | Adds an option to replace toolbar buttons in Shorts player with skip/rewind buttons. | 17.34.36 |
| `Snack bar components` | Adds options to hide or change components related to the snack bar. | 17.34.36 |
| `SponsorBlock` | Adds options to enable and configure SponsorBlock, which can skip undesired video segments, such as sponsored content. | 17.34.36 |
| `Spoof app version` | Adds options to spoof the YouTube client version. This can be used to restore old UI elements and features. | 17.34.36 |
| `Spoof streaming data` | Adds options to spoof the streaming data to allow playback. | 17.34.36 |
| `Swipe controls` | Adds options for controlling volume and brightness with swiping, and whether to enter fullscreen when swiping down below the player. | 17.34.36 |
| `Theme` | Changes the app's themes to the values specified in patch options. | 17.34.36 |
| `Toolbar components` | Adds options to hide or change components located on the toolbar, such as the search bar, header, and toolbar buttons. | 17.34.36 |
| `Translations for YouTube` | Add translations or remove string resources. | 17.34.36 |
| `Video playback` | Adds options to customize settings related to video playback, such as default video quality and playback speed. | 17.34.36 |
| `Visual preferences icons for YouTube` | Adds icons to specific preferences in the settings. | 17.34.36 |
| `Watch history` | Adds an option to change the domain of the watch history or check its status. | 17.34.36 |
</details>


## Telegram <img height="24px" src="https://user-images.githubusercontent.com/13122796/178032213-faf25ab8-0bc3-4a94-a730-b524c96df124.png" />
[![TelegramChannel](https://img.shields.io/badge/Telegram_news_channel-2CA5E0?style=for-the-badge&logo=Telegram&logoColor=white)](https://t.me/rvx_for_a6_7)
[![TelegramChat](https://img.shields.io/badge/Telegram_chat_group-2CA5E0?style=for-the-badge&logo=Telegram&logoColor=white)](https://t.me/rvx_for_a6_7_chat)

Check the Telegram channel for the latest announcements!

Join the Telegram chat for discussions.  
(We also have [GitHub Discussions](https://github.com/kitadai31/revanced-patches-android6-7/discussions))

## About YouTube Music
YT Music is still available on Android 5.0 and above.

This fork doesn't have patches for YT Music.

For YT Music, use inotia00's official RVX Patches.  
It supports old YT Music versions for Android 5.0-7.1.  
(`v6.20.51` for Android 5-6, `v6.42.55` for Android 7)

See [About YouTube Music](https://github.com/kitadai31/revanced-patches-android6-7/wiki/About-YouTube-Music)
