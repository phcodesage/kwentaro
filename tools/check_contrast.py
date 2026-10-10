#!/usr/bin/env python3
"""WCAG AA checks against the actual Kotlin palette, plus solid component fills."""

from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
SOURCE = (ROOT / "app/src/main/java/com/phcodesage/kwentaro/ui/theme/Theme.kt").read_text()
COLORS = {name: color for name, color in re.findall(r"(?:private )?val (\w+) = Color\(0xFF([\dA-F]{6})\)", SOURCE)}
COLORS.update({"Color.White": "FFFFFF", "Color.Black": "000000"})
SURFACES = ("background", "surface", "surfaceVariant", "surfaceDim", "surfaceBright", "surfaceContainerLowest", "surfaceContainerLow", "surfaceContainer", "surfaceContainerHigh", "surfaceContainerHighest")


def luminance(color):
    channels = [int(color[i:i + 2], 16) / 255 for i in (0, 2, 4)]
    linear = [v / 12.92 if v <= 0.04045 else ((v + 0.055) / 1.055) ** 2.4 for v in channels]
    return sum(v * weight for v, weight in zip(linear, (0.2126, 0.7152, 0.0722)))


def contrast(foreground, background):
    lo, hi = sorted((luminance(foreground), luminance(background)))
    return (hi + 0.05) / (lo + 0.05)


def scheme(name):
    block = SOURCE.split(f"private val {name}Colors = ", 1)[1].split("\n)", 1)[0]
    roles = {}
    for role, value in re.findall(r"^    (\w+) = ([^,\n]+)", block, re.M):
        match = re.fullmatch(r"Color\(0xFF([\dA-F]{6})\)", value)
        roles[role] = match[1] if match else COLORS[value]
    return roles


def main():
    failed = []
    results = []
    for name in ("Light", "Dark"):
        roles = scheme(name)
        pairs = [("on" + role[0].upper() + role[1:], role) for role in ("primary", "primaryContainer", "secondary", "secondaryContainer", "tertiary", "tertiaryContainer", "error", "errorContainer")]
        pairs += [("inverseOnSurface", "inverseSurface"), ("inversePrimary", "inverseSurface")]
        for family in ("primary", "secondary", "tertiary"):
            for suffix in ("Fixed", "FixedDim"):
                for on_suffix in ("Fixed", "FixedVariant"):
                    pairs.append(("on" + family.capitalize() + on_suffix, family + suffix))
        pairs += [(on, surface) for surface in SURFACES for on in ("onSurface", "onSurfaceVariant")]
        accent = "secondary" if name == "Dark" else "primary"
        pairs += [(accent, surface) for surface in SURFACES]
        pairs += [("tertiary", surface) for surface in SURFACES]
        pairs += [("error", surface) for surface in SURFACES]
        checked = []
        for foreground, background in pairs:
            ratio = contrast(roles[foreground], roles[background])
            checked.append((ratio, foreground, background))
            results.append((name, foreground, background, ratio))
            if ratio < 4.5:
                failed.append(f"{name}: {foreground} on {background}: {ratio:.2f}:1")
        ratio, foreground, background = min(checked)
        print(f"{name}: {len(checked)} text pairs; minimum {ratio:.2f}:1 ({foreground} on {background})")
    for label, foreground, background in (("Paper on jade", "Paper", "Jade"), ("Paper on deep jade", "Paper", "DeepJade"), ("Ink on mango", "Ink", "Mango"), ("Paper on clay", "Paper", "Clay"), ("Paper on ink", "Paper", "Ink"), ("Jade on paper CTA", "Jade", "Paper")):
        ratio = contrast(COLORS[foreground], COLORS[background])
        print(f"{label}: {ratio:.2f}:1")
        if ratio < 4.5:
            failed.append(f"{label}: {ratio:.2f}:1")
    for failure in failed:
        print("FAIL " + failure, file=sys.stderr)
    if not failed:
        print(f"PASS: {len(results) + 6} foreground/background checks meet 4.5:1.")
    return bool(failed)


if __name__ == "__main__":
    sys.exit(main())
