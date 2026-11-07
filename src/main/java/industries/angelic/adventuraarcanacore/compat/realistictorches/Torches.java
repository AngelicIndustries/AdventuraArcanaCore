package industries.angelic.adventuraarcanacore.compat.realistictorches;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import industries.angelic.adventuraarcanacore.AdventuraArcanaCore;
import industries.angelic.adventuraarcanacore.Config;
import industries.angelic.adventuraarcanacore.core.PatchSwitches;

/**
 * The torches that burn out: Realistic Torches' torch, Netherlicious' soul, foxfire and shadow torches and its bone
 * torches, and the Aether's ambrosium, hellfire and ethereal (cold fire) torches with AAC's bone torches of those
 * flames. Each kind has a lit, a smoldering and an unlit block: Realistic Torches' own for its torch, with its unlit
 * torch for every wooden kind, the mods' lit torches, and AAC's lit, smoldering and unlit bone torches where the mods
 * have none. AAC's blocks are registered with the torchClock patch.
 */
public final class Torches {

    public enum Material {
        WOOD,
        BONE
    }

    public enum Flame {
        FIRE,
        SOUL,
        FOXFIRE,
        SHADOW,
        AMBROSIUM,
        HELLFIRE,
        COLD
    }

    /** By the IDs of Config.TORCH_KINDS. */
    public enum Kind {

        TORCH(Material.WOOD, Flame.FIRE, "RealisticTorches", "TorchLit", null, null),
        SOUL_TORCH(Material.WOOD, Flame.SOUL, NETHERLICIOUS, "SoulTorch", "netherlicious:soul_torch", null),
        FOXFIRE_TORCH(Material.WOOD, Flame.FOXFIRE, NETHERLICIOUS, "FoxfireTorch", "netherlicious:foxfire_torch", null),
        SHADOW_TORCH(Material.WOOD, Flame.SHADOW, NETHERLICIOUS, "ShadowTorch", "netherlicious:shadow_torch", null),
        BONE_TORCH(Material.BONE, Flame.FIRE, NETHERLICIOUS, "BoneTorch", "netherlicious:torch_bone", null),
        SOUL_BONE_TORCH(Material.BONE, Flame.SOUL, NETHERLICIOUS, "SoulBoneTorch", "netherlicious:soul_torch_bone",
            null),
        FOXFIRE_BONE_TORCH(Material.BONE, Flame.FOXFIRE, NETHERLICIOUS, "FoxfireBoneTorch",
            "netherlicious:foxfire_torch_bone", null),
        SHADOW_BONE_TORCH(Material.BONE, Flame.SHADOW, NETHERLICIOUS, "ShadowBoneTorch",
            "netherlicious:shadow_torch_bone", null),
        // The Aether's torches, on Skyroot sticks, are drawn as vanilla's torch with their flame's colours.
        AMBROSIUM_TORCH(Material.WOOD, Flame.AMBROSIUM, AETHER, "ambrosium_torch", "minecraft:torch_on",
            "aether_legacy:ambrosium_torch"),
        HELLFIRE_TORCH(Material.WOOD, Flame.HELLFIRE, AETHER, "hellfire_torch", "minecraft:torch_on",
            "aether_legacy:hellfire_torch"),
        ETHEREAL_TORCH(Material.WOOD, Flame.COLD, AETHER, "ethereal_torch", "minecraft:torch_on",
            "aether_legacy:ethereal_torch"),
        AMBROSIUM_BONE_TORCH(Material.BONE, Flame.AMBROSIUM, AdventuraArcanaCore.MODID, "ambrosium_bone_torch",
            "netherlicious:torch_bone", "aether_legacy:ambrosium_torch"),
        HELLFIRE_BONE_TORCH(Material.BONE, Flame.HELLFIRE, AdventuraArcanaCore.MODID, "hellfire_bone_torch",
            "netherlicious:torch_bone", "aether_legacy:hellfire_torch"),
        ETHEREAL_BONE_TORCH(Material.BONE, Flame.COLD, AdventuraArcanaCore.MODID, "ethereal_bone_torch",
            "netherlicious:torch_bone", "aether_legacy:ethereal_torch");

        public final Material material;
        public final Flame flame;
        private final String litMod, litName;
        /**
         * The lit torch's look (domain:name of a block texture), which AAC's smoldering torch is painted from; null
         * for Realistic Torches' torch, whose smoldering torch is its own.
         */
        final String look;
        /** A torch texture whose flame colours replace the look's flame, or null. */
        final String flameFrom;
        Block lit, smoldering;

        Kind(Material material, Flame flame, String litMod, String litName, String look, String flameFrom) {
            this.material = material;
            this.flame = flame;
            this.litMod = litMod;
            this.litName = litName;
            this.look = look;
            this.flameFrom = flameFrom;
        }

        /** Whether the mods its blocks and textures come from are there. */
        boolean available() {
            return loaded(litMod) && loaded(domain(look)) && loaded(domain(flameFrom));
        }

        /** AAC's own lit torch, for a flame its mod has no bone torch of. */
        boolean aacLit() {
            return AdventuraArcanaCore.MODID.equals(litMod);
        }

        public String id() {
            return name().toLowerCase(Locale.ROOT);
        }

        /** Ticks from lit to unlit; 0 or less: never. */
        public int burnout() {
            return Config.torchBurnout.getOrDefault(id(), 72000);
        }

        public boolean rain() {
            return Config.torchRain.getOrDefault(id(), flame != Flame.SOUL && flame != Flame.COLD);
        }

        /** Whether the torch goes out at all, and so drops an unlit torch. */
        public boolean goesOut() {
            return burnout() > 0 || rain();
        }

        /** The last tenth of the burnout, as Realistic Torches smolders. */
        long smolderTicks() {
            return Math.max(1, burnout() / 10);
        }

        long litTicks() {
            return burnout() - smolderTicks();
        }

        public Block lit() {
            return lit;
        }

        public Block smoldering() {
            return smoldering;
        }

        public Block unlit() {
            return UNLIT.get(material);
        }
    }

    private static final String NETHERLICIOUS = "netherlicious", AETHER = "aether_legacy";
    private static final Map<Block, Kind> LIT = new HashMap<>(), SMOLDERING = new HashMap<>();
    private static final Map<Material, Block> UNLIT = new EnumMap<>(Material.class);
    private static final Map<Block, Material> UNLIT_MATERIALS = new HashMap<>();
    private static ItemGlowstonePaste paste;
    private static ItemZaniteAndAmbrosium ambrosiumLighter;

    private Torches() {}

    public static void preInit() {
        if (PatchSwitches.enabled("torchClock")) {
            for (Kind kind : Kind.values()) {
                if (kind.look == null || !kind.available()) continue;
                if (kind.aacLit()) {
                    kind.lit = new BlockBoneTorch(kind);
                    GameRegistry.registerBlock(kind.lit, kind.id());
                }
                kind.smoldering = new BlockSmolderingTorch(kind);
                GameRegistry.registerBlock(kind.smoldering, null, "smoldering_" + kind.id());
            }
            if (Loader.isModLoaded(NETHERLICIOUS)) {
                Block unlitBone = new BlockUnlitBoneTorch();
                GameRegistry.registerBlock(unlitBone, "unlit_bone_torch");
                UNLIT.put(Material.BONE, unlitBone);
            }
        }
        if (Config.glowstonePaste) {
            paste = new ItemGlowstonePaste();
            GameRegistry.registerItem(paste, "glowstone_paste");
        }
        if (Config.ambrosiumLighter && Loader.isModLoaded(AETHER)) {
            ambrosiumLighter = new ItemZaniteAndAmbrosium();
            GameRegistry.registerItem(ambrosiumLighter, "zanite_and_ambrosium");
        }
    }

    public static void postInit() {
        Block woodUnlit = GameRegistry.findBlock("RealisticTorches", "TorchUnlit");
        if (woodUnlit != null) UNLIT.put(Material.WOOD, woodUnlit);
        for (Map.Entry<Material, Block> unlit : UNLIT.entrySet()) UNLIT_MATERIALS.put(unlit.getValue(), unlit.getKey());
        for (Kind kind : Kind.values()) {
            if (kind.lit == null) kind.lit = GameRegistry.findBlock(kind.litMod, kind.litName);
            if (kind == Kind.TORCH) kind.smoldering = GameRegistry.findBlock("RealisticTorches", "TorchSmoldering");
            if (kind.lit != null) LIT.put(kind.lit, kind);
            if (kind.smoldering != null) SMOLDERING.put(kind.smoldering, kind);
            if (kind.lit != null && kind.smoldering instanceof BlockSmolderingTorch smoldering)
                smoldering.lightLike(kind.lit);
        }
        if (paste != null) {
            GameRegistry
                .addShapelessRecipe(new ItemStack(paste), Items.glowstone_dust, Items.clay_ball, Items.water_bucket);
            if (Kind.TORCH.lit != null) GameRegistry
                .addShapelessRecipe(new ItemStack(Blocks.torch), new ItemStack(Kind.TORCH.lit), new ItemStack(paste));
        }
        Item shard = GameRegistry.findItem(AETHER, "ambrosium_shard"),
            zanite = GameRegistry.findItem(AETHER, "zanite_gemstone");
        if (ambrosiumLighter != null && shard != null && zanite != null)
            GameRegistry.addShapelessRecipe(new ItemStack(ambrosiumLighter), shard, zanite);
        AdventuraArcanaCore.LOG.info("Torches that burn out: {}", LIT.values());
    }

    /** The kind whose lit torch the block is, or null. */
    public static Kind litKind(Block block) {
        return LIT.get(block);
    }

    /** The kind whose smoldering torch the block is, or null. */
    public static Kind smolderingKind(Block block) {
        return SMOLDERING.get(block);
    }

    /** The lit torch of the material and flame, or null. */
    public static Block lit(Material material, Flame flame) {
        for (Kind kind : Kind.values()) if (kind.material == material && kind.flame == flame) return kind.lit;
        return null;
    }

    private static boolean loaded(String mod) {
        return mod == null || mod.equals("minecraft")
            || mod.equals(AdventuraArcanaCore.MODID)
            || Loader.isModLoaded(mod);
    }

    private static String domain(String texture) {
        return texture == null ? null : texture.substring(0, texture.indexOf(':'));
    }

    /** The kind a lighter of the flame makes of the torch, lit, smoldering or unlit, or null if it isn't one. */
    public static Kind toLight(Block torch, Flame flame) {
        Kind burning = LIT.containsKey(torch) ? LIT.get(torch) : SMOLDERING.get(torch);
        Material material = burning != null ? burning.material : UNLIT_MATERIALS.get(torch);
        if (material == null) return null;
        for (Kind kind : Kind.values())
            if (kind.material == material && kind.flame == flame && kind.lit != null) return kind;
        return null;
    }

    /** What a lit torch of the block drops, if it goes out: its unlit torch. Null if not one, or it never goes out. */
    public static Item unlitDrop(Block lit) {
        Kind kind = LIT.get(lit);
        Block unlit = kind == null || !kind.goesOut() ? null : kind.unlit();
        return unlit == null ? null : Item.getItemFromBlock(unlit);
    }
}
