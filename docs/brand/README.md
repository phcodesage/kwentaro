# Kwentaro brand and Palengke theme

![Kwentaro horizontal wordmark](logo-wordmark.svg)

## The tally K

Kwentaro comes from **kwenta**, to count or tally. The mark turns that idea into three bold strokes: a paper upright with a receipt tear at its foot, a mango counting stroke, and a paper lower stroke. Together they read as K. The detached strokes and horizontal gap give the silhouette its identity at small sizes; the color adds warmth without carrying the meaning alone.

Deep jade anchors the mark, warm paper recalls a receipt, and ripe mango marks a completed count. The rounded outer terminals feel approachable at a neighborhood shop; the straight inner edges keep the mark crisp. The wordmark is original rounded geometric lettering drawn as SVG paths. It needs no installed font or external asset.

### Assets and usage

- [logo.svg](logo.svg): 1024 × 1024 full-color mark on an unrounded jade background. Let the destination apply its own icon mask.
- [logo-wordmark.svg](logo-wordmark.svg): 1248 × 320 horizontal lockup on warm paper; all lettering is vector geometry.
- `app/src/main/res/drawable/ic_launcher_foreground.xml`: full-color adaptive foreground on a 108dp canvas.
- `app/src/main/res/drawable/ic_launcher_monochrome.xml`: the same geometry in one opaque color. Android supplies the themed icon tint; the background is a separate adaptive layer.
- `app/src/main/res/drawable/ic_kwentaro_mark.xml`: a cropped 72-unit viewport for a 48dp Compose badge in the NavigationRail header, on the jade brand background.

The launcher geometry stays inside the **66dp safe circle**, centered at (54, 54) on the 108dp canvas. Its furthest point is 31.89dp from the center, below the 33dp limit. The mark was inspected at a 48dp launcher size in full color and as light/dark monochrome silhouettes. Keep the receipt notch, the gap between the two arms, and the gap beside the upright intact. For standalone use, reserve at least one upright's width as clear space around the bare mark; do not compress the wordmark horizontally.

The manifest points both `android:icon` and `android:roundIcon` to `@mipmap/ic_launcher`. With **minSdk 26**, the `mipmap-anydpi-v26` adaptive resource covers every supported device, so no PNG or older raster fallback is needed. The monochrome layer is available to launchers that support themed icons. All brand assets are local; there are no network font or image requests.

## Palengke palette

Jade is the action fill. Mango is the accent for selected controls, quantities, and money moments. Warm paper is the working canvas; deep ink and red clay give the dashboard a bold, distinct rhythm.

### Solid is the principle

Use **opaque, flat color blocks**. Top bars and the Register header are jade, navigation is jade with a mango indicator, selected chips are mango, and unselected chips are filled neutrals. Product cards use contrasting solid surfaces with zero elevation; selected register cards become jade with paper text. Cart bars are mango; Charge and Complete sale are jade. Insights uses jade, mango, clay (`#9C4934`), and ink (`#20251F`) stat tiles. No gradients, translucent decoration, or elevation tint. The existing camera and modal scrims remain for focus and legibility.

Dark mode uses deep jade (`#08483D`) action fills and deep ink/green surfaces, with paper on dark fills. **Do not use dark primary as text on dark surfaces**: `actionTextColor` uses light jade (`#83D5BD`, the dark secondary role) for amounts, text actions, and focused field labels. Always use the matching on-color on a solid container. Low stock uses both a label and color so it remains understandable on selected cards.

These are the **default brand schemes** in `ui/theme/Theme.kt`; all 48 Material 3 1.4 roles are explicit. The optional wallpaper setting retains dynamic neutral surfaces while brand action, selection, and error-container colors stay solid. Fixed roles are also solid brand fills in both modes.

| Material 3 role | Light | Dark |
| --- | --- | --- |
| `primary` | `#0B5D4E` | `#08483D` |
| `onPrimary` | `#FFFFFF` | `#FBF7EF` |
| `primaryContainer` | `#0B5D4E` | `#0B5D4E` |
| `onPrimaryContainer` | `#FBF7EF` | `#FBF7EF` |
| `inversePrimary` | `#83D5BD` | `#0B5D4E` |
| `secondary` | `#526458` | `#83D5BD` |
| `onSecondary` | `#FFFFFF` | `#243629` |
| `secondaryContainer` | `#283D33` | `#123B31` |
| `onSecondaryContainer` | `#FBF7EF` | `#FBF7EF` |
| `tertiary` | `#855000` | `#F2A541` |
| `onTertiary` | `#FFFFFF` | `#422900` |
| `tertiaryContainer` | `#F2A541` | `#F2A541` |
| `onTertiaryContainer` | `#382000` | `#382000` |
| `background` | `#FBF7EF` | `#101E19` |
| `onBackground` | `#20251F` | `#FBF7EF` |
| `surface` | `#FBF7EF` | `#101E19` |
| `onSurface` | `#20251F` | `#FBF7EF` |
| `surfaceVariant` | `#EAE5D9` | `#283D33` |
| `onSurfaceVariant` | `#50564D` | `#C3CBBE` |
| `surfaceTint` | `#0B5D4E` | `#08483D` |
| `inverseSurface` | `#2D332C` | `#E6E6DC` |
| `inverseOnSurface` | `#F5F1E7` | `#2D332C` |
| `error` | `#A33226` | `#FFB4A6` |
| `onError` | `#FFFFFF` | `#60190F` |
| `errorContainer` | `#A33226` | `#A33226` |
| `onErrorContainer` | `#FBF7EF` | `#FBF7EF` |
| `outline` | `#6B7267` | `#8D9788` |
| `outlineVariant` | `#CCC7B9` | `#41493F` |
| `scrim` | `#000000` | `#000000` |
| `surfaceDim` | `#DED9CE` | `#101E19` |
| `surfaceBright` | `#FBF7EF` | `#283D33` |
| `surfaceContainerLowest` | `#FFFFFF` | `#0B1511` |
| `surfaceContainerLow` | `#F5F1E7` | `#14271F` |
| `surfaceContainer` | `#EFEADF` | `#192F26` |
| `surfaceContainerHigh` | `#E9E4D8` | `#20372C` |
| `surfaceContainerHighest` | `#E3DED2` | `#283D33` |
| `primaryFixed` | `#0B5D4E` | `#0B5D4E` |
| `primaryFixedDim` | `#08483D` | `#08483D` |
| `onPrimaryFixed` | `#FBF7EF` | `#FBF7EF` |
| `onPrimaryFixedVariant` | `#FBF7EF` | `#FBF7EF` |
| `secondaryFixed` | `#20251F` | `#20251F` |
| `secondaryFixedDim` | `#101E19` | `#101E19` |
| `onSecondaryFixed` | `#FBF7EF` | `#FBF7EF` |
| `onSecondaryFixedVariant` | `#FBF7EF` | `#FBF7EF` |
| `tertiaryFixed` | `#F2A541` | `#F2A541` |
| `tertiaryFixedDim` | `#D68B29` | `#D68B29` |
| `onTertiaryFixed` | `#2A1800` | `#2A1800` |
| `onTertiaryFixedVariant` | `#382000` | `#382000` |

### Contrast and application

Run `python3 tools/check_contrast.py` to check the Kotlin palette directly. **150 text foreground/background pairs pass WCAG AA at 4.5:1**: 72 per appearance plus six brand/onboarding combinations. This covers on-colors, fixed and inverse roles, both surface text colors on all ten surface levels, and action/error/accent text on every neutral level. The light minimum is **4.75:1** (tertiary on surfaceDim); the dark minimum is **5.52:1** (onTertiaryFixedVariant on tertiaryFixedDim).

| Solid pairing | Contrast |
| --- | ---: |
| Paper on jade | 7.31:1 |
| Paper on deep jade | 9.80:1 |
| Ink on mango | 7.60:1 |
| Paper on clay | 5.75:1 |
| Paper on ink | 14.60:1 |
| Jade on paper CTA | 7.31:1 |

Mango is a fill, not body text on paper or jade. Use `onTertiaryContainer` for chip, navigation indicator, badge, and cart text. Disabled controls use opaque neutral fills; disabled text and user-selected dynamic neutral palettes are outside the measured brand-palette guarantee. Icons and labels on jade top/navigation bars use the matching onPrimary; selected navigation icons use the dark mango on-color.

`SolidSystemBars` in the Compose theme file sets status/navigation icon appearance from the actual screen background through WindowCompat. Dark bars use light icons; the mango intro page uses dark icons. It targets the current dialog window when appropriate, so the full-screen camera retains black bars with light icons. System contrast scrims are disabled; Compose paints the opaque background beneath edge-to-edge system bars. The Register's tablet cart also paints jade under the status inset. The XML launch/window backgrounds use jade by day and deep jade at night, with light system icons.

## First-run intro

Four original, hand-authored shape-only Bodymovin 5.7.4 animations live in `app/src/main/res/raw/onboarding_{sell,scan,receipt,insights}.json`. Each uses a **512 × 512 canvas, 60fps, and a seamless 2.5-second loop** with jade, mango, paper, and clay geometry. There are no images, text layers, expressions, fonts, downloaded animations, or external assets. Layer and group stacking follows Bodymovin's front-to-back order. The printing receipt uses an animated local mask to keep the printing window fixed while the paper slides out.

`OnboardingScreen` uses a four-page HorizontalPager and lottie-compose 6.7.1. The pages say “Benta in seconds”, “Scan, tap, tapos”, “Resibo na agad”, and “Know your kita”, with jade, mango, clay, and ink backgrounds. The illustration sits on a flat paper panel; swiping uses a small scale transition and an animated pill indicator. A brand-mark fallback covers both loading and failure. When the system animator duration scale is zero, the final frame is static and Next/dots switch without animation. The setting is observed live.

After the last page, Get started opens “Set up your store”, prefilling the current store name and currency (default ₱). Saving writes those two fields and `onboarding_done` in one DataStore edit; existing tax, receipt, and appearance preferences are preserved. Skip writes only completion and retains the current settings. Errors keep the intro open for retry. Settings → About → **Replay intro** clears completion. Older installations without the key see the intro once.

The Activity waits for the first real settings emission before choosing the intro or register, using a solid jade loading surface. Pages resize their illustrations and can scroll on short screens or large fonts; tablets use an illustration/text row. Setup scrolls with the keyboard and respects safe drawing insets.

Validate resource structure, paths, timing, forbidden content, and the strict **<25,000-byte** budget with `python3 tools/check_lottie.py`. All four also parsed without warnings through the cached Lottie Android 6.7.1 parser in a local JVM harness using Android geometry/interpolator stubs. That parser check does not validate Android drawing; confirm rendering on the emulator.

## Type scale

The app keeps the Android **system sans serif** and respects the system font scale. No font files are bundled. Displays use semibold weight and tighter tracking; screen headlines use bold or semibold; titles and control labels use semibold. Body text stays regular with modest positive tracking for scanning product names, receipt details, and settings.

| Style | Size / line height (sp) | Weight | Tracking (sp) |
| --- | --- | --- | --- |
| `displayLarge` | 57 / 64 | 600 | -1 |
| `displayMedium` | 45 / 52 | 600 | -0.75 |
| `displaySmall` | 36 / 44 | 600 | -0.5 |
| `headlineLarge` | 32 / 40 | 700 | -0.5 |
| `headlineMedium` | 28 / 36 | 700 | -0.4 |
| `headlineSmall` | 24 / 32 | 600 | -0.25 |
| `titleLarge` | 22 / 28 | 600 | -0.2 |
| `titleMedium` | 16 / 24 | 600 | 0.1 |
| `titleSmall` | 14 / 20 | 600 | 0.1 |
| `bodyLarge` | 16 / 24 | 400 | 0.1 |
| `bodyMedium` | 14 / 20 | 400 | 0.15 |
| `bodySmall` | 12 / 16 | 400 | 0.2 |
| `labelLarge` | 14 / 20 | 600 | 0.1 |
| `labelMedium` | 12 / 16 | 600 | 0.2 |
| `labelSmall` | 11 / 16 | 600 | 0.3 |

`MoneyStyle` uses `FontFamily.Monospace`, weight 600, zero tracking, and the `tnum` OpenType feature. Merge it **after** the size style, for example `MaterialTheme.typography.titleLarge.merge(MoneyStyle)`, so the system sans family cannot overwrite the tabular money font. Standalone `MoneyStyle` text inherits its size and line height from the surrounding content style.

Products, Sales, Insights, and Settings use compact pinned `TopAppBar` headers, including on tablets, to leave more room for the shop's working content. Checkout totals still use the display scale, receipt totals the title scale, and product prices the surrounding body scale. The NavigationRail gets the 48dp brand badge instead of a text K.

## Validation and review

The original SVG and Android launcher geometry is unchanged. All Android resources were compiled/linked directly with SDK 36 tools, and all Kotlin sources compiled with cached Kotlin 2.3.20, the Compose compiler plugin, Compose 1.9.4, Material 3 1.4.0, and Lottie 6.7.1. The offline classpath used cached coroutines 1.9.0 for the unavailable 1.10.2 transitive binaries. **Gradle was not run.** Full dependency resolution, packaging, and device verification remain with the maintainer: review first launch and relaunch, Skip/setup/Replay intro, reduced motion, animation fallback, 480 × 800 phones, tablets, the keyboard and large fonts, both appearance modes and wallpaper neutrals, selected/low-stock cards, checkout, and camera/dialog system icons. Database, POS calculations, and versioning are unchanged.
