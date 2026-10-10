# Changelog

All notable changes to Kwentaro are documented here. Versions follow [Semantic Versioning](https://semver.org); the version lives in `gradle.properties` (`appVersion`).

## [1.2.0]

- Offline accounts: sign up / sign in without internet. Each account has its own database and settings, so records never mix. Salted PBKDF2 password hashes, a one-time recovery code for offline resets, change password, switch and delete account. The first account adopts existing store data.
- New `foss` build variant: ZXing scanner, no proprietary code, no internet permission (F-Droid ready). `full` keeps ML Kit.
- Open-source project files: CONTRIBUTING, Code of Conduct, Security policy, issue/PR templates, F-Droid fastlane metadata

## [1.1.0]

- Onboarding: four original Lottie intro animations (sell, scan, receipt, insights) with a store setup step; shown on first launch, replayable from Settings → Replay intro; respects reduced motion
- Bold "solid" theme: solid jade bars and navigation with mango accents, filled chips, solid CTAs and stat tiles, flat cards; light and dark (all text WCAG AA)

## [1.0.0]

First release.

- Register with product grid, search, category filters, cart and discounts
- Camera barcode scanning (CameraX + bundled ML Kit) and in-app product photos
- Checkout with cash/card/e-wallet, quick-tender and live change
- VAT inclusive/exclusive tax, receipts with share and refund
- Inventory with low-stock alerts; sales insights dashboard
- 126 built-in product illustrations with a searchable picker and name-based auto-suggest
- Kwentaro logo, themed launcher icon, Palengke light/dark Material 3 theme
