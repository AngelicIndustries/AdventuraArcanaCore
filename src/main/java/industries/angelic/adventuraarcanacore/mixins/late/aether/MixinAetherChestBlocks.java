package industries.angelic.adventuraarcanacore.mixins.late.aether;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import industries.angelic.adventuraarcanacore.compat.chests.ChestPairs;

/**
 * The Aether's chest blocks as AdjaChest makes vanilla's: placing one next to any number of others is allowed and turns
 * none of them; it faces the player, and its bounds and inventory take only its partner.
 */
@Pseudo
@Mixin(
    targets = { "com.gildedgames.the_aether.blocks.container.BlockSkyrootChest",
        "com.gildedgames.the_aether.blocks.container.BlockElysianChest",
        "com.gildedgames.the_aether.blocks.container.BlockTreasureChestBreakable" },
    remap = false)
public abstract class MixinAetherChestBlocks extends BlockContainer {

    protected MixinAetherChestBlocks(Material material) {
        super(material);
    }

    @Inject(method = { "canPlaceBlockAt", "func_149742_c" }, at = @At("HEAD"), cancellable = true)
    private void adventuraarcanacore$placeAnywhere(World world, int x, int y, int z,
        CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(
            world.getBlock(x, y, z)
                .isReplaceable(world, x, y, z));
    }

    // It turned itself and its neighbors to face as one double chest.
    @Inject(method = { "onBlockAdded", "func_149726_b" }, at = @At("HEAD"), cancellable = true)
    private void adventuraarcanacore$keepNeighbors(World world, int x, int y, int z, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = { "onBlockPlacedBy", "func_149689_a" }, at = @At("HEAD"), cancellable = true)
    private void adventuraarcanacore$place(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack,
        CallbackInfo ci) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof ChestPairs.Pairable)) return;
        ChestPairs.Pairable chest = (ChestPairs.Pairable) tile;
        chest.adventuraarcanacore$placed(stack);
        if (ChestPairs.sneaking(placer)) ChestPairs.sneakPlaced(world, x, y, z, placer, chest);
        else {
            world.setBlockMetadataWithNotify(x, y, z, ChestPairs.facing(placer), 3);
            ChestPairs.resolve(tile);
        }
        ci.cancel();
    }

    @Inject(method = { "setBlockBoundsBasedOnState", "func_149719_a" }, at = @At("HEAD"), cancellable = true)
    private void adventuraarcanacore$bounds(IBlockAccess access, int x, int y, int z, CallbackInfo ci) {
        TileEntity tile = access.getTileEntity(x, y, z);
        ChestPairs.bounds(
            this,
            tile instanceof ChestPairs.Pairable ? ((ChestPairs.Pairable) tile).adventuraarcanacore$pairedTo()
                : ForgeDirection.UNKNOWN);
        ci.cancel();
    }

    /** Its inventory joins each neighbor of its kind; to it, only its partner is one. */
    @WrapOperation(
        method = "getInventory",
        at = {
            @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/World;func_147439_a(III)Lnet/minecraft/block/Block;",
                remap = false),
            @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;",
                remap = false) },
        require = 4)
    private Block adventuraarcanacore$onlyPartner(World world, int nx, int ny, int nz, Operation<Block> original,
        @Local(argsOnly = true, ordinal = 0) int x, @Local(argsOnly = true, ordinal = 1) int y,
        @Local(argsOnly = true, ordinal = 2) int z) {
        Block block = original.call(world, nx, ny, nz);
        if (block != this) return block;
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof ChestPairs.Pairable && ChestPairs.isPartnerAt(tile, nx, ny, nz) ? block : Blocks.air;
    }
}
