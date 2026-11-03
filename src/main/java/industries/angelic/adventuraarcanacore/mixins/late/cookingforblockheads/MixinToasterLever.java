package industries.angelic.adventuraarcanacore.mixins.late.cookingforblockheads;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import industries.angelic.adventuraarcanacore.compat.cookingforblockheads.ToasterLever;

/**
 * The toaster kept whether it was running to the side that was clicked: other players never saw its lever go down,
 * and a reload stopped it with the bread still inside. It now saves and shares that, plays its lever's sounds for
 * everyone nearby and moves its lever smoothly.
 */
@Pseudo
@Mixin(targets = "net.blay09.mods.cookingforblockheads.tile.TileToaster", remap = false)
public abstract class MixinToasterLever implements ToasterLever.Animated {

    @Shadow(remap = false)
    private boolean active;

    @Shadow(remap = false)
    private int toastTicks;

    @Unique
    private float adventuraarcanacore$lever, adventuraarcanacore$prevLever;

    /** Whether the server shares the toaster's state, so the client can wait for it. */
    @Unique
    private boolean adventuraarcanacore$synced;

    // The tile's NBT is also its update packet.
    @Inject(method = { "writeToNBT", "func_145841_b" }, at = @At("RETURN"), remap = false, require = 1)
    private void adventuraarcanacore$write(NBTTagCompound tag, CallbackInfo ci) {
        tag.setBoolean(ToasterLever.ACTIVE_TAG, active);
        tag.setInteger(ToasterLever.TICKS_TAG, toastTicks);
    }

    @Inject(method = { "readFromNBT", "func_145839_a" }, at = @At("RETURN"), remap = false, require = 1)
    private void adventuraarcanacore$read(NBTTagCompound tag, CallbackInfo ci) {
        if (!tag.hasKey(ToasterLever.ACTIVE_TAG)) return;
        adventuraarcanacore$synced = true;
        active = tag.getBoolean(ToasterLever.ACTIVE_TAG);
        toastTicks = tag.getInteger(ToasterLever.TICKS_TAG);
    }

    @WrapMethod(method = "setActive", remap = false)
    private void adventuraarcanacore$leverMoved(boolean value, Operation<Void> original) {
        boolean was = active;
        original.call(value);
        TileEntity tile = (TileEntity) (Object) this;
        World world = tile.getWorldObj();
        if (was == active || world == null || world.isRemote) return;
        // Covers starting, finishing, stopping by hand and taking the bread out.
        world.playSoundEffect(
            tile.xCoord + 0.5,
            tile.yCoord + 0.5,
            tile.zCoord + 0.5,
            active ? ToasterLever.LOWER : ToasterLever.POP,
            0.8F,
            ToasterLever.PITCH + (world.rand.nextFloat() - 0.5F) * ToasterLever.PITCH_SPREAD);
        world.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
    }

    @Inject(
        method = { "updateEntity", "func_145845_h" },
        at = @At("HEAD"),
        cancellable = true,
        remap = false,
        require = 1)
    private void adventuraarcanacore$moveLever(CallbackInfo ci) {
        World world = ((TileEntity) (Object) this).getWorldObj();
        if (world == null || !world.isRemote) return;
        adventuraarcanacore$prevLever = adventuraarcanacore$lever;
        adventuraarcanacore$lever = ToasterLever.step(adventuraarcanacore$lever, active);
        // The client's own count ran ahead of a busy server, so the lever popped up before the pop sound.
        // With the server's state shared, the lever waits for it instead.
        if (adventuraarcanacore$synced) ci.cancel();
    }

    @Override
    public float adventuraarcanacore$lever(float partialTicks) {
        return adventuraarcanacore$prevLever
            + (adventuraarcanacore$lever - adventuraarcanacore$prevLever) * partialTicks;
    }
}
