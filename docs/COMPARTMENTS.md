# Compartments

Port of Binnie's compartment storage, with no Binnie or Forestry dependency.
The original model and six textures are included under LGPLv3.

Compartments are enabled by default. To disable them, set `B:enableCompartments=false`
in the `general` section of `config/adventuraarcanacore.cfg` and restart.
Use the same setting on client and server. This skips block, tile entity, GUI,
network, renderer and recipe registration. Disabling them in an existing world
removes its compartments and their stored contents.

| Tier | Tabs | Slots per tab | Total slots |
| --- | ---: | ---: | ---: |
| Wood | 4 | 25 | 100 |
| Copper | 6 | 25 | 150 |
| Bronze | 8 | 25 | 200 |
| Iron | 4 | 50 | 200 |
| Gold | 6 | 50 | 300 |
| Diamond | 8 | 50 | 400 |

Right-click to open. The tabs on either side select a page. Shift-click moves
items into the selected tab. Each viewer selects tabs independently.

The controls below the slots rename the selected tab, copy the held item's
icon without consuming it, and choose one of sixteen colors. Press Enter or
click outside the name field to save. Clicking the icon with an empty cursor
restores the paper icon.

Search scans all tabs. It supports case-insensitive names, separate item and
block filters, alphabetical sorting, and page buttons or the mouse wheel.
Search results use the actual storage slots and support normal inventory actions.
Shift-clicking from the player inventory still inserts into the selected tab.

Hoppers and other inventory automation can access all slots. Breaking a
compartment drops its contents and the block separately, as in Binnie. Tab
settings are saved in the placed block, not in the dropped item.

## Recipes

All recipes use `pcp / cbc / pcp`: `b` is the center, `c` the four edge items,
and `p` the four corners.

| Output | Center | Edges | Corners |
| --- | --- | --- | --- |
| Wood | Book | Chests | Stone buttons |
| Copper | Wood compartment | Copper gears or ingots | Stone buttons |
| Bronze | Copper compartment | Bronze gears or ingots | Gold nuggets |
| Iron | Copper compartment | Iron gears or ingots | Gold nuggets |
| Gold | Iron compartment | Gold gears or ingots | Emeralds |
| Diamond | Gold compartment | Diamonds | Emeralds |

Recipes use ore-dictionary gears when present, otherwise ingots. If copper or
bronze is absent, copper compartments use iron ingots and bronze compartments
use gold ingots. This keeps every tier craftable without adding a metal mod.
These fallbacks are the recipe changes from Binnie.

The block is registered as `adventuraarcanacore:compartment`, metadata 0 through 5
in the order above. This is a new block; existing Binnie storage is not migrated.
