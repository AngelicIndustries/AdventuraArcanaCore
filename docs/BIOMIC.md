# Biomic integration

Biomic is optional. When it is installed, `compat/biomic/BiomicIntegration` adds the pack's choices to Biomic's worlds
through Biomic's API (`industries.angelic.biomic.api`); nothing here loads without it. Each addition has a switch in
the `biomic` category of `config/adventuraarcanacore.cfg` and needs the mods it names.

Biomic's API is not published yet. To build, run `VERSION=0.0.1-debloat ./gradlew publishToMavenLocal` in Biomic
first; the dependency is `compileOnly`, so it is never part of AAC's own dependencies.

| Switch | What it does | Needs |
|---|---|---|
| `pmpIvyForVines` | Vanilla's vine becomes Plant Mega Pack's ivy with the same faces, whoever writes it: Swedish ivy where it is hot and wet, Japanese ivy elsewhere (Biomic's `BlockSubstitutes`). | Plant Mega Pack |
| `wildFoodInBiomic` | HarvestCraft's food grows wild in the habitats `food.json` names (an overlay of Biomic's palettes): crop patches, the wild food's bushes, fruit groves of HarvestCraft's trees and the pod trees, fruiting vines (Biomic's `PlantLayer`). HarvestCraft's own gardens, fruit trees, hives and salt stand down in Biomic's worlds (`ForeignGenerators`), and so does Natura's overworld pass of wild barley, cotton and berries (`naturaBerriesStandDown` keeps its berry bushes out too). | HarvestCraft |
| `pmpBambooInBiomic` | Origen's bamboo grows as Plant Mega Pack's species (`bamboo-leaf-decor.json`, an overlay of Biomic's leaf decor): Moso by default, Tropical Blue in the sinkhole's jungle, Giant Timber in the fossilized fenlands. Growthcraft's bamboo stands down. | Plant Mega Pack |
| `madakeInBiomic` | Regions Unexplored's bamboo tree is Madake and grows in both of the Bamboo Forest's tree groups (`madake.json`, an overlay of Biomic's modern features); without it the second group grows Et Futurum's bamboo. | |
| `bambooShootsInBiomic` | Each bamboo's shoot grows wild where the bamboo grows (`shoots.json`), and HarvestCraft's generic shoot crop becomes a Moso shoot when a chunk generates (`BlockSubstitutes`). | HarvestCraft |
| `floraInBiomic` | Other mods' plants in Biomic's habitats (`flora.json`, an overlay of Biomic's palettes): Plant Mega Pack's above all, BOP's, SaltyMod's and Natura's, by habitat and tier; epiphytes and tree mushrooms on trunks, vines, immersed and floating plants; the flower patches that replace Origen's vanilla ones; the sky islands' plants. Plant Mega Pack's own generator stands down. | each plant's mod |
| `leafDecorInBiomic` | Origen's leaf decor in BOP's and Plant Mega Pack's plants (`flora-leaf-decor.json`, an overlay of Biomic's leaf decor): their shrubs in bush clumps, ivy and flowering vines on cliffs and under ceilings, willow and moss on walls. | each plant's mod |
| `decorInBiomic` | BOP's and Plant Mega Pack's blocks in Biomic's own cave and sea decoration (`decor.json`, an overlay of Biomic's decor slots): moss and ivy on cave walls, glowshrooms, stone formations, bones and miner's delight in caves; Plant Mega Pack's giant kelp, eelgrass, sea plants, fresh-water plants and corals in the seas, in place of Biomic's ported seagrass and algae. | each block's mod |
| `netherFoodInBiomic` | Harvest the Nether grows wild in Tartarus (`nether.json`, Biomic's Nether plant layer): its crops in patches on nether ground by biome, glow flowers over Soul Valley's soul sand, ignis trees about one per six chunks of their biomes. Its own gardens and trees stand down. | Harvest the Nether |
| `beeNestsInBiomic` | Et Futurum's bee nests hang on trunk sides under the canopy, about one chunk in eight of the habitats that have nests, with two or three bees inside; SaltyMod's nests stand down there (`saltyModBeeNests`). | Et Futurum Requiem |

These moved here from Biomic, which keeps what a player with Biomic and that mod alone would expect.
