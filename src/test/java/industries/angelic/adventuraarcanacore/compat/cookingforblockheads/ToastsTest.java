package industries.angelic.adventuraarcanacore.compat.cookingforblockheads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collections;

import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import cpw.mods.fml.common.Loader;
import industries.angelic.adventuraarcanacore.compartment.ForgeTestRunner;

@RunWith(ForgeTestRunner.class)
public class ToastsTest {

    @BeforeClass
    public static void bootstrap() {
        Loader.injectData("7", "99", "40", "1614", "1.7.10", "9.05", new File("."), Collections.emptyList());
        Bootstrap.func_151354_b();
    }

    @Test
    public void oneToastPerSlotWithoutMutatingOrSharingStacks() {
        ItemStack bread = new ItemStack(Items.bread, 64);
        ItemStack result = new ItemStack(Items.cookie, 32);
        result.setTagCompound(new NBTTagCompound());
        result.getTagCompound()
            .setString("test", "original");
        Toasts.Recipe recipe = new Toasts.Recipe(bread, result);
        assertEquals(64, bread.stackSize);
        assertEquals(32, result.stackSize);
        assertEquals(1, recipe.input().stackSize);
        ItemStack first = recipe.output();
        assertEquals(1, first.stackSize);
        first.stackSize = 20;
        first.getTagCompound()
            .setString("test", "changed");
        assertEquals(1, recipe.output().stackSize);
        assertEquals(
            "original",
            recipe.output()
                .getTagCompound()
                .getString("test"));
    }

    @Test
    public void recipeAcceptsNamedBreadButNotDifferentMetadataOrEmptyStacks() {
        Toasts.Recipe recipe = new Toasts.Recipe(new ItemStack(Items.bread), new ItemStack(Items.cookie));
        assertTrue(recipe.matches(new ItemStack(Items.bread).setStackDisplayName("My bread")));
        assertFalse(recipe.matches(new ItemStack(Items.bread, 1, 1)));
        assertFalse(recipe.matches(new ItemStack(Items.bread, 0)));
        assertFalse(recipe.matches(new ItemStack(Items.apple)));
        assertFalse(recipe.matches(null));
    }
}
