# Terralyze

A Kotlin-based parser for Terraria `.plr` player files. It reads the binary player data and converts it into structured models for further analysis and export.

### Version compatibility

The parser fully supports Terraria `.plr` files starting from **v230**.

Older versions are not fully supported because the player file format changed significantly over time, including different layouts and field availability:

- **Player gender / skin format (before v107)** — older versions store gender differently. The parser only supports the `skinVariant` format introduced in v107.
- **Armor layout (before v124)** — older versions use different slot counts and indexing. The parser uses the modern layout.
- **Dye layout (before v124)** — the number of dye slots changed over time (`3 → 8 → 10`). The parser uses the modern 10-slot layout.
- **Inventory layout (before v58)** — older versions contain 48 inventory slots instead of 58.
- **Builder accessory status (before v230)** — the number of entries and some migration logic changed across several versions. The parser uses the modern 12-entry layout.
- **Research data (before v218)** — research data was not present in the same form. The parser supports the research section used from v218 onward.

These limitations are intentional: maintaining every historical `.plr` layout would require supporting a number of legacy formats that are no longer relevant for modern Terraria files.