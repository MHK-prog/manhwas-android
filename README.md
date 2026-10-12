# Reading Progress Tracker

An offline-first Android and web app for tracking reading progress. The Android version wraps the local web interface in a WebView and stores user data on-device.

## Features

- Add reading entries with a title, chapter count, and current progress.
- Tap any card to open its chapter checklist.
- Long-press a card to select items. The toolbar then offers edit and delete; editing is disabled when multiple items are selected.
- Change a cover image from the edit dialog.
- Choose a storage folder and export a backup.
- Use the app offline after installation.

The bundled Vazirmatn font is licensed under SIL OFL 1.1; its license is in `fonts/OFL.txt`.

## Build an Android APK

Push changes to `main`, or start the **Build signed Android APK** workflow from GitHub Actions. After a successful run, download the APK artifact and extract it from the ZIP.

The workflow signs the release with the repository's configured signing secrets and verifies the APK signature before upload.

## Local build

With Android SDK and JDK 17 or newer:

```powershell
.\gradlew.bat --no-daemon :app:assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.
