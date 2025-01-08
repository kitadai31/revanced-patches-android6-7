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

### Avoid "Switch to YouTube.com"
In Feb 4, 2025, Google started to show the force update screen ("Update your app" / "Switch to YouTube.com") to old clients less than 19.01.xx.  
This means that the stock YouTube app is no longer usable on Android 6-7.

<img src="https://github.com/user-attachments/assets/ad3d5099-9e6a-4915-aa5b-04f1ee22477e" width="200">

However, RVX's "Disable update screen" patch can disable this update screen!  
By this patch, you can continue to use YouTube app on Android 6-7 for the next 4-5 months.

(This method will work until Google shuts down the old clients. After that, old clients become completely unavailable with [400] error. [Details](https://t.me/rvx_for_a6_7/73))

## 📣 Announcement of major update
In January 2025, the biggest update was released!  
Since then, this fork is **based on the latest RVX Patches.**

Previously, this patch was based on RVX v2.160.1 (released in Feb 13, 2023).  
v2.160.1 was the last version that works on 17.34.36.  
But it was too old and has fewer features than now.  
For example, the current RVX Patches contains a lot of new features that should also be available in 17.34.36, but this fork didn't have such features.  
So I moved to latest version of RVX Patches and added only minimal changes for 17.34.36.

**Almost all RVX features are available now!**  
And it will always follow the RVX's update!  
I keep the diff commits clean, so I will be able to follow the RVX updates easily by just rebase my diff commits.

## 📋 List of patches in this repository

{{ table }}

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
(`v6.20.51` for Android 5-6, `v6.42.55` for Android 7)

See [About YouTube Music](https://github.com/kitadai31/revanced-patches-android6-7/wiki/About-YouTube-Music)