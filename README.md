<p align="center"><b>English</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://github.com/kitadai31/revanced-patches-android6-7/wiki/README_JP">日本語</a></p>

# YouTube ReVanced for Android 6.0-7.1
ReVanced Extended fork for YouTube 17.34.36. (unofficial)  
Based on inotia00's ReVanced Extended (RVX) [v2.160.1](https://github.com/inotia00/revanced-patches/releases/tag/v2.160.1)

## How to patch
See [[How to build]](https://github.com/kitadai31/revanced-patches-android6-7/wiki/How-to-build) page in documentations.

Other information is also available on [[documentations]](https://github.com/kitadai31/revanced-patches-android6-7/wiki) (GitHub Wiki).

## About
The last YouTube app available on Android 6 or 7 is **17.34.36**.

And ReVanced Extended **v2.160.1** was the last patch compatible with 17.34.36.

So I decided to fork RVX v2.160.1 to support YouTube 17.34.36.  
I fixed some issues for 17.34.36 and backported some features (**including endless buffering fix**) from newer RVX Patches.

Also, I released a fork ReVanced Manager for Android 5-7.  
https://github.com/kitadai31/revanced-manager-android5-7

## 🧩 Patches List

### [📦 `com.google.android.youtube`](https://play.google.com/store/apps/details?id=com.google.android.youtube)
<details>

Target version: 17.34.36

| 💊 Patch | 📜 Description | Default |
|:--------:|:--------------:|:-------:|
| `check-android-version` | If the device is Android 8.0 or higher, show a warning dialog. |  |
| `custom-branding-icon-afn-blue` | Changes the YouTube launcher icon (Afn / Blue). | No |
| `custom-branding-icon-afn-red` | Changes the YouTube launcher icon (Afn / Red). |  |
| `custom-branding-icon-revancify` | Changes the YouTube launcher icon (Revancify). | No |
| `custom-branding-name` | Changes the YouTube launcher name to your choice (defaults to ReVanced Extended). |  |
| `custom-seekbar-color` | Change seekbar color in video player and video thumbnails. Also adds an option to hide seekbar in video thumbnails. |  |
| `custom-video-speed` | Adds more video speed options. |  |
| `default-video-quality` | Adds ability to set default video quality settings. |  |
| `default-video-speed` | Adds ability to set default video speed settings. |  |
| `disable-haptic-feedback` | Disable haptic feedback when swiping. |  |
| `disable-landscape-mode` | Disable landscape mode when entering fullscreen. | No |
| `disable-update-screen` | Disables the "Update your app" screen. |  |
| `enable-external-browser` | Use an external browser to open the url. |  |
| `enable-minimized-playback` | Enables minimized and background playback. |  |
| `enable-old-quality-layout` | Enables the original quality flyout menu. |  |
| `enable-open-links-directly` | Bypass URL redirects (youtube.com/redirect) when opening links in video descriptions. |  |
| `enable-seekbar-tapping` | Enables tap-to-seek on the seekbar of the video player. |  |
| `enable-tablet-miniplayer` | Enables the tablet mini player layout. |  |
| `enable-wide-searchbar` | Replaces the search icon with a wide search bar. This will hide the YouTube logo when active. |  |
| `force-premium-heading` | Forces premium heading on the home screen. | No |
| `header-switch` | Add switch to change header. |  |
| `hide-auto-captions` | Hide captions from being automatically enabled. |  |
| `hide-auto-player-popup-panels` | Hide automatic popup panels (playlist or live chat) on video player. |  |
| `hide-autoplay-button` | Hides the autoplay button in the video player. |  |
| `hide-button-container` | Adds the options to hide action buttons under a video. |  |
| `hide-cast-button` | Hides the cast button in the video player. |  |
| `hide-channel-watermark` | Hides creator's watermarks on videos. |  |
| `hide-comment-component` | Adds options to hide comment component under a video. |  |
| `hide-create-button` | Hides the create button in the navigation bar. |  |
| `hide-crowdfunding-box` | Hides the crowdfunding box between the player and video description. |  |
| `hide-email-address` | Hides the email address in the account switcher. |  |
| `hide-endscreen-cards` | Hides the suggested video cards at the end of a video in fullscreen. |  |
| `hide-endscreen-overlay` | Hide endscreen overlay on swipe controls. |  |
| `hide-filmstrip-overlay` | Hide flimstrip overlay on swipe controls. |  |
| `hide-firsttime-background-notification` | Disable notification when you launch background play for the first time. |  |
| `hide-flyout-panel` | Adds options to hide player settings flyout panel. |  |
| `hide-fullscreen-panels` | Hides video description and comments panel in fullscreen view. |  |
| `hide-general-ads` | Hooks the method which parses the bytes into a ComponentContext to filter components. |  |
| `hide-info-cards` | Hides info-cards in videos. |  |
| `hide-live-chat-button` | Hides the live chat button in the video player. |  |
| `hide-mix-playlists` | Removes mix playlists from home feed and video player. |  |
| `hide-next-prev-button` | Hides the next prev button in the player controller. |  |
| `hide-player-captions-button` | Hides the captions button in the video player. |  |
| `hide-player-overlay-filter` | Remove the dark filter layer from the player's background. |  |
| `hide-shorts-button` | Hides the shorts button in the navigation bar. |  |
| `hide-shorts-component` | Hides other Shorts components. |  |
| `hide-snackbar` | Hides the snackbar action popup. |  |
| `hide-startup-shorts-player` | Disables playing YouTube Shorts when launching YouTube. |  |
| `hide-suggested-actions` | Hide the suggested actions bar inside the player. |  |
| `hide-time-and-seekbar` | Hides progress bar and time counter on videos. |  |
| `hide-tooltip-content` | Hides the tooltip box that appears on first install. |  |
| `hide-video-ads` | Removes ads in the video player. |  |
| `layout-switch` | Tricks the dpi to use some tablet/phone layouts. |  |
| `materialyou` | Enables MaterialYou theme for Android 12+ | No |
| `microg-support` | Allows YouTube ReVanced to run without root and under a different package name with GmsCore. ("GmsCore" is new Vanced MicroG). |  |
| `optimize-resource` | Removes duplicate resources and adds missing translation files from YouTube. |  |
| `overlay-buttons` | Add overlay buttons for ReVanced Extended. |  |
| `patch-options` | Create an options.toml file. |  |
| `remove-player-button-background` | Removes the background from the video player buttons. |  |
| `return-youtube-dislike` | Shows the dislike count of videos using the Return YouTube Dislike API. |  |
| `settings` | Applies mandatory patches to implement ReVanced settings into the application. |  |
| `shorts-seek` | Replace search and menu button in Shorts player with skip/rewind button. |  |
| `sponsorblock` | Integrates SponsorBlock which allows skipping video segments such as sponsored content. |  |
| `spoof-app-version` | Spoof the YouTube client version to enable the new layout or restore old layout. |  |
| `spoof-streaming-data` | Adds options to spoof the streaming data to allow video playback. |  |
| `swipe-controls` | Adds volume and brightness swipe controls. |  |
| `switch-create-notification` | Adds an option to swap create button and notification button. This also removes all ads, including Shorts ads. |  |
| `theme` | Applies a custom theme (default: amoled). |  |
| `translations` | Add Crowdin Translations. |  |
</details>

## Telegram <img height="24px" src="https://user-images.githubusercontent.com/13122796/178032213-faf25ab8-0bc3-4a94-a730-b524c96df124.png" />
[![TelegramChannel](https://img.shields.io/badge/Telegram_news_channel-2CA5E0?style=for-the-badge&logo=Telegram&logoColor=white)](https://t.me/rvx_for_a6_7)
[![TelegramChat](https://img.shields.io/badge/Telegram_chat_group-2CA5E0?style=for-the-badge&logo=Telegram&logoColor=white)](https://t.me/rvx_for_a6_7_chat)

Check the Telegram channel for the latest announcements!

\* We also have [GitHub Discussions](https://github.com/kitadai31/revanced-patches-android6-7/discussions)

## About YouTube Music
This fork doesn't have patches for YT Music.

For YT Music, use inotia00's official RVX Patches.  
It supports old YT Music versions for Android 5.0-7.1.  
(YT Music `v6.20.51` for Android 5-6, `v6.42.55` for Android 7.x)

See [About YouTube Music](https://github.com/kitadai31/revanced-patches-android6-7/wiki/About-YouTube-Music)

## Warning: Do not use this on Android 8.0+!

Sometimes I see people trying to use this fork on newer Androids believing that YouTube 17.34.36 has better performance.  
However, this is incorrect.  
This patch is based on the old code base of RVX and is poorly optimized. (Especially the ad filter and network stack of SponsorBlock & RYD.)  
This means that this fork may have potential performance issues.

Therefore, I do not recommend using this on Android 8.0 or above devices.

I also see people using this fork to get old UI design, but this is also wrong.

## Recommended ways:

### For performance:

- Patch the official RVX Patches to YouTube `18.29.38`

18.29.38 is the oldest version supported by the inotia00's official RVX Patches.  
This is a sufficiently old version, and is expected to have same performance as 17.34.36.

Note: If you are using RVX Manager, to select a 18.29.38 apk, turn off the "Require suggested app version" setting.

### For old UI:

- Patch YouTube `18.29.38`, and set "Spoof app version" setting to `17.33.42`

YouTube app UI is server-side, so you can get the old design by spoofing app version, without using the actual old versions.  
17.33.42 is the oldest working version and has the same design as 17.34.36.

This way, you get both the old design and the stability of the latest patches.

Tips: Follow this [instructions](https://github.com/kitadai31/revanced-patches-android6-7/wiki/Spoof-app-version-information#-how-to-reflect-version-spoofing-immediately) to reflect version spoofing.

## Todo
~~I would like to quit developing based on 2.160.1 and remake it based on the latest RVX. (for well maintainability🔧, updated translation🌍 and new features✨)~~ it's difficult for me  
<sub>please someone recreate from scratch based on the latest RVX</sub>
