package industries.angelic.adventuraarcanacore.compat.angelica;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.gtnewhorizons.angelica.api.BlockLightProvider;
import com.gtnewhorizons.angelica.compat.ModStatus;
import com.gtnewhorizons.angelica.compat.backhand.BackhandReflectionCompat;
import com.gtnewhorizons.angelica.dynamiclights.DynamicLights;

import industries.angelic.adventuraarcanacore.compat.ItemLightColors;
import industries.angelic.adventuraarcanacore.compat.supernova.SupernovaColors;

/**
 * The color of an entity's dynamic light, which Angelica keeps white: a held, worn or dropped block item takes the
 * color Supernova gives the block, other items the one in itemLightColors (lava buckets lava's), a burning entity
 * fire's. Several lit items mix by their brightest channels, each at its own brightness.
 */
public final class DynamicLightColor {

    private DynamicLightColor() {}

    /** Angelica's packed RGB for the entity's light at this luminance. */
    public static int rgb(Entity entity, int luminance) {
        if (luminance <= 0) return 0;
        if (entity.isBurning()) return scaled(fire(), 15);
        int[] mixed = null;
        if (entity instanceof EntityItem) mixed = add(mixed, ((EntityItem) entity).getEntityItem());
        else if (entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;
            for (int slot = 0; slot < 5; slot++) mixed = add(mixed, living.getEquipmentInSlot(slot));
            if (ModStatus.isBackhandLoaded && entity instanceof EntityPlayer)
                mixed = add(mixed, BackhandReflectionCompat.getOffhandItem((EntityPlayer) entity));
        }
        if (mixed == null) return BlockLightProvider.packRGB(luminance, luminance, luminance);
        return BlockLightProvider.packRGB(mixed[0], mixed[1], mixed[2]);
    }

    /** Mixes in a stack's light: its color scaled to the luminance Angelica gives it, by brightest channel. */
    private static int[] add(int[] mixed, ItemStack stack) {
        if (stack == null) return mixed;
        int luminance = DynamicLights.getLuminanceFromItemStack(stack);
        if (luminance <= 0) return mixed;
        int[] color = color(stack);
        int max = Math.max(color[0], Math.max(color[1], color[2]));
        if (max <= 0) return mixed;
        int[] lit = new int[3];
        for (int i = 0; i < 3; i++) lit[i] = Math.round(color[i] * luminance / (float) max);
        if (mixed == null) return lit;
        for (int i = 0; i < 3; i++) mixed[i] = Math.max(mixed[i], lit[i]);
        return mixed;
    }

    /** The stack's light color, 0 to 15 per channel; white when nothing says otherwise. */
    public static int[] color(ItemStack stack) {
        Item item = stack.getItem();
        int[] own = ItemLightColors.get(Item.itemRegistry.getNameForObject(item), stack.getItemDamage());
        if (own != null) return own;
        Block block = Block.getBlockFromItem(item);
        if (item == Items.lava_bucket) block = Blocks.lava;
        if (block != null && block != Blocks.air) {
            int[] emission = SupernovaColors
                .emission(block, item == Items.lava_bucket ? 0 : item.getMetadata(stack.getItemDamage()));
            if (emission != null && (emission[0] | emission[1] | emission[2]) != 0) return emission;
        }
        return new int[] { 15, 15, 15 };
    }

    private static int[] fire() {
        int[] emission = SupernovaColors.emission(Blocks.fire, 0);
        return emission != null && (emission[0] | emission[1] | emission[2]) != 0 ? emission : new int[] { 15, 15, 15 };
    }

    private static int scaled(int[] color, int luminance) {
        int max = Math.max(color[0], Math.max(color[1], color[2]));
        return BlockLightProvider.packRGB(
            Math.round(color[0] * luminance / (float) max),
            Math.round(color[1] * luminance / (float) max),
            Math.round(color[2] * luminance / (float) max));
    }
}
