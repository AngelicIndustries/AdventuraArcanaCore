# Wild food

Natural forms of HarvestCraft crops that have none of their own, registered only with HarvestCraft 1.3.13-GTNH
(`enableWildFood`):

* Wild berry bush: bayberry, wild cotton, cranberry and raspberry, drawn as Plant Mega Pack's berry bush in their
  colours (HarvestCraft's own crop without PMP). Right-click picks the fruit; the bare bush fruits again in light.
* Fruit pods: coffee cherries, curry berries, bay berries and tea flowers, hanging under leaves. They ripen in three
  stages; a ripe pod picked by hand starts again. HarvestCraft's fruit-tree generator grows their trees.
* Fruiting vines: wild grapevine and kiwi vine, on tree trunks. Picking turns a vine bare; it fruits again in time.
  With Growthcraft Grapes (`growthcraftGrapes`), Growthcraft's grape is the only grape: the wild grapevine gives
  Growthcraft's grapes and grape seeds, and NEI hides HarvestCraft's grape, grape seeds and grape crop. HarvestCraft's
  recipes take Growthcraft's grapes, which are `cropGrape` too.

Bamboo shoots (`enableBambooShoots`), one for each bamboo: Plant Mega Pack's nine species and Madake, in place of
HarvestCraft's one generic shoot crop. A shoot has eight stages; picked by hand when grown, it drops shoot items
(`cropBambooshoot`, which plant it again) and starts over; left alone, it turns into its bamboo after about half an
hour (a PMP stalk, or with Biomic Madake's tree). Its colour is its bamboo's culm.

The textures are painted when the block atlas is stitched (`util.PaintedSprite`), from PMP's, HarvestCraft's and
vanilla's own textures, so AAC ships none of their art.

`foodLinks` makes other mods' wild plants drop HarvestCraft food (`food_links.json`): BOP's rice, sunflowers' seeds,
PMP's lettuce and others, each with a chance of the seed. With `pmpHarvestCraftBerries`, PMP's berry bushes give
HarvestCraft's berries in place of their own.

With Biomic, all of it grows wild in Biomic's habitats (`docs/BIOMIC.md`). The blocks came from Biomic: Biomic remaps
its old names to them, so worlds made before keep them.

## Toast

With Cooking for Blockheads and HarvestCraft installed, `patches.toasterBread` changes the toaster recipes:

* Vanilla bread gives HarvestCraft toast.
* Plant Mega Pack corn bread gives Corn Bread Toast.
* SaltyMod salted bread gives Salted Toast.
* Netherlicious abyssal bread gives Abyssal Toast.

Each variant is available only with its source mod. Corn Bread Toast and Abyssal Toast use HarvestCraft toast's
shape and shading, recoloured from the source bread. Salted Toast keeps the regular toast texture with added
salt flecks. The variants inherit HarvestCraft toast's
food values and register as `foodToast`.

Cooking for Blockheads' duplicate toast is hidden from creative tabs and NEI, and toaster recipes that produced
it give HarvestCraft toast instead. Furnace and crafting recipes producing these plain toasts are removed;
recipes for dishes made with toast remain. Other toaster recipes are preserved.

NEI shows the four bread recipes and accepts the toaster as a recipe catalyst. The toaster takes two pieces of
bread at once and cooks them in three seconds. Right-click with bread to load it, then right-click with an empty
hand to start.

The switch defaults to on and requires a restart. Disabling it, or omitting Cooking for Blockheads or HarvestCraft,
keeps the original recipes and adds no toast variants.

## Kitchen

With Cooking for Blockheads installed:

* `patches.kitchenSneakTake`: sneak-use the oven's top with an empty hand to take the cookware under the cursor back
  into your hand. Where there is none, the oven opens as before. Sneak-use the toaster with an empty hand to take its
  bread back out, which also stops it. The bread tops up the stacks you carry first, as picking it up would, then
  goes into your hand. An empty toaster starts and stops as before. Use the same setting on client and server.
* `patches.toasterLever`: the toaster's lever goes down over a few ticks when it starts, lowering the bread with it,
  and pops back up when it stops. Each move has a sound, which the server plays for everyone nearby: when it finishes,
  is stopped by hand or has its bread taken out. The toaster saves and shares whether it runs, which Cooking for
  Blockheads kept to the side that clicked it: other players never saw the lever go down, and a reload stopped it with
  the bread inside. The lever pops up when the server's toaster stops, together with its sound, rather than on the
  client's own count, which ran ahead of a busy server. Each toaster also shows its own lever; they used to show the
  one drawn before.
* `patches.toasterNoEating`: putting bread into the toaster ends the click there. The toaster used to report the click
  as unused, so a hungry player also started eating the bread. Using a full toaster with bread it can toast does
  nothing either. Use the same setting on client and server.
* `patches.toasterSaltedBread` (with SaltyMod): salted bread is drawn in the toaster without the salt particles in the
  top left of its texture. They lie outside the loaf, so in the slot they hung in the air beside it. Client only.
* `patches.recipeListScroll`: the mouse wheel scrolls the recipe book's and cooking table's recipe list only over the
  list or its scrollbar. It used to scroll it anywhere on the screen, including over the inventory and NEI.

Each switch defaults to on and requires a restart.
