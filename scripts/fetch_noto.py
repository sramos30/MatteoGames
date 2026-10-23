#!/usr/bin/env python3
"""
Build-time helper: download Noto Emoji PNGs (the same Noto drawings Literki uses) for every
emoji the app draws, into app/src/main/assets/noto/emoji_u<hex>.png. Runtime stays offline.

Sources of emojis:
  * every string literal in the Kotlin sources made only of emoji characters (UI glyphs);
  * scripts/literki-words-emoji.json: the emoji of Literki's 133 words, used as fallback
    pictures while/if the xx.images SVGs are not available.

FireOS (Android 4.4) has no color emoji font; the app never draws emoji as text.
Noto Emoji images: Apache License 2.0 (https://github.com/googlefonts/noto-emoji).
"""
import glob
import json
import os
import re
import urllib.request

BASE = "https://cdn.jsdelivr.net/gh/googlefonts/noto-emoji@v2.047/png/128"  # pinned release
OUT = "app/src/main/assets/noto"


def is_emoji_char(c):
    o = ord(c)
    return o >= 0x2190 or o in (0x200D, 0xFE0F, 0x20E3)


def collect():
    found = set(json.load(open("scripts/literki-words-emoji.json", encoding="utf-8")))
    lit = re.compile(r'"([^"\\]{1,16})"')
    for path in glob.glob("app/src/main/java/**/*.kt", recursive=True):
        with open(path, encoding="utf-8") as f:
            for m in lit.finditer(f.read()):
                s = m.group(1)
                if s and all(is_emoji_char(c) for c in s) and any(ord(c) > 0x2190 for c in s):
                    found.add(s)
    return found


def key_for(emoji):
    return "_".join(f"{ord(c):04x}" for c in emoji if ord(c) != 0xFE0F)


def main():
    os.makedirs(OUT, exist_ok=True)
    ok, missing = 0, []
    for e in sorted(collect()):
        k = key_for(e)
        dest = os.path.join(OUT, f"emoji_u{k}.png")
        if os.path.exists(dest):
            ok += 1
            continue
        try:
            req = urllib.request.Request(f"{BASE}/emoji_u{k}.png", headers={"User-Agent": "matteo-games"})
            with urllib.request.urlopen(req, timeout=20) as r:
                data = r.read()
            with open(dest, "wb") as out:
                out.write(data)
            ok += 1
        except Exception:
            missing.append((e, k))
    print(f"noto: {ok} ok, {len(missing)} missing")
    for e, k in missing:
        print(f"  missing: {e!r} ({k})")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
