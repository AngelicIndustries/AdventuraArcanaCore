# Wild food

Natural forms of HarvestCraft crops that have none of their own, registered only with HarvestCraft 1.3.13-GTNH
(`enableWildFood`):

* Wild berry bush: bayberry, wild cotton, cranberry and raspberry, drawn as Plant Mega Pack's berry bush in their
  colours (HarvestCraft's own crop without PMP). Right-click picks the fruit; the bare bush fruits again in light.
* Fruit pods: coffee cherries, curry berries, bay berries and tea flowers, hanging under leaves. They ripen in three
  stages; a ripe pod picked by hand starts again. HarvestCraft's fruit-tree generator grows their trees.
* Fruiting vines: wild grapevine and kiwi vine, on tree trunks. Picking turns a vine bare; it fruits again in time.

The textures are painted when the block atlas is stitched (`util.PaintedSprite`), from PMP's, HarvestCraft's and
vanilla's own textures, so AAC ships none of their art.

`foodLinks` makes other mods' wild plants drop HarvestCraft food (`food_links.json`): BOP's rice, sunflowers' seeds,
PMP's lettuce and others, each with a chance of the seed. With `pmpHarvestCraftBerries`, PMP's berry bushes give
HarvestCraft's berries in place of their own.

With Biomic, all of it grows wild in Biomic's habitats (`docs/BIOMIC.md`). The blocks came from Biomic: Biomic remaps
its old names to them, so worlds made before keep them.
