#!/usr/bin/env python3
"""Validate the local, shape-only onboarding animations without Android/Gradle."""

import argparse
import json
import math
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
RAW = ROOT / "app/src/main/res/raw"
NAMES = ("sell", "scan", "receipt", "insights")
LIMIT = 25_000
REQUIRED = {"v", "fr", "ip", "op", "w", "h", "layers"}
SHAPES = {"gr", "tr", "rc", "el", "sh", "fl", "st", "tm"}


def walk(value, location="$", shape=False):
    if isinstance(value, dict):
        if isinstance(value.get("x"), str):
            raise ValueError(f"{location}: expression is forbidden")
        # Keyframe timestamps are numeric `t`; text-layer data is an object.
        if isinstance(value.get("t"), dict) or value.get("ty") == 5:
            raise ValueError(f"{location}: text is forbidden")
        if value.get("ty") in ("gf", "gs"):
            raise ValueError(f"{location}: gradient is forbidden")
        if shape and "ty" in value and value["ty"] not in SHAPES:
            raise ValueError(f"{location}: unsupported shape {value['ty']!r}")
        if {"v", "i", "o", "c"} <= value.keys():
            vertices = value["v"]
            if not vertices or not len(vertices) == len(value["i"]) == len(value["o"]):
                raise ValueError(f"{location}: path vertices/tangents must match")
            if any(not isinstance(p, list) or len(p) != 2 for key in ("v", "i", "o") for p in value[key]):
                raise ValueError(f"{location}: path coordinates must be pairs")
        for key, child in value.items():
            walk(child, f"{location}.{key}", shape or key in ("shapes", "it"))
    elif isinstance(value, list):
        for index, child in enumerate(value):
            walk(child, f"{location}[{index}]", shape)
    elif isinstance(value, float) and not math.isfinite(value):
        raise ValueError(f"{location}: non-finite number")


def check(path):
    size = path.stat().st_size
    if size >= LIMIT:
        raise ValueError(f"size {size:,} bytes must be below {LIMIT:,}")
    data = json.loads(path.read_text(encoding="utf-8"), parse_constant=lambda v: (_ for _ in ()).throw(ValueError(f"invalid number {v}")))
    if not isinstance(data, dict) or not REQUIRED <= data.keys():
        raise ValueError(f"missing required top-level keys: {sorted(REQUIRED - data.keys()) if isinstance(data, dict) else sorted(REQUIRED)}")
    version = tuple(int(part) for part in data["v"].split("."))
    if version < (5, 7):
        raise ValueError("Bodymovin version must be 5.7+")
    if data["fr"] != 60 or (data["w"], data["h"]) != (512, 512):
        raise ValueError("expected 60fps and 512 × 512 canvas")
    duration = (data["op"] - data["ip"]) / data["fr"]
    if not 2 <= duration <= 3:
        raise ValueError(f"duration {duration:g}s is outside 2–3s")
    if data.get("assets") or data.get("fonts") or data.get("chars"):
        raise ValueError("assets, images and fonts are forbidden; use local shape layers only")
    if not isinstance(data["layers"], list) or not data["layers"]:
        raise ValueError("layers must be a nonempty array")
    indices = set()
    for layer in data["layers"]:
        if layer.get("ty") != 4 or not layer.get("shapes") or not layer.get("ks"):
            raise ValueError("every layer must be a shape layer with transforms and geometry")
        if layer.get("ind") in indices:
            raise ValueError("layer indices must be unique")
        indices.add(layer.get("ind"))
    walk(data)
    return size, duration, len(data["layers"])


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("files", nargs="*", type=Path, help="optional files; defaults to all four onboarding resources")
    args = parser.parse_args()
    failed = False
    for path in args.files or [RAW / f"onboarding_{name}.json" for name in NAMES]:
        try:
            size, duration, layers = check(path)
            print(f"PASS {path.name}: {size:,} bytes, {duration:g}s, {layers} shape layers")
        except (OSError, ValueError, TypeError, KeyError) as error:
            failed = True
            print(f"FAIL {path.name}: {error}", file=sys.stderr)
    return int(failed)


if __name__ == "__main__":
    sys.exit(main())
