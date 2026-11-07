# Adding pack patches

All registrations live in `core/Mixins.java`, which implements GTNHMixins' `IMixins`.
`core/TargetMod.java` implements `ITargetMod` for optional targets.
The builders and loader interfaces come from UniMixins; no separate GTNHMixins or AngelLib jar is needed.

## Vanilla patches

Put the class in `mixins/early/minecraft/`. Add an enum entry before the semicolon in `Mixins`:

```java
VANILLA_PATCH(new MixinBuilder("Describe the pack change")
    .setPhase(Phase.EARLY)
    .addCommonMixins("minecraft.MixinSomeClass")),
```

Names are relative to the JSON package (`industries.angelic.adventuraarcanacore.mixins.early`).
Replace the illustrative names with real mixin classes. Use `addClientMixins` for client classes,
`addServerMixins` for dedicated-server classes, and `addCommonMixins` for both physical sides.
Gameplay server logic also runs on the integrated server, so it normally belongs in common mixins.

## Optional mod patches

Add a target to `TargetMod` using the exact, case-sensitive Forge mod ID:

```java
SOME_MOD(new TargetModBuilder().setModId("SomeMod")),
```

Put its classes in `mixins/late/somemod/` and register them in `Mixins`:

```java
SOME_MOD_PATCH(new MixinBuilder("Describe the integration")
    .setPhase(Phase.LATE)
    .addRequiredMod(TargetMod.SOME_MOD)
    .addCommonMixins("somemod.MixinSomeClass")),
```

Use `compileOnly("group:artifact:version:dev")` in `dependencies.gradle` if the source imports target
classes. Optional local runtime jars can go in the ignored `run/client/mods/` and `run/server/mods/`
directories. Do not add the target as a required mod or published runtime dependency.

Use `remap = false` on mixins/injections targeting unobfuscated mod members, while keeping remapping
for Minecraft members. Prefer `@Accessor` and `@Invoker` over access transformers.

For a target coremod that must be patched early, use
`new TargetModBuilder().setCoreModClass("some.mod.LoadingPlugin")` and `Phase.EARLY`.
The early loader uses the supplied coremod set; the late loader uses the supplied mod-ID set.
Do not call `Loader.isModLoaded` or load target classes during early discovery.

## Conditions and exclusions

A builder can use `.addExcludedMod(TargetMod.CONFLICTING_MOD)` or
`.setApplyIf(() -> yourBootstrapFlag)`. Conditions run before Forge `preInit`, including for late
mixins. The normal `Config` class is loaded in `preInit`, so it is too late to decide which mixins load.
Every patch has a switch in the `patches` category of `config/adventuraarcanacore.cfg`. `Config` declares it (so it
is written to the file and shown in the config screen), and `core.PatchSwitches` reads it before `preInit`, when the
mixins are chosen: add `.setApplyIf(() -> PatchSwitches.enabled("someKey"))` to the builder and a `patch("someKey",
"...")` line to `Config.sync`. A missing file or key means on. Mixin selection requires a restart.
Ordinary runtime settings can use `Config` and its GUI reload event.

## Config files

* `mixins.adventuraarcanacore.early.json`: queued by `EarlyMixinLoader`, registered in the jar manifest.
* `mixins.adventuraarcanacore.late.json`: queued by the `@LateMixin` loader after mod discovery.
* `mixins.adventuraarcanacore.json`: the empty base config expected by the GTNH buildscript.

Keep the JSON mixin lists empty when registering through the builder; otherwise a class can be
loaded twice or bypass its side/mod guards. All three configs share the generated refmap.

## Growthcraft Forestry dependency

`fml.MixinFMLModContainer` runs after Forge binds mod metadata and before dependency validation.
It removes Forestry from Growthcraft's required mods while preserving its load order and version
constraint. Releases that use `mcmod.info` also receive the Forestry ordering rule declared in their
annotation. Other requirements and other mods are unchanged.

This patch runs early on both sides because late mixins load after dependency validation.
It targets Forge and checks the exact `Growthcraft` mod ID, so Growthcraft itself is optional.
Switch: `growthcraftWithoutForestry`.

## Project Zulu gamerules

`projectzulu.MixinDeathGamerulesKeep` replaces Project Zulu's world load handler, which set `keepInventory` and its own
`dropInventory`, `dropHotbar`, `dropArmor` and `dropXP` gamerules to its config defaults on every world load, so a
`/gamerule` change lasted only until the next one. It now adds its own gamerules, with those defaults, only to a world
that lacks them, and leaves `keepInventory` alone. Switch: `projectZuluGamerules`.

## OpenBlocks graves in tall worlds

`openblocks.MixinGraveHeight` raises the top of OpenBlocks' grave search by however much the world reaches above
Y=256. OpenBlocks clamps a grave's height to its `maximalPosY` (249 by default, just under a 256-block world's top), so
with Hohenangst's 512 blocks a player who died above Y=249 got a grave there, often in the air far below where they
died. The configured margin now holds below the world's real top (Y=505 with Hohenangst). Switch:
`openBlocksGraveHeight`.

## Natura clouds

`natura.MixinNaturaNoClouds` stops Natura's cloud generator in every dimension: its clouds float as flat slabs. It
loads for Natura 2.8.19 and 2.8.24 only (`TargetMod.NATURA`, which reads the version from `mods.natura.Natura`).
Switch: `noNaturaClouds`.

## Torches

`realistictorches.MixinTorchLighting` lets every lighter light Realistic Torches' torches through
`compat.realistictorches.TorchLighting`, each into its own flame: Realistic Torches otherwise takes only the vanilla
flint and steel. Switch: `realisticTorchesLighters`.

`realistictorches.MixinTorchClock` and `MixinTorchTileIdle` put Realistic Torches' lit and smoldering torches on
`compat.realistictorches.TorchClock`: placing one starts its time, its own scheduled updates do nothing and its tile
entity, which looked for rain every tick, no longer ticks. `netherlicious.MixinTorchClock` (the soul, foxfire and shadow
torches, whose own burnout removes them) and `netherlicious.MixinBoneTorchClock` (the bone torches, which have none) do
the same for Netherlicious' torches, and let lighters relight them. Switch: `torchClock`, which also registers AAC's
smoldering torches and unlit bone torch. `aether.MixinAetherTorchClock` puts the Aether's ambrosium, hellfire and
ethereal torches, which burn forever, on the clock and lets lighters relight them, and `aether.MixinAetherTorchLook`
(client) draws them as vanilla's torch with their flame. With the Aether, `torchClock` also registers AAC's bone torches
of its flames. See [TORCHES.md](TORCHES.md).

## Chests

AdjaChest pairs a chest with one neighbor of its kind facing the same way, so any number of chests can stand side by
side. `minecraft.MixinBlockChestSneakPlace` and `MixinTileEntityChestPairs` (early, with AdjaChest) make a chest placed
while sneaking pair only on purpose: placed against the side of a single chest of its kind, it pairs with that one and
takes its facing; placed anywhere else, it stays single. `MixinItemStackPlaceClick` notes the face it went against. Such
a manual chest never pairs on its own later and no neighbor takes it; it keeps and sends that in its tile entity. Both
mixins run after AdjaChest's (priority 1100) to reach the methods it adds to vanilla's chest. Switch:
`chestSneakPlacing`.

`aether.MixinAetherChestPairs` and `MixinAetherChestBlocks` pair the Aether's Skyroot, Elysian and breakable treasure
chests, copies of the old vanilla chest that join every neighbor, as AdjaChest pairs vanilla's, sneaking included. The
server picks each pair (`compat.chests.ChestPairs`) and sends it; mimics pair with nothing. Switch: `aetherChestPairs`.

## Better Foliage across threads

`betterfoliage.MixinBlockFilterCache` (client) gives Better Foliage's `BlockFilterSet` a `ConcurrentHashMap` for the
answers it caches per block and metadata, in place of a `HashMap`. Angelica builds chunk meshes on several threads, each
asking the filters about the blocks it draws; a lookup during another thread's put could see the key and then read
null, which crashed the game with a NullPointerException in `BlockFilterSet.matches`. Switch: `betterFoliageFilterCache`.

## SaltyMod on a dedicated server

Three server-only patches keep SaltyMod's client code off a dedicated server, which crashes on it otherwise:
`MixinBeeNestServerIcons` skips the bee nest's client-only icon fields, `MixinNeiServerRegistration` skips its NEI
screens, and `MixinServerRenderType` answers the render type of four blocks without the client proxy. They load for
the pack's SaltyMod build only (`TargetMod.SALTYMOD`, runtime version `2.0.0-master.150+9a0954aa6f`). Switch:
`saltyModDedicatedServer`.

## Wild food

`minecraft.MixinBlockBushWildCrops` (early) lets HarvestCraft's and Harvest the Nether's crops stay where they grow
wild: natural ground for HarvestCraft's, nether ground for Harvest the Nether's. Sowing still needs farmland. Switch:
`wildCropsStay`.

`plantmegapack.MixinPmpBerryLinks` gives HarvestCraft's berries from PMP's berry bushes (`food_links.json`). Switch:
`pmpHarvestCraftBerries`.

`saltymod.MixinSaltyBeeNestSeed` seeds SaltyMod's bee nests per chunk from the world seed, so a seed gives the same
nests (they came from one clock-seeded random). In Biomic's overworld it places none while `beeNestsInBiomic` is on.
Switch: `saltyModBeeNests`.

`natura.MixinNaturaBerryOwnership` keeps Natura's wild berry bushes out of Biomic's overworld while `wildFoodInBiomic`
is on, so HarvestCraft's berries are the only wild ones there. Switch: `naturaBerriesStandDown`.

These moved here from Biomic, which keeps only what a player with Biomic and that mod alone would expect.

Keep future integration helpers in `compat/`, event handlers in `handlers/client/` or
`handlers/server/`, and add `network/`, `util/`, `varinstances/` and `commands/` as needed.
