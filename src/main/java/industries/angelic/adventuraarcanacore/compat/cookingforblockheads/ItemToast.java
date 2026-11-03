package industries.angelic.adventuraarcanacore.compat.cookingforblockheads;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import industries.angelic.adventuraarcanacore.util.PaintedSprite;

/** The variants use Pam's toast food values, including changes made by the pack. */
public final class ItemToast extends ItemFood {

    private final Toasts.Kind kind;

    ItemToast(Toasts.Kind kind) {
        super(1, 0.1F, false);
        this.kind = kind;
        setUnlocalizedName("adventuraarcanacore." + kind.id);
        setCreativeTab(CreativeTabs.tabFood);
    }

    @Override
    public int func_150905_g(ItemStack stack) {
        return Toasts.pamToast() instanceof ItemFood food ? food.func_150905_g(new ItemStack(food))
            : super.func_150905_g(stack);
    }

    @Override
    public float func_150906_h(ItemStack stack) {
        return Toasts.pamToast() instanceof ItemFood food ? food.func_150906_h(new ItemStack(food))
            : super.func_150906_h(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = PaintedSprite
            .register(register, "adventuraarcanacore:" + kind.id, source -> ToastPaint.paint(source, kind));
    }
}
