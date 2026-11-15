# ExerciseRecord Android project

A native Android starter app written in Kotlin + Jetpack Compose.

## Implemented in this first build
- Month calendar UI with month navigation and selectable dates
- Add, edit, and delete exercise categories
- Per-category icon selection
- Per-category independent color selection
- Persist exercise category names, icons, and colors across app restarts using SharedPreferences

## Build APK locally
1. Install Android Studio (current stable) and JDK 17.
2. Open this folder (`ExerciseRecord`) in Android Studio.
3. Install Android SDK Platform 35 when prompted.
4. Wait for Gradle sync.
5. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. APK output: `app/build/outputs/apk/debug/app-debug.apk`.

The project uses Android Gradle Plugin 8.7.3 and Gradle 8.9.

## Build APK using GitHub Actions
This project includes `.github/workflows/build-apk.yml`.
1. Create a GitHub repository.
2. Upload the contents of this folder to the repository's `main` branch.
3. Open the repository's **Actions** tab.
4. Run **Build Android APK** (or let it run after pushing to `main`).
5. Open the successful workflow run and download the `ExerciseRecord-debug-apk` artifact.
6. Extract the artifact ZIP; it contains `app-debug.apk`.

## Important scope notes
This is a fresh starter implementation, not a binary patch of the uploaded APK. The current prototype includes the calendar screen and editable color-coded exercise categories. Full workout logging (sets, duration, photos, statistics), import of the old app's database, and installation/device testing have not yet been implemented or verified.

## Build environment note
The workspace used to prepare this project did not have Android SDK, Gradle, Kotlin compiler, or APK signing tools installed. Therefore an APK could not be compiled or installation-tested in this session.
