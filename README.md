<p align="center"><b>English</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://github.com/kitadai31/revanced-patches-android6-7/wiki/README_JP">日　本　語</a></p>

# YouTube ReVanced for Android 6.0-7.1
ReVanced Extended fork for YouTube 17.34.36. (unofficial)  
Based on inotia00's ReVanced Extended (RVX) [v2.160.1](https://github.com/inotia00/revanced-patches/releases/tag/v2.160.1)

## How to patch
See [[How to build]](https://github.com/kitadai31/revanced-patches-android6-7/wiki/How-to-build) page in documentations.

Other informations are also available on [[documentations]](https://github.com/kitadai31/revanced-patches-android6-7/wiki) (GitHub Wiki).

## About
The last YouTube app available on Android 6 or 7 is **17.34.36**.

And ReVanced Extended **v2.160.1** was the last patch compatible with 17.34.36.

So I decided to fork RVX v2.160.1 to support YouTube 17.34.36.  
I fixed some issues for 17.34.36 and backported some features (**including endless buffering fix**) from newer patch.

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
| `custom-seekbar-color` | Change seekbar color in dark mode. |  |
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
| `force-vp9-codec` | Forces the VP9 codec for videos. |  |
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
| `spoof-client` | Adds options to spoofs the client to allow video playback. |  |
| `swipe-controls` | Adds volume and brightness swipe controls. |  |
| `switch-create-notification` | Switching the create button and notification button. |  |
| `theme` | Applies a custom theme (default: amoled). |  |
| `translations` | Add Crowdin Translations. |  |
</details>

## Telegram <img height="24px" src="https://user-images.githubusercontent.com/13122796/178032213-faf25ab8-0bc3-4a94-a730-b524c96df124.png" />
[![TelegramChannel](https://img.shields.io/badge/Telegram_news_channel-2CA5E0?style=for-the-badge&logo=Telegram&logoColor=white)](https://t.me/rvx_for_a6_7)
[![TelegramChat](https://img.shields.io/badge/Telegram_chat_group-2CA5E0?style=for-the-badge&logo=Telegram&logoColor=white)](https://t.me/rvx_for_a6_7_chat)

※ We also have [GitHub Discussions](https://github.com/kitadai31/revanced-patches-android6-7/discussions)

## Issue
For suggestions and bug reports, open an issue.

## Todo
~~I would like to quit developing based on 2.160.1 and remake it based on the latest RVX. (for well maintainability🔧, updated translation🌍 and new features✨)~~ it's difficult for me

## I have skill issue...
Embarrassingly, I'm not very familiar with reverse engineering and also programming. (But I can read a little smali :D )  
So I can not create new patches by myself. All my modifying are merely copy and paste.

If anyone else is interested in developing a ReVanced Patches for YouTube 17.34.36 besides me, please don't hesitate to release it!  
If a more excellent ReVanced Patches for YouTube 17.34.36 is emerged, I will immediately stop releasing my one and introduce yours!

Also all contributions are welcomed!

## Credits
[@inotia00](https://github.com/inotia00)  
[@revanced](https://github.com/revanced)

## Android 5
Are you looking for ReVanced for Android 5.0 (YouTube 16.40.36)?  
There is another fork by d4n3436.  
https://github.com/d4n3436/revanced-patches-android5
