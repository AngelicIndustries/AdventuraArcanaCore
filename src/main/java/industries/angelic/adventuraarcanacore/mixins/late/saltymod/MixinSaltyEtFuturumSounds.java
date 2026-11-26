package industries.angelic.adventuraarcanacore.mixins.late.saltymod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * SaltyMod 2.1.3 plays its Salt Grass flattening and Bee Burrow stripping sounds as minecraft_1.21:item.shovel.flatten
 * and minecraft_1.21:item.axe.strip, a sound domain nothing registers, so both were silent. Et Futurum registers these
 * sounds under its own asset version (Reference.MCAssetVer); play them from there.
 */
@Pseudo
@Mixin(targets = { "darkbum.saltymod.block.BlockSaltGrass", "darkbum.saltymod.block.BlockBeeBurrow" }, remap = false)
public abstract class MixinSaltyEtFuturumSounds {

    @Unique
    private static String adventuraarcanacore$domain;

    // The targets are not on the compile classpath: name their MCP and shipped SRG methods. Salt Grass plays its sound
    // in onBlockActivated, the Bee Burrow in tryStripBlock, which onBlockActivated calls.
    @ModifyConstant(
        method = { "onBlockActivated", "func_149727_a", "tryStripBlock" },
        constant = { @Constant(stringValue = "minecraft_1.21:item.shovel.flatten"),
            @Constant(stringValue = "minecraft_1.21:item.axe.strip") },
        require = 1,
        remap = false)
    private String adventuraarcanacore$etFuturumSound(String sound) {
        String domain = adventuraarcanacore$domain;
        if (domain == null) adventuraarcanacore$domain = domain = adventuraarcanacore$etFuturumDomain();
        return domain.isEmpty() ? sound : domain + sound.substring(sound.indexOf(':'));
    }

    /** Et Futurum's asset version, read at run time: a compiled-in copy would go stale when Et Futurum moves on. */
    @Unique
    private static String adventuraarcanacore$etFuturumDomain() {
        try {
            return (String) Class.forName("ganymedes01.etfuturum.lib.Reference")
                .getField("MCAssetVer")
                .get(null);
        } catch (ReflectiveOperationException | LinkageError e) {
            return "";
        }
    }
}
