# Adding pack patches

All registrations live in `core/Mixins.java`, which implements GTNHMixins' `IMixins`.
`core/TargetMod.java` implements `ITargetMod` for optional targets. Both start empty.
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
Introduce a separate early-safe config loader if patch selection needs configuration; never access
Minecraft client classes or initialize the main mod from it. Mixin selection requires a restart.
Ordinary runtime settings can use `Config` and its GUI reload event.

## Config files

* `mixins.adventuraarcanacore.early.json`: queued by `EarlyMixinLoader`, registered in the jar manifest.
* `mixins.adventuraarcanacore.late.json`: queued by the `@LateMixin` loader after mod discovery.
* `mixins.adventuraarcanacore.json`: the empty base config expected by the GTNH buildscript.

Keep the JSON mixin lists empty when registering through the builder; otherwise a class can be
loaded twice or bypass its side/mod guards. All three configs share the generated refmap.
With no actual `@Mixin` classes yet, the annotation processor produces no refmap or mixin SRG file;
the build/runtime may report their absence. These are generated once real mixins are added.

Keep future integration helpers in `compat/`, event handlers in `handlers/client/` or
`handlers/server/`, and add `network/`, `util/`, `varinstances/` and `commands/` as needed.
