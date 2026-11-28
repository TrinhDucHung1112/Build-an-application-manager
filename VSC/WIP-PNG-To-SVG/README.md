# PNG → SVG

Convert a **PNG to SVG literally** — the SVG is a complete, faithful
replica of the image, at its **original resolution**.  Two tracing back-ends,
both **full-colour with no posterization**:

* **Exact (rectangles, default)** — every colour region becomes a crisp
  flat-filled `<rect>` with `shape-rendering="crispEdges"` and pure integer
  coordinates.  Pixel-perfect.
* **Vector (paths)** — every colour region is traced as smooth polygon
  paths with `fill-rule="evenodd"` (holes and transparency correct).

Every distinct **RGBA** colour of the PNG is preserved exactly (optional
`--tolerance N` merges near-identical colours into their average instead);
semi-transparent pixels keep their true alpha via `fill-opacity`, and fully
transparent pixels are simply skipped.

**Dependencies:** Python 3.8+ and Pillow.  Installing **numpy** (`pip install
numpy` or `pip install -e ".[fast]"`) enables a compiled fast path for colour
classification; without it a pure-Python path with identical results is used.

## Run

From this directory (no installation needed):

```bash
python3 -m png2svg           # GUI
python3 -m png2svg in.png    # CLI  → writes in.svg
```

Or install a `png2svg` command:

```bash
pip install -e .     # or: pip install -e ".[fast]"  for the numpy fast path
png2svg                     # GUI
png2svg in.png out.svg --mode vector
```

## Command line

| Option | Meaning |
|---|---|
| `--mode exact|vector` | `exact` = crisp rectangles (default), `vector` = smooth paths |
| `--tolerance N` | merge colours within N per channel (0 = literal, default) |
| `--smooth D` | vector: polygon simplification in px (default 0 = none) |
| `--min-area N` | vector: drop regions smaller than N px² (default 0 = keep all) |
| `--max-size N` | downscale the long edge to N px **before** tracing. Default: original resolution (full quality) |
| `--square` | centre content on a 1:1 square canvas (default: exact image size) |

## GUI

```bash
python3 -m png2svg
```

* **File → Open** (`Ctrl+O`), or type/paste a path — the preview refreshes
  instantly on a transparency-checker background, together with the image
  dimensions.
* Pick a mode and options, then **Convert** (`Ctrl+R` or `Enter`).
* **Save SVG As…** (`Ctrl+S`), **Open result**, **Open output folder**
  (`Ctrl+D`), plus a **Recent files** menu.
* The status bar warns whenever a non-default trace size would reduce
  quality, and shows a progress bar while tracing.

## Quality notes

* The default keeps **the original resolution** (`max_size=None`) and
  **every original colour** — the SVG replicates the PNG literally.
* Exact mode only ever emits **integer pixel coordinates** and sets
  `shape-rendering="crispEdges"`, so zooming in shows crisp pixels instead of
  soft, anti-aliased rectangle edges.
* Transparency is handled three ways: fully transparent pixels are skipped,
  semi-transparent (anti-aliased) pixels keep their alpha via
  `fill-opacity`, and your browser's transparency checkers render through
  transparent areas.
* Browsers may smooth-scale an SVG when shown smaller than its native size —
  press `Ctrl+0` for 100% to judge the real quality.

## Project layout

```
png2svg/
  __init__.py    public API + version
  __main__.py    `python -m png2svg`
  core.py        shared helpers, numpy/pure colour classification + convert_file
  exact.py       pixel-exact rectangle tracer
  vector.py      literal full-colour path tracer (crack edges + RDP simplify)
  cli.py         command-line interface
  gui.py         tkinter GUI
tests/           self-tests
```

## Tests

```bash
python3 -m unittest discover -s tests -v
```
