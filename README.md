<p align="center"><b>English</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://github.com/kitadai31/revanced-patches-android6-7/wiki/README_JP">日　本　語</a></p>

# YouTube ReVanced for Android 6.0-7.1
An unofficial ReVanced Extended for YouTube 17.34.36.  
Based on inotia00's ReVanced Extended [v2.160.1](https://github.com/inotia00/revanced-patches/releases/tag/v2.160.1)

## How to patch
See [[How to build]](https://github.com/kitadai31/revanced-patches/wiki/How-to-build) page in documentations.

Other informations are also avalable on [[documentations]](https://github.com/kitadai31/revanced-patches-android6-7/wiki) (GitHub Wiki).

## Overview
The last YouTube app available on Android 6 or 7 is **17.34.36**, but the latest ReVanced and ReVanced Extended are not compatible with 17.34.36.

However, until ReVanced Extended **v2.160.1**, most of patches worked properly, although not officially supported.

So I forked RVX 2.160.1 and released this unofficial (yet another) ReVanced Patches for YouTube 17.34.36.  
I fixed some issues in 2.160.1 and backported some features (**including protobuf-spoof**) from newer version.

Furthermore, I have successfully made ReVanced Manager work on A6-7.  
So you can build it as easily as regular ReVanced and without Termux!  
https://github.com/kitadai31/revanced-manager-android6-7

> However, due to stability and performance issues, if you have another newer device, you may want to use it.
>
> For this "revanced-patches-android6-7" I have checked that the patch completes successfully on my main smartphone (Snapdragon 410, 2GB RAM, Android 7.1.1), but it took **45 minutes** to complete.

## 🧩 Patches List

### [📦 `com.google.android.youtube`](https://play.google.com/store/apps/details?id=com.google.android.youtube)
<details>

| 💊 Patch | 📜 Description | 🏹 Target Version |
|:--------:|:--------------:|:-----------------:|
| `client-spoof` | Spoofs the YouTube client to prevent playback issues. | 17.34.36 |
| `custom-branding-icon-afn-blue` | Changes the YouTube launcher icon (Afn / Blue). | 17.34.36 |
| `custom-branding-icon-afn-red` | Changes the YouTube launcher icon (Afn / Red). | 17.34.36 |
| `custom-branding-icon-revancify` | Changes the YouTube launcher icon (Revancify). | 17.34.36 |
| `custom-branding-name` | Changes the YouTube launcher name to your choice (defaults to ReVanced Extended). | 17.34.36 |
| `custom-seekbar-color` | Change seekbar color in dark mode. | 17.34.36 |
| `custom-video-speed` | Adds more video speed options. | 17.34.36 |
| `default-video-quality` | Adds ability to set default video quality settings. | 17.34.36 |
| `default-video-speed` | Adds ability to set default video speed settings. | 17.34.36 |
| `disable-haptic-feedback` | Disable haptic feedback when swiping. | 17.34.36 |
| `disable-landscape-mode` | Disable landscape mode when entering fullscreen. | 17.34.36 |
| `enable-external-browser` | Use an external browser to open the url. | 17.34.36 |
| `enable-minimized-playback` | Enables minimized and background playback. | 17.34.36 |
| `enable-old-quality-layout` | Enables the original quality flyout menu. | 17.34.36 |
| `enable-open-links-directly` | Bypass URL redirects (youtube.com/redirect) when opening links in video descriptions. | 17.34.36 |
| `enable-seekbar-tapping` | Enables tap-to-seek on the seekbar of the video player. | 17.34.36 |
| `enable-tablet-miniplayer` | Enables the tablet mini player layout. | 17.34.36 |
| `enable-wide-searchbar` | Replaces the search icon with a wide search bar. This will hide the YouTube logo when active. | 17.34.36 |
| `force-premium-heading` | Forces premium heading on the home screen. | 17.34.36 |
| `force-vp9-codec` | Forces the VP9 codec for videos. | 17.34.36 |
| `header-switch` | Add switch to change header. | 17.34.36 |
| `hide-auto-captions` | Hide captions from being automatically enabled. | 17.34.36 |
| `hide-auto-player-popup-panels` | Hide automatic popup panels (playlist or live chat) on video player. | 17.34.36 |
| `hide-autoplay-button` | Hides the autoplay button in the video player. | 17.34.36 |
| `hide-button-container` | Adds the options to hide action buttons under a video. | 17.34.36 |
| `hide-cast-button` | Hides the cast button in the video player. | 17.34.36 |
| `hide-channel-watermark` | Hides creator's watermarks on videos. | 17.34.36 |
| `hide-comment-component` | Adds options to hide comment component under a video. | 17.34.36 |
| `hide-create-button` | Hides the create button in the navigation bar. | 17.34.36 |
| `hide-crowdfunding-box` | Hides the crowdfunding box between the player and video description. | 17.34.36 |
| `hide-email-address` | Hides the email address in the account switcher. | 17.34.36 |
| `hide-endscreen-cards` | Hides the suggested video cards at the end of a video in fullscreen. | 17.34.36 |
| `hide-endscreen-overlay` | Hide endscreen overlay on swipe controls. | 17.34.36 |
| `hide-filmstrip-overlay` | Hide flimstrip overlay on swipe controls. | 17.34.36 |
| `hide-firsttime-background-notification` | Disable notification when you launch background play for the first time. | 17.34.36 |
| `hide-flyout-panel` | Adds options to hide player settings flyout panel. | 17.34.36 |
| `hide-fullscreen-panels` | Hides video description and comments panel in fullscreen view. | 17.34.36 |
| `hide-general-ads` | Hooks the method which parses the bytes into a ComponentContext to filter components. | 17.34.36 |
| `hide-info-cards` | Hides info-cards in videos. | 17.34.36 |
| `hide-live-chat-button` | Hides the live chat button in the video player. | 17.34.36 |
| `hide-mix-playlists` | Removes mix playlists from home feed and video player. | 17.34.36 |
| `hide-next-prev-button` | Hides the next prev button in the player controller. | 17.34.36 |
| `hide-player-captions-button` | Hides the captions button in the video player. | 17.34.36 |
| `hide-player-overlay-filter` | Remove the dark filter layer from the player's background. | 17.34.36 |
| `hide-shorts-button` | Hides the shorts button in the navigation bar. | 17.34.36 |
| `hide-shorts-component` | Hides other Shorts components. | 17.34.36 |
| `hide-snackbar` | Hides the snackbar action popup. | 17.34.36 |
| `hide-startup-shorts-player` | Disables playing YouTube Shorts when launching YouTube. | 17.34.36 |
| `hide-stories` | Hides YouTube Stories shelf on the feed. | 17.34.36 |
| `hide-suggested-actions` | Hide the suggested actions bar inside the player. | 17.34.36 |
| `hide-time-and-seekbar` | Hides progress bar and time counter on videos. | 17.34.36 |
| `hide-tooltip-content` | Hides the tooltip box that appears on first install. | 17.34.36 |
| `hide-video-ads` | Removes ads in the video player. | 17.34.36 |
| `layout-switch` | Tricks the dpi to use some tablet/phone layouts. | 17.34.36 |
| `materialyou` | Enables MaterialYou theme for Android 12+ | 17.34.36 |
| `microg-support` | Allows YouTube ReVanced to run without root and under a different package name with Vanced MicroG. | 17.34.36 |
| `optimize-resource` | Removes duplicate resources and adds missing translation files from YouTube. | 17.34.36 |
| `os-version-check` | Check the Android version and show a warning if the device is Android 8.0 or higher. | 17.34.36 |
| `overlay-buttons` | Add overlay buttons for ReVanced Extended. | 17.34.36 |
| `patch-options` | Create an options.toml file. | 17.34.36 |
| `remove-player-button-background` | Removes the background from the video player buttons. | 17.34.36 |
| `return-youtube-dislike` | Shows the dislike count of videos using the Return YouTube Dislike API. | 17.34.36 |
| `settings` | Applies mandatory patches to implement ReVanced settings into the application. | 17.34.36 |
| `sponsorblock` | Integrates SponsorBlock which allows skipping video segments such as sponsored content. | 17.34.36 |
| `spoof-app-version` | Spoof the YouTube client version to enable the new layout or restore old layout. | 17.34.36 |
| `spoof-player-parameters` | Spoofs player parameters to prevent the endless buffering issue. | 17.34.36 |
| `swipe-controls` | Adds volume and brightness swipe controls. | 17.34.36 |
| `switch-create-notification` | Switching the create button and notification button. | 17.34.36 |
| `theme` | Applies a custom theme (default: amoled). | 17.34.36 |
| `translations` | Add Crowdin Translations. | 17.34.36 |
</details>

## Telegram <img height="24px" src="https://user-images.githubusercontent.com/13122796/178032213-faf25ab8-0bc3-4a94-a730-b524c96df124.png" />
Telegram channel was opened! (2023-06-17)  
RVX for Andorid 6-7 News  
https://t.me/rvx_for_a6_7

Updates and the latest information will be announced through this channel.  
You can view the channel even if you don't have Telegram account.

## Issue
For suggestions and bug reports, open an issue.

## About "Discussions" page
This repository uses [GitHub Discussions](https://github.com/kitadai31/revanced-patches-android6-7/discussions).  
It can be used for questions, supports, announcements, etc.

Also, Telegram group is available now (Started 2023-07-30)  
https://t.me/rvx_for_a6_7_chat

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