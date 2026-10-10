# Contributing to Kwentaro

Thanks for helping small shops count their sales. Contributions of all sizes are welcome: bug reports, translations (Tagalog, Bisaya and others), product illustrations, docs, and code.

## Getting started

1. Install JDK 17 and the Android SDK (API 36).
2. Fork and clone the repo, then build:
   ```bash
   ./gradlew assembleFossDebug        # fully open-source build (ZXing scanner)
   ./gradlew assembleFullDebug        # Google ML Kit scanner build
   ```
3. Run the tests before opening a pull request:
   ```bash
   ./gradlew testFullDebugUnitTest testFossDebugUnitTest
   python3 tools/check_templates.py   # product illustrations stay small and registered
   python3 tools/check_lottie.py      # onboarding animations stay valid
   ```

## Build flavors

| Flavor | Barcode engine | Proprietary code | Internet permission |
|---|---|---|---|
| `foss` | ZXing (Apache-2.0) | None | No |
| `full` | Google ML Kit (bundled) | ML Kit, Play Services basement | Yes (added by ML Kit telemetry) |

Code that is shared goes in `app/src/main`. Only the scanner engine differs per flavor (`app/src/{full,foss}/.../camera/BarcodeEngineImpl.kt`). **Do not add proprietary or tracking dependencies to `main`.** The `foss` build must stay buildable by F-Droid.

## Guidelines

- **Offline first.** Features must work without internet. Store data stays on the device unless the user explicitly exports it.
- **Per-account data.** Anything a shop records belongs to the signed-in account. Use `LocalSession` / `appViewModel { }` and never a global singleton.
- **Money is in centavos** (`Long`). Never use floating point for amounts.
- **Room schema changes** need a version bump plus an `AutoMigration` or `Migration`. Commit the generated JSON in `app/schemas/`.
- **UI** follows the solid Palengke theme in `ui/theme` and `docs/brand/README.md`, and must stay readable at WCAG AA.
- Match the surrounding code style (Kotlin official style, Compose). Keep pull requests focused, and include screenshots for UI changes.

## Versioning

The version lives in `gradle.properties` (`appVersion`). Maintainers bump it, update `CHANGELOG.md` and `fastlane/metadata/android/en-US/changelogs/<versionCode>.txt`, then tag `vX.Y.Z` to publish a release.

By contributing, you agree that your contributions are licensed under the MIT License, and you agree to follow the [Code of Conduct](CODE_OF_CONDUCT.md).
