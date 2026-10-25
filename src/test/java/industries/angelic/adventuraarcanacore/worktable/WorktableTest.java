package industries.angelic.adventuraarcanacore.worktable;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collections;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.Loader;
import industries.angelic.adventuraarcanacore.compartment.ForgeTestRunner;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@RunWith(ForgeTestRunner.class)
public class WorktableTest {

    private EntityPlayer player;
    private TileWorktable tile;
    private ContainerWorktable container;

    @BeforeClass
    public static void bootstrap() throws Exception {
        Loader.injectData("7", "99", "40", "1614", "1.7.10", "9.05", new File("."), Collections.emptyList());
        java.lang.reflect.Field side = cpw.mods.fml.relauncher.FMLRelaunchLog.class.getDeclaredField("side");
        side.setAccessible(true);
        side.set(null, cpw.mods.fml.relauncher.Side.SERVER);
        Bootstrap.func_151354_b();
        TileEntity.addMapping(TileWorktable.class, "aac_test_worktable");
    }

    @Before
    public void setup() {
        player = new TestPlayer(new TestWorld());
        tile = new TileWorktable() {

            @Override
            public boolean isUseableByPlayer(EntityPlayer player) {
                return true;
            }
        };
        container = new ContainerWorktable(player, tile);
        player.openContainer = container;
    }

    private void sticks() {
        tile.setPattern(0, new ItemStack(Blocks.planks));
        tile.setPattern(3, new ItemStack(Blocks.planks));
    }

    @Test
    public void ghostClicksDoNotMoveOrCreateItems() {
        player.inventory.setItemStack(new ItemStack(Blocks.planks, 12));
        container.slotClick(1, 0, 0, player);
        assertEquals(12, player.inventory.getItemStack().stackSize);
        assertEquals(1, tile.getPattern(0).stackSize);
        for (int mode : new int[] { 2, 3, 4, 5, 6 }) container.slotClick(1, 0, mode, player);
        assertEquals(12, player.inventory.getItemStack().stackSize);
        assertEquals(1, tile.getPattern(0).stackSize);
        container.slotClick(1, 1, 0, player);
        assertNull(tile.getPattern(0));
        assertEquals(12, player.inventory.getItemStack().stackSize);
    }

    @Test
    public void craftingConsumesOnlyAvailableSetsAndRemembersRecipe() {
        sticks();
        tile.setInventorySlotContents(0, new ItemStack(Blocks.planks, 3));
        container.slotClick(0, 0, 0, player);
        assertEquals(
            Items.stick,
            player.inventory.getItemStack()
                .getItem());
        assertEquals(4, player.inventory.getItemStack().stackSize);
        assertEquals(1, tile.getStackInSlot(0).stackSize);
        assertNotNull(
            tile.getMemory()
                .get(0));
        container.slotClick(0, 0, 0, player);
        assertEquals(4, player.inventory.getItemStack().stackSize);
        assertEquals(1, tile.getStackInSlot(0).stackSize);
    }

    @Test
    public void fullCursorOrInventoryDoesNotConsumeIngredients() {
        sticks();
        tile.setInventorySlotContents(0, new ItemStack(Blocks.planks, 64));
        player.inventory.setItemStack(new ItemStack(Items.stick, 63));
        container.slotClick(0, 0, 0, player);
        assertEquals(64, tile.getStackInSlot(0).stackSize);
        for (int i = 0; i < 36; i++) player.inventory.setInventorySlotContents(i, new ItemStack(Items.diamond, 64));
        container.slotClick(0, 0, 1, player);
        assertEquals(64, tile.getStackInSlot(0).stackSize);
    }

    @Test
    public void shiftCraftingIsBoundedAndHotbarCraftingConsumesOnce() {
        sticks();
        tile.setInventorySlotContents(0, new ItemStack(Blocks.planks, 64));
        container.slotClick(0, 0, 1, player);
        int sticks = count(player, Items.stick);
        assertTrue(sticks > 0 && sticks <= 64);
        assertEquals(64 - sticks / 2, tile.getStackInSlot(0).stackSize);
        container.slotClick(0, 8, 2, player);
        assertEquals(4, player.inventory.getStackInSlot(8).stackSize);
        assertEquals(64 - sticks / 2 - 2, tile.getStackInSlot(0).stackSize);
    }

    @Test
    public void sourceModesReserveDuplicateIngredientsAcrossInventories() {
        sticks();
        tile.setInventorySlotContents(0, new ItemStack(Blocks.planks));
        player.inventory.setInventorySlotContents(0, new ItemStack(Blocks.planks));
        assertNull(tile.plan(player));
        tile.cycleSource();
        assertNull(tile.plan(player));
        tile.cycleSource();
        container.slotClick(0, 0, 0, player);
        assertNull(tile.getStackInSlot(0));
        assertNull(player.inventory.getStackInSlot(0));
        assertEquals(4, player.inventory.getItemStack().stackSize);
    }

    @Test
    public void oreSubstitutionMustPreserveTheSelectedOutput() {
        sticks();
        tile.setInventorySlotContents(0, new ItemStack(Blocks.planks, 2, 1));
        assertNotNull(tile.plan(player));
        tile.clearPattern();
        tile.setPattern(0, new ItemStack(Blocks.log, 1, 0));
        tile.setInventorySlotContents(0, new ItemStack(Blocks.log, 2, 1));
        assertEquals(
            0,
            tile.getOutput()
                .getItemDamage());
        assertNull(tile.plan(player));
    }

    @Test
    public void bucketsAreReturnedToIngredientStorage() {
        ItemStack[] cake = { new ItemStack(Items.milk_bucket), new ItemStack(Items.milk_bucket),
            new ItemStack(Items.milk_bucket), new ItemStack(Items.sugar), new ItemStack(Items.egg),
            new ItemStack(Items.sugar), new ItemStack(Items.wheat), new ItemStack(Items.wheat),
            new ItemStack(Items.wheat) };
        tile.setPattern(cake);
        for (int i = 0; i < cake.length; i++) tile.setInventorySlotContents(i, cake[i].copy());
        container.slotClick(0, 0, 0, player);
        assertEquals(
            Items.cake,
            player.inventory.getItemStack()
                .getItem());
        int buckets = 0;
        for (int i = 0; i < 18; i++) if (tile.getStackInSlot(i) != null) {
            assertEquals(
                Items.bucket,
                tile.getStackInSlot(i)
                    .getItem());
            buckets += tile.getStackInSlot(i).stackSize;
        }
        assertEquals(3, buckets);
    }

    @Test
    public void toolsKeepTheirDamageAndCanBeReusedUntilBroken() {
        Item tool = new Item() {

            @Override
            public boolean hasContainerItem(ItemStack stack) {
                return true;
            }

            @Override
            public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack getContainerItem(ItemStack stack) {
                ItemStack result = stack.copy();
                result.stackSize = 1;
                result.setItemDamage(stack.getItemDamage() + 1);
                return result;
            }
        }.setMaxStackSize(1)
            .setMaxDamage(2);
        IRecipe recipe = new ShapelessOreRecipe(
            new ItemStack(Items.paper),
            new ItemStack(tool, 1, OreDictionary.WILDCARD_VALUE),
            Items.iron_ingot);
        CraftingManager.getInstance()
            .getRecipeList()
            .add(recipe);
        try {
            tile.setPattern(0, new ItemStack(tool));
            tile.setPattern(1, new ItemStack(Items.iron_ingot));
            tile.setInventorySlotContents(0, new ItemStack(tool));
            tile.setInventorySlotContents(1, new ItemStack(Items.iron_ingot, 4));
            for (int i = 1; i <= 3; i++) container.slotClick(0, 0, 0, player);
            assertEquals(3, player.inventory.getItemStack().stackSize);
            assertNull(tile.getStackInSlot(0));
            assertNull(tile.plan(player));
            assertEquals(1, tile.getStackInSlot(1).stackSize);
        } finally {
            CraftingManager.getInstance()
                .getRecipeList()
                .remove(recipe);
        }
    }

    @Test
    public void conflictingRecipesAndSelectionSurviveReload() {
        IRecipe first = new ShapelessOreRecipe(new ItemStack(Items.gold_ingot), Items.diamond, Items.apple);
        IRecipe second = new ShapelessOreRecipe(new ItemStack(Items.iron_ingot), Items.diamond, Items.apple);
        CraftingManager.getInstance()
            .getRecipeList()
            .add(first);
        CraftingManager.getInstance()
            .getRecipeList()
            .add(second);
        try {
            tile.setPattern(0, new ItemStack(Items.diamond));
            tile.setPattern(1, new ItemStack(Items.apple));
            assertTrue(tile.hasConflicts());
            tile.cycleRecipe(1);
            assertEquals(
                Items.iron_ingot,
                tile.getOutput()
                    .getItem());
            NBTTagCompound saved = new NBTTagCompound();
            // The anonymous test tile is not in the TE registry; use the production class for persistence.
            TileWorktable original = new TileWorktable();
            original.setPattern(0, tile.getPattern(0));
            original.setPattern(1, tile.getPattern(1));
            original.preferOutput(tile.getOutput());
            original.setInventorySlotContents(17, new ItemStack(Items.apple, 31));
            original.cycleSource();
            original.cycleSource();
            original.getMemory()
                .remember(
                    new ItemStack[] { tile.getPattern(0), tile.getPattern(1), null, null, null, null, null, null,
                        null },
                    tile.getOutput());
            original.getMemory()
                .toggleLock(0);
            original.writeToNBT(saved);
            TileWorktable restored = new TileWorktable();
            restored.readFromNBT(saved);
            assertEquals(
                Items.iron_ingot,
                restored.getOutput()
                    .getItem());
            assertEquals(31, restored.getStackInSlot(17).stackSize);
            assertEquals(2, restored.getSource());
            assertTrue(
                restored.getMemory()
                    .get(0).locked);
            restored.clearPattern();
            restored.selectMemory(0);
            assertEquals(
                Items.iron_ingot,
                restored.getOutput()
                    .getItem());
        } finally {
            CraftingManager.getInstance()
                .getRecipeList()
                .remove(first);
            CraftingManager.getInstance()
                .getRecipeList()
                .remove(second);
        }
    }

    @Test
    public void lockedRecipesAreNotEvictedAndRecallCannotGrantOutput() {
        RecipeMemory memory = tile.getMemory();
        ItemStack[] pattern = new ItemStack[9];
        pattern[0] = new ItemStack(Items.apple);
        for (int i = 0; i < 9; i++) {
            memory.remember(pattern, new ItemStack(Items.dye, 1, i));
            memory.toggleLock(i);
        }
        memory.remember(pattern, new ItemStack(Items.diamond));
        for (int i = 0; i < 9; i++) assertEquals(Items.dye, memory.get(i).output.getItem());
        memory.toggleLock(4);
        memory.remember(pattern, new ItemStack(Items.diamond));
        assertEquals(Items.diamond, memory.get(8).output.getItem());
        tile.selectMemory(8);
        container.slotClick(0, 0, 0, player);
        assertNull(player.inventory.getItemStack());
        assertNull(tile.getOutput());
    }

    @Test
    public void twoViewersCannotCraftTheSameIngredientsTwice() {
        sticks();
        tile.setInventorySlotContents(0, new ItemStack(Blocks.planks, 2));
        EntityPlayer other = new TestPlayer(new TestWorld());
        ContainerWorktable second = new ContainerWorktable(other, tile);
        container.slotClick(0, 0, 0, player);
        second.slotClick(0, 0, 0, other);
        assertEquals(4, player.inventory.getItemStack().stackSize);
        assertNull(other.inventory.getItemStack());
    }

    @Test
    public void neiPacketRoundTripsAndDoesNotTrustRequestedOutput() {
        ItemStack[] pattern = new ItemStack[9];
        pattern[0] = new ItemStack(Blocks.log);
        ByteBuf encoded = Unpooled.buffer();
        ByteBuf copied = Unpooled.buffer();
        try {
            new WorktableNetwork.Transfer(3, pattern, new ItemStack(Items.diamond)).toBytes(encoded);
            WorktableNetwork.Transfer decoded = new WorktableNetwork.Transfer();
            decoded.fromBytes(encoded.duplicate());
            decoded.toBytes(copied);
            assertEquals(encoded, copied);
        } finally {
            encoded.release();
            copied.release();
        }
        tile.setPattern(pattern);
        tile.preferOutput(new ItemStack(Items.diamond));
        assertEquals(
            Item.getItemFromBlock(Blocks.planks),
            tile.getOutput()
                .getItem());
    }

    private static int count(EntityPlayer player, Item item) {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == item) count += stack.stackSize;
        }
        return count;
    }

    private static class TestPlayer extends EntityPlayer {

        TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "WorktableTest"));
        }

        @Override
        public ChunkCoordinates getPlayerCoordinates() {
            return new ChunkCoordinates(0, 64, 0);
        }

        @Override
        public void addChatMessage(IChatComponent message) {}

        @Override
        public boolean canCommandSenderUseCommand(int permission, String command) {
            return true;
        }
    }

    private static class TestWorld extends World {

        TestWorld() {
            super(
                null,
                "WorktableTest",
                new WorldProviderSurface(),
                new WorldSettings(0, WorldSettings.GameType.SURVIVAL, false, false, WorldType.FLAT),
                new Profiler());
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return null;
        }

        @Override
        protected int func_152379_p() {
            return 0;
        }

        @Override
        public Entity getEntityByID(int id) {
            return null;
        }

        @Override
        public ChunkCoordinates getSpawnPoint() {
            return new ChunkCoordinates(0, 64, 0);
        }
    }
}
