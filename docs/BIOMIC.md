# Biomic integration

Biomic is optional. When it is installed, `compat/biomic/BiomicIntegration` adds the pack's choices to Biomic's worlds
through Biomic's API (`org.fentanylsolutions.biomic.api`); nothing here loads without it. Each addition has a switch in
the `biomic` category of `config/adventuraarcanacore.cfg` and needs the mods it names.

Biomic's API is not published yet. To build, run `VERSION=0.0.1-debloat ./gradlew publishToMavenLocal` in Biomic
first; the dependency is `compileOnly`, so it is never part of AAC's own dependencies.

| Switch | What it does | Needs |
|---|---|---|
| `pmpIvyForVines` | Vanilla's vine becomes Plant Mega Pack's ivy with the same faces, whoever writes it: Swedish ivy where it is hot and wet, Japanese ivy elsewhere (Biomic's `BlockSubstitutes`). | Plant Mega Pack |
| `wildFoodInBiomic` | HarvestCraft's food grows wild in the habitats `food.json` names (an overlay of Biomic's palettes): crop patches, the wild food's bushes, fruit groves of HarvestCraft's trees and the pod trees, fruiting vines (Biomic's `PlantLayer`). HarvestCraft's own gardens, fruit trees, hives and salt stand down in Biomic's worlds (`ForeignGenerators`). | HarvestCraft |
| `beeNestsInBiomic` | Et Futurum's bee nests hang on trunk sides under the canopy, about one chunk in eight of the habitats that have nests, with two or three bees inside; SaltyMod's nests stand down there (`saltyModBeeNests`). | Et Futurum Requiem |

These moved here from Biomic, which keeps what a player with Biomic and that mod alone would expect.
