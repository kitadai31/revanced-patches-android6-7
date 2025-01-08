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

### This project is almost dead

#### Current situation

In Feb 4, 2025, Google started to show the force update screen ("Update your app" / "Switch to YouTube.com") to old clients less than 19.01.xx.  

<img src="https://github.com/user-attachments/assets/ad3d5099-9e6a-4915-aa5b-04f1ee22477e" width="200">

This screen can be removed with RVX's "Disable update screen" patch.

After that, in March 2025, Google shut down old clients completely.  
The home tab only shows "Switch to YouTube.com" screen, and all accesses from old clients are blocked with [400] error.

<img src="https://github.com/user-attachments/assets/d5b8e1c7-0825-4530-a0ba-60cc7966713a" width="200">

This usually means the complete death of YouTube 17.34.36.  
"Spoof app version" is not useful because spoofing to 19.xx will break the app.

#### However, a workaround exists!

Thankfully, the old server-side layout for 17.34.36 is still being served from YouTube's servers.  
This is our hope.  
"Fetching 17.34.36 layouts -> spoof app version to 19.01.34 -> freezing layout updates" is working as of May 13, 2025.

With our new patches `Spoof app version`, `Freeze layout updates`, and `Add missing resources`, YouTube 17.34.36 can still be available. (but half-broken)  
[See details](https://github.com/kitadai31/revanced-patches-android6-7/releases/tag/v5.4.1) 

We don't know how long this will work.  
If this no longer works, development will be discontinued and the repository will be archived.

## 📋 List of patches in this repository

{{ table }}

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