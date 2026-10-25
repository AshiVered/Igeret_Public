# Igeret

Igeret is a Telegram client that forked from the official Telegram source code.

Igeret is focused on digital minimalism and controlling your content.

In settings, after setting up a password, you can:

* Disable profile pictures
* Disable channels (and allow specific channels)
* Disable groups
* Disable 18+ content
* Strict privacy settings (can be changed through Privacy Settings)
* Block privacy settings changes (to prevent users from changing these settings without the knowledge of the "parent" — the person who set the password)
* Block adding devices (because if another device uses a different client, it shows all content without Igeret's restrictions)
* Disable bots and mini-apps

## To-Do List



- [x] 1. Block Telegram ads
- [ ] 2. Option to block stickers
- [x] 3. Change the splash screen to display the Igeret logo
- [ ] 4. Remove all help sections from the settings page
- [ ] 5. Block stories
- [ ] 6. Remove the 18+ content toggle from chat settings (already controlled in Control and Management settings)
- [x] 7. Remove the browser toggle from chat settings
- [x] 8. Block the built-in browser
- [x] 9. Write a sign-in and sign-up guide (regarding API keys) and add screenshots.
- [ ] 10. Write guide for langpacks
- [x] 11. Change all app icon options in chat settings to Igeret logo (locked to Igeret logo)
- [ ] 12. Option to block videos in groups and channels (blocking also in allowed channels)
- [ ] 13. Option to block videos in PMs

---

## User Guide: Setup, API Keys & Control Settings

### 1. Installing the APK on Android
Igeret is distributed directly as an Android Package file (`app.apk`):
1. **Download:** Transfer or download `app.apk` onto your Android device.
2. **Allow Unknown Sources:**
   - Tap the APK file to install. If prompted by Android security that your browser or file manager is not permitted to install apps, tap **Settings**.
   - Toggle **"Allow from this source"** ON.
3. **Install:** Tap **Install**, then tap **Open** once installation finishes.

### 2. Prerequisite: Creating a Telegram Account (Official Client)
> **Note:** This step is **only required for new users** who do not yet have an active Telegram account. If you already have a Telegram account, skip directly to Step 3.

Telegram does not dispatch initial registration SMS verification codes to third-party clients. Therefore:
1. Temporarily install the official Telegram app on your phone (or Telegram Desktop on your PC).
2. Register your phone number through the official app.
3. Keep the official app open to receive the login code for Igeret. Once logged in to Igeret, you may safely uninstall the official client.

### 3. Obtaining Telegram API Keys (API ID & API Hash)
Every standalone Telegram client requires developer API credentials to communicate with Telegram MTProto servers:
1. Open your browser and navigate to: **[https://my.telegram.org](https://my.telegram.org)**.
2. Enter your phone number in full international format (e.g., `+1...` or `+972...`) and click **Next**.
3. A confirmation code will be sent to your active Telegram chat. Copy the code, paste it into the **Confirmation code** box, and click **Sign In**.
4. In the main menu, click **API development tools**.
5. Fill in the short form:
   - **App title:** E.g., `Igeret App`
   - **Short name:** E.g., `igeretapp`
   - **Platform:** `Android`
6. Click **Create application**.
7. Note down your two credentials:
   - **`App api_id`**: A 7–8 digit number (e.g., `12345678`).
   - **`App api_hash`**: A 32-character hexadecimal string.

### 4. Launching Igeret & First Login
1. Open **Igeret**. On the very first launch, the **Telegram API Keys Setup** screen appears.
2. Enter your `API ID` and `API Hash`, then tap **Save & Continue**.
3. The standard Telegram phone login screen will now appear. Enter your phone number.
4. Input the verification code sent to your Telegram chat, followed by your 2-Step Verification password (if configured).
5. You are now logged in!

### 5. Control & Management Settings
Access via **Side Menu -> Settings -> Control and Management**:
- **Password Protection:** Set a master password to prevent unauthorized adjustments.
- **Disable Profile Photos:** Replaces all profile avatars with neutral placeholders.
- **Disable Channels:** Blocks access to all channel feeds, removing public browsing distractions.
- **Allowed Channels (Whitelist):** When channels are disabled, allows whitelisting up to 3 essential channels.
- **Disable Groups:** Blocks group chats, keeping only direct one-on-one private messaging.
- **Disable Sensitive Content (18+):** Blocks media flagged by Telegram as adult or sensitive.
- **Lock Privacy Settings:** Disallows changes to privacy settings to maintain strict visibility limits.
- **Block Adding Devices:** Hides connected sessions and blocks login notifications containing authentication codes, preventing unmonitored client logins.
- **Block Bots & Mini-Apps:** Prohibits launching interactive bots, web apps, and in-app games.
- **Telegram API Keys:** View or update your custom API credentials at any time.

---

From here, this is the original Telegram README:


### Telegram messenger for Android

[Telegram](https://telegram.org) is a messaging app with a focus on speed and security. It’s superfast, simple and free.
This repo contains the official source code for [Telegram App for Android](https://play.google.com/store/apps/details?id=org.telegram.messenger).

### Creating your Telegram Application

We welcome all developers to use our API and source code to create applications on our platform.
There are several things we require from **all developers** for the moment.

1. [**Obtain your own api_id**](https://core.telegram.org/api/obtaining_api_id) for your application.
2. Please **do not** use the name Telegram for your app — or make sure your users understand that it is unofficial.
3. Kindly **do not** use our standard logo (white paper plane in a blue circle) as your app's logo.
3. Please study our [**security guidelines**](https://core.telegram.org/mtproto/security_guidelines) and take good care of your users' data and privacy.
4. Please remember to publish **your** code too in order to comply with the licences.

#### API, Protocol documentation

Telegram API manuals: https://core.telegram.org/api

MTproto protocol manuals: https://core.telegram.org/mtproto

#### Compilation Guide

**Note**: In order to support [reproducible builds](https://core.telegram.org/reproducible-builds), this repo contains dummy release.keystore,  google-services.json and filled variables inside BuildVars.java. Before publishing your own APKs please make sure to replace all these files with your own.

You will require Android Studio 3.4, Android NDK rev. 20 and Android SDK 8.1

1. Download the Telegram source code from https://github.com/DrKLO/Telegram ( git clone https://github.com/DrKLO/Telegram.git )
2. Copy your release.keystore into TMessagesProj/config
3. Fill out RELEASE_KEY_PASSWORD, RELEASE_KEY_ALIAS, RELEASE_STORE_PASSWORD in gradle.properties to access your  release.keystore
4.  Go to https://console.firebase.google.com/, create two android apps with application IDs org.telegram.messenger and org.telegram.messenger.beta, turn on firebase messaging and download google-services.json, which should be copied to the same folder as TMessagesProj.
5. Open the project in the Studio (note that it should be opened, NOT imported).
6. Fill out values in TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java – there’s a link for each of the variables showing where and which data to obtain.
7. You are ready to compile Telegram.

#### Localization

We moved all translations to https://translations.telegram.org/en/android/. Please use it.

To download langpack and include it in your source code, please sea tools/langpack_downloader