# Changelog

## [2.3.0] - 2026-02-25

### Added
- An API jar for other mods to depend on. Currently, this is mainly just `BiomeApi`, which is used to read/write biomes
in REID format easier.
- Conversion of CubicChunks worlds from vanilla/NEID to REID.

### Changed
- REID's biomes are now stored in a `BiomeContainer`. Internally, this still uses an `int[]`, but may change in the 
future.
- Updated buildscript to RFG 2.0.2.

### Deprecated
- `INewChunk#getIntBiomeArray()` & `INewChunk#setIntBiomeArray()` for reading/writing biomes in REID format. No more
casting `(INewChunk) chunk`; mod-makers should use the `BiomeAPI` instead.

### Fixed
- Update compatibility for Scape and Run: Parasites 1.10.x; version 1.9.x is still supported until 1.10.x becomes stable.
- Update compatibility for Wyrms of Nyrus 0.8+.
- Handle CubicChunks 3D biomes correctly - worlds with 3D biomes should report their biomes correctly now depending on 
the y-level.