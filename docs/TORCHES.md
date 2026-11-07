# Torches

Torches burn out, after Realistic Torches: its own torch, Netherlicious' soul, foxfire and shadow torches and its bone
torches, and the Aether's ambrosium, hellfire and ethereal (cold fire) torches. Everything here needs Realistic Torches;
Netherlicious' torches need Netherlicious, the Aether's the Aether, and the bone torches of the Aether's flames both.

## Kinds

`compat.realistictorches.Torches` lists the fourteen kinds, by material (wood or bone) and flame (normal, soul, foxfire,
shadow, ambrosium, hellfire, cold), so every flame has a wooden and a bone torch. Each has a lit, a smoldering and an
unlit block:

| | Lit | Smoldering | Unlit |
| --- | --- | --- | --- |
| Torch | Realistic Torches' | Realistic Torches' | Realistic Torches' |
| Soul, foxfire, shadow torch | Netherlicious' | AAC's | Realistic Torches' |
| Bone torch, of each flame | Netherlicious' | AAC's | AAC's unlit bone torch |
| Ambrosium, hellfire, ethereal torch | The Aether's | AAC's | Realistic Torches' |
| Ambrosium, hellfire, ethereal bone torch | AAC's | AAC's | AAC's unlit bone torch |

A lit torch smolders for the last tenth of its burnout time, at about two thirds of its light, smoking and showing its
flame now and then, and then becomes its unlit torch. Lit and smoldering torches drop their unlit torch. Netherlicious'
soul torch looks like Et Futurum's, which stays the one that never goes out.

The Aether's torches, on Skyroot sticks, are drawn as vanilla's torch with their flame's colours, like the unlit wooden
torch they go out into. The ethereal torch, crafted with a cyro rod, is the one of cold fire. AAC's bone torches of the
Aether's flames give the light and particles of the Aether's torch of the same flame.

## Lighting

`TorchLighting` lights a torch, lit, smoldering or unlit, into its own material's torch of the lighter's flame: the
vanilla flint and steel and the `torchLighters` (Natura's flint and blaze) give a normal flame, Netherlicious' soul,
foxfire and shadow flint and steels theirs, the Aether's Zanite and Cinerarium hellfire and its Zanite and Cyro cold fire.
AAC adds Zanite and Ambrosium (an ambrosium shard and a zanite gemstone, 24 uses) for ambrosium flame; it lights torches
and does nothing else. Each use wears the lighter, the torch keeps its facing, and rain that would put the torch out
keeps it from being lit.

## Clock

`TorchClock` keeps the burning torches of each chunk with the world time each next smolders or goes out. They are saved
with the chunk, in its `AACTorches` tag (the save's world time, each torch's position in the chunk and the ticks it had
left), and dropped from memory when it unloads, so only loaded chunks are looked at. With `burnUnloaded` a reloaded
chunk's torches catch up on the time it was away, so one can come back to a torch smoldering or out; without, they pause.

Once a second, the loaded chunks whose next deadline has passed are gone through, and while it rains, those with torches
that rain puts out; any other chunk costs a comparison. A torch that stands in the rain for `rainDelay` ticks goes out.
A torch enters the clock when placed or lit, through its onBlockAdded, and leaves when it goes out or is no longer
there. A chunk saved without the tag, from before, is searched once when it loads, and its burning torches start their
time then.

## Config

The `torches` category holds `torchLighters`, `rainDelay`, `burnUnloaded`, `glowstonePaste` and `ambrosiumLighter`, and
a subcategory per kind (`torch`, `soul_torch`, ..., `ethereal_bone_torch`) with its `burnout` (ticks, 0 or less for
never) and `rain` (whether rain puts it out). Rain puts out every kind but the soul and cold fire torches by default. Realistic Torches'
`torch.burnoutTime` no longer applies. The patches are `realisticTorchesLighters` and `torchClock`.

## Textures

AAC ships none. Its sprites are painted when the atlas is stitched (`TorchPaint`, with `util.PaintedSprite`), from the
installed textures:

* A smoldering torch is its lit torch with the flame (the top two rows of the shape) faded and dimmed, and for a normal
  flame reddened, as Realistic Torches' smoldering torch is drawn from its lit one.
* The unlit bone torch is Netherlicious' bone torch with the charred tip of Realistic Torches' unlit torch.
* The Aether's torches, and AAC's bone torches of their flames, are vanilla's torch and Netherlicious' bone torch with
  the flame of the Aether's torch: each flame pixel takes the Aether flame's colour at its place in the light-to-dark
  order.
* Zanite and Ambrosium is the Aether's Zanite and Cinerarium with its cinerarium in the ambrosium shard's colours.
* Glowstone paste is vanilla's clay ball in glowstone dust's colours, a quarter clay.

## Glowstone paste

After later Realistic Torches versions: glowstone dust, clay and a water bucket make glowstone paste, and paste on a lit
Realistic Torches torch makes a vanilla torch, which never goes out. With Realistic Torches' glowstone crystal (4
glowstone dust around coal, with a stick for 4 torches), it is the way to light that lasts.
