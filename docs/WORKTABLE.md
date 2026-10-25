# Worktable

Port of the GTNH Forestry worktable, without a Forestry dependency. The original
block faces, GUI and recipe lock textures are included under LGPLv3.

The worktable has 18 storage slots, a 3 by 3 ghost crafting grid, and nine saved
recipes. Left-click a grid slot with a held item to copy it into the pattern;
right-click to clear a slot. Setting a pattern does not consume the held item.
The small cross next to the grid clears the whole pattern.

Click the output to craft once. Shift-click crafts up to one stack at a time,
limited by available ingredients and inventory space. Number keys craft into
the corresponding hotbar slot, and the drop key crafts and drops one result.
Crafting fires Forge's crafting event and vanilla crafting achievements.
Buckets and reusable tools return to the selected ingredient inventory; excess
items drop at the player. Ghost ingredients and remembered outputs never drop.

The button on the left selects the ingredient source: `T` for table storage,
`P` for player inventory, or `+` for both. The default is table storage.
Equivalent ore-dictionary ingredients and reusable tools are accepted only
when the actual ingredients still produce the selected output. Arrows below
the output cycle between conflicting recipes.

Crafted recipes are remembered automatically. Left-click a remembered output
to restore its pattern; right-click to lock or unlock it. When memory is full,
the least recently used unlocked recipe is replaced. Locked recipes stay in
memory. Like Forestry, map recipes are not remembered. Saved patterns are
checked against registered recipes before crafting, including after a reload.

NEI is optional. Its recipe transfer button fills the ghost grid and selects
the requested output when that output is valid for the recipe. It does not
move or create ingredients.

Storage, patterns, remembered recipes, locks, selected outputs and ingredient
source are saved in the placed block. All viewers share the pattern and memory.
Hoppers access only the 18 real storage slots. Breaking the worktable drops its
stored items and the block; the dropped block does not retain recipe memory.

## Recipe

Place a book above a crafting table above a chest in a crafting grid. Wood
crafting tables and chests use their ore-dictionary entries when available,
with vanilla blocks as the fallback. This is Forestry's original recipe.

## Configuration

Worktables are enabled by default. Set `B:enableWorktable=false` in the `general`
section of `config/adventuraarcanacore.cfg` and restart to disable their block,
tile entity, network, NEI overlay and recipe registration. Use the same setting
on client and server. Disabling worktables in an existing world removes them
and their stored contents.

The block ID is `adventuraarcanacore:worktable`. Existing Forestry worktables
are not migrated.
