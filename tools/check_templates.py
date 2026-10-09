#!/usr/bin/env python3
"""Validate the offline vector library and registry, without Android/Gradle."""

from collections import Counter
from pathlib import Path
import re
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
DRAWABLES = ROOT / "app/src/main/res/drawable"
REGISTRY = ROOT / "app/src/main/java/com/phcodesage/kwentaro/data/ProductTemplates.kt"
ANDROID = "{http://schemas.android.com/apk/res/android}"
FILE_BUDGET = 2_500
SET_BUDGET = 250_000
PALETTE = {
    "#0B5D4E", "#F2A541", "#C76B50", "#FBF7EF", "#83D5BD", "#D9A66A",
    "#795548", "#9277AA", "#C95845", "#F5DCA9", "#9EB4AF",
}


def main():
    errors = []
    files = sorted(DRAWABLES.glob("tpl_*.xml"))
    source = REGISTRY.read_text(encoding="utf-8")
    entries = re.findall(
        r'ProductTemplate\("([a-z0-9_]+)",\s*"([^"]+)",\s*"([^"]+)",\s*'
        r'R\.drawable\.(tpl_[a-z0-9_]+),\s*listOf\(([^\n]*)\)\)', source,
    )
    if len(files) < 100:
        errors.append(f"Expected at least 100 templates, found {len(files)}")
    for key, count in Counter(entry[0] for entry in entries).items():
        if count > 1:
            errors.append(f"Duplicate registry key: {key}")
    registered = {entry[3] for entry in entries}
    present = {path.stem for path in files}
    for missing in sorted(registered - present):
        errors.append(f"Missing drawable: {missing}")
    for unlisted in sorted(present - registered):
        errors.append(f"Drawable absent from registry: {unlisted}")
    for key, label, category, resource, keywords in entries:
        if resource != f"tpl_{key}":
            errors.append(f"Registry key/resource mismatch: {key} / {resource}")
        if not re.search(r'"[^"\n]+"', keywords):
            errors.append(f"No keywords: {key}")

    sizes = {}
    for path in files:
        sizes[path.name] = path.stat().st_size
        if sizes[path.name] > FILE_BUDGET:
            errors.append(f"{path.name}: {sizes[path.name]} bytes exceeds {FILE_BUDGET}")
        try:
            vector = ET.parse(path).getroot()
        except ET.ParseError as error:
            errors.append(f"{path.name}: invalid XML: {error}")
            continue
        if vector.tag != "vector":
            errors.append(f"{path.name}: root must be vector")
        for attribute, value in {
            "width": "48dp", "height": "48dp", "viewportWidth": "48", "viewportHeight": "48",
        }.items():
            if vector.get(ANDROID + attribute) != value:
                errors.append(f"{path.name}: {attribute} must be {value}")
        paths = list(vector)
        if not paths or any(child.tag != "path" for child in paths):
            errors.append(f"{path.name}: only simple filled paths are allowed")
        colors = set()
        for child in paths:
            color = child.get(ANDROID + "fillColor")
            colors.add(color)
            if color not in PALETTE:
                errors.append(f"{path.name}: fill outside the shared palette: {color}")
            if list(child):
                errors.append(f"{path.name}: gradients/filters/nested path elements are forbidden")
            data = child.get(ANDROID + "pathData", "")
            if not data or re.search(r"\d+\.\d{2,}", data):
                errors.append(f"{path.name}: empty path or coordinates exceed one decimal place")
        if not 2 <= len(colors) <= 4:
            errors.append(f"{path.name}: expected 2–4 fills, found {len(colors)}")

    total = sum(sizes.values())
    if total > SET_BUDGET:
        errors.append(f"Set: {total} bytes exceeds {SET_BUDGET}")
    if errors:
        print("Template validation FAILED:", file=sys.stderr)
        for error in errors:
            print(f"  {error}", file=sys.stderr)
        return 1
    largest = max(sizes, key=sizes.get)
    print(f"PASS: {len(files)} templates, all well-formed and registered")
    print(f"Total: {total:,} bytes ({total / 1000:.2f} KB / {SET_BUDGET / 1000:.0f} KB)")
    print(f"Largest: {largest}: {sizes[largest]:,} bytes ({FILE_BUDGET:,} byte limit)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
