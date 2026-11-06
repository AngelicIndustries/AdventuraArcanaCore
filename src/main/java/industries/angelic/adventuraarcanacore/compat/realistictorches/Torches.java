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
 * The torches that burn out: Realistic Torches' torch, and Netherlicious' soul, foxfire and shadow torches and its bone
 * torches. Each kind has a lit, a smoldering and an unlit block: Realistic Torches' own for its torch, with its unlit
 * torch for every wooden kind, Netherlicious' lit torches, and AAC's smoldering torches and unlit bone torch where the
 * mods have none. AAC's blocks are registered with the torchClock patch.
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
        SHADOW
    }

    /** By the IDs of Config.TORCH_KINDS. */
    public enum Kind {

        TORCH(Material.WOOD, Flame.FIRE, "RealisticTorches", "TorchLit", null),
        SOUL_TORCH(Material.WOOD, Flame.SOUL, "netherlicious", "SoulTorch", "soul_torch"),
        FOXFIRE_TORCH(Material.WOOD, Flame.FOXFIRE, "netherlicious", "FoxfireTorch", "foxfire_torch"),
        SHADOW_TORCH(Material.WOOD, Flame.SHADOW, "netherlicious", "ShadowTorch", "shadow_torch"),
        BONE_TORCH(Material.BONE, Flame.FIRE, "netherlicious", "BoneTorch", "torch_bone"),
        SOUL_BONE_TORCH(Material.BONE, Flame.SOUL, "netherlicious", "SoulBoneTorch", "soul_torch_bone"),
        FOXFIRE_BONE_TORCH(Material.BONE, Flame.FOXFIRE, "netherlicious", "FoxfireBoneTorch", "foxfire_torch_bone"),
        SHADOW_BONE_TORCH(Material.BONE, Flame.SHADOW, "netherlicious", "ShadowBoneTorch", "shadow_torch_bone");

        public final Material material;
        public final Flame flame;
        private final String litMod, litName;
        /** Netherlicious' texture of the lit torch, which AAC's smoldering torch is painted from, or null. */
        final String texture;
        Block lit, smoldering;

        Kind(Material material, Flame flame, String litMod, String litName, String texture) {
            this.material = material;
            this.flame = flame;
            this.litMod = litMod;
            this.litName = litName;
            this.texture = texture;
        }

        public String id() {
            return name().toLowerCase(Locale.ROOT);
        }

        /** Ticks from lit to unlit; 0 or less: never. */
        public int burnout() {
            return Config.torchBurnout.getOrDefault(id(), 72000);
        }

        public boolean rain() {
            return Config.torchRain.getOrDefault(id(), flame != Flame.SOUL);
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

    private static final Map<Block, Kind> LIT = new HashMap<>(), SMOLDERING = new HashMap<>();
    private static final Map<Material, Block> UNLIT = new EnumMap<>(Material.class);
    private static final Map<Block, Material> UNLIT_MATERIALS = new HashMap<>();
    private static ItemGlowstonePaste paste;

    private Torches() {}

    public static void preInit() {
        boolean netherlicious = Loader.isModLoaded("netherlicious");
        if (netherlicious && PatchSwitches.enabled("torchClock")) {
            for (Kind kind : Kind.values()) if (kind.texture != null) {
                kind.smoldering = new BlockSmolderingTorch(kind);
                GameRegistry.registerBlock(kind.smoldering, null, "smoldering_" + kind.id());
            }
            Block unlitBone = new BlockUnlitBoneTorch();
            GameRegistry.registerBlock(unlitBone, "unlit_bone_torch");
            UNLIT.put(Material.BONE, unlitBone);
        }
        if (Config.glowstonePaste) {
            paste = new ItemGlowstonePaste();
            GameRegistry.registerItem(paste, "glowstone_paste");
        }
    }

    public static void postInit() {
        Block woodUnlit = GameRegistry.findBlock("RealisticTorches", "TorchUnlit");
        if (woodUnlit != null) UNLIT.put(Material.WOOD, woodUnlit);
        for (Map.Entry<Material, Block> unlit : UNLIT.entrySet()) UNLIT_MATERIALS.put(unlit.getValue(), unlit.getKey());
        for (Kind kind : Kind.values()) {
            kind.lit = GameRegistry.findBlock(kind.litMod, kind.litName);
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
