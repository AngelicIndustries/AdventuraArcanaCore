package industries.angelic.adventuraarcanacore.compat.chests;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import industries.angelic.adventuraarcanacore.core.PatchSwitches;

/**
 * Chest pairs on top of AdjaChest, which pairs a chest with one neighbor of its kind facing the same way, so any number
 * can stand side by side. A chest placed while sneaking pairs only on purpose: placed against the side of a single
 * chest of its kind, it takes that chest's facing and pairs with it; placed anywhere else, it stays single. Such a
 * manual chest never pairs on its own later, and no neighbor takes it. The Aether's chests, copies of the old vanilla
 * chest, pair the same way through AAC.
 */
public final class ChestPairs {

    /** Saved and sent with a manual chest. */
    public static final String MANUAL_TAG = "AACChestManual";
    /** The direction an Aether chest is paired in, as a ForgeDirection ordinal. */
    public static final String PAIR_TAG = "AACChestPair";

    /** Whether chests placed while sneaking pair on purpose; the Aether's chests ask it at runtime. */
    public static final boolean SNEAK_PLACING = PatchSwitches.enabled("chestSneakPlacing");

    private static final ForgeDirection[] ALONG_X = { ForgeDirection.WEST, ForgeDirection.EAST };
    private static final ForgeDirection[] ALONG_Z = { ForgeDirection.NORTH, ForgeDirection.SOUTH };
    private static final ThreadLocal<Click> CLICK = new ThreadLocal<>();

    private ChestPairs() {}

    /** A chest that pairs with at most one neighbor, in the direction it keeps. */
    public interface Pairable {

        /** UNKNOWN when single. */
        ForgeDirection adventuraarcanacore$pairedTo();

        void adventuraarcanacore$pairTo(ForgeDirection direction);

        boolean adventuraarcanacore$manual();

        void adventuraarcanacore$setManual(boolean manual);

        /** Whether the block at the spot is a chest of this one's kind. */
        boolean adventuraarcanacore$pairsWith(int x, int y, int z);

        /** Looks at its neighbors again when it next ticks or is drawn. */
        void adventuraarcanacore$recheck();

        /** Takes the name of the stack that placed it. */
        void adventuraarcanacore$placed(ItemStack stack);
    }

    private static final class Click {

        final EntityPlayer player;
        final int x, y, z, side;

        Click(EntityPlayer player, int x, int y, int z, int side) {
            this.player = player;
            this.x = x;
            this.y = y;
            this.z = z;
            this.side = side;
        }
    }

    /** A player uses an item on a block face, which a chest it places goes against. */
    public static void clicking(EntityPlayer player, int x, int y, int z, int side) {
        CLICK.set(new Click(player, x, y, z, side));
    }

    public static void clicked() {
        CLICK.remove();
    }

    /** The facing (metadata) vanilla gives a chest the entity places: towards it. */
    public static int facing(EntityLivingBase placer) {
        switch (MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3) {
            case 0:
                return 2;
            case 1:
                return 5;
            case 2:
                return 3;
            default:
                return 4;
        }
    }

    /** Whether chests facing {@code meta} can pair along {@code direction}: side by side, not front to back. */
    public static boolean sideBySide(int meta, ForgeDirection direction) {
        return meta >= 2 && meta <= 5
            && direction.offsetY == 0
            && direction != ForgeDirection.UNKNOWN
            && (meta & 6) != (direction.ordinal() & 6);
    }

    /** The chest the tile is paired with, if that one is paired back and both still fit, or null. */
    public static Pairable partner(TileEntity tile) {
        if (!(tile instanceof Pairable) || !tile.hasWorldObj()) return null;
        Pairable chest = (Pairable) tile;
        ForgeDirection direction = chest.adventuraarcanacore$pairedTo();
        if (direction == ForgeDirection.UNKNOWN) return null;
        World world = tile.getWorldObj();
        int x = tile.xCoord + direction.offsetX, y = tile.yCoord, z = tile.zCoord + direction.offsetZ;
        int meta = world.getBlockMetadata(tile.xCoord, y, tile.zCoord);
        TileEntity other = world.getTileEntity(x, y, z);
        return other instanceof Pairable && ((Pairable) other).adventuraarcanacore$pairedTo() == direction.getOpposite()
            && chest.adventuraarcanacore$pairsWith(x, y, z)
            && world.getBlockMetadata(x, y, z) == meta
            && sideBySide(meta, direction) ? (Pairable) other : null;
    }

    /** Whether the spot holds the tile's partner. */
    public static boolean isPartnerAt(TileEntity tile, int x, int y, int z) {
        ForgeDirection direction = ((Pairable) tile).adventuraarcanacore$pairedTo();
        return direction != ForgeDirection.UNKNOWN && y == tile.yCoord
            && x == tile.xCoord + direction.offsetX
            && z == tile.zCoord + direction.offsetZ
            && partner(tile) != null;
    }

    /** Whether the player sneaks, so the chest it places pairs only on purpose. */
    public static boolean sneaking(EntityLivingBase placer) {
        return SNEAK_PLACING && placer instanceof EntityPlayer && placer.isSneaking();
    }

    /**
     * Faces and pairs a chest placed while sneaking, after the block face it went against: the side of a single chest
     * of its kind, it pairs with that one and takes its facing; anything else, it faces the player and stays single.
     */
    public static void sneakPlaced(World world, int x, int y, int z, EntityLivingBase placer, Pairable chest) {
        int meta = facing(placer);
        TileEntity target = null;
        ForgeDirection toTarget = ForgeDirection.UNKNOWN;
        Click click = CLICK.get();
        if (click != null && click.player == placer) {
            ForgeDirection face = ForgeDirection.getOrientation(click.side);
            int theirMeta = world.getBlockMetadata(click.x, click.y, click.z);
            TileEntity clicked = world.getTileEntity(click.x, click.y, click.z);
            if (click.x + face.offsetX == x && click.y + face.offsetY == y
                && click.z + face.offsetZ == z
                && sideBySide(theirMeta, face)
                && clicked instanceof Pairable
                && chest.adventuraarcanacore$pairsWith(click.x, click.y, click.z)
                && partner(clicked) == null) {
                target = clicked;
                toTarget = face.getOpposite();
                meta = theirMeta;
            }
        }
        world.setBlockMetadataWithNotify(x, y, z, meta, 3);
        chest.adventuraarcanacore$setManual(true);
        chest.adventuraarcanacore$pairTo(toTarget);
        chest.adventuraarcanacore$recheck();
        world.markBlockForUpdate(x, y, z);
        // A client learns the target's side from the server, which may also turn the placement down.
        if (target != null && !world.isRemote) {
            ((Pairable) target).adventuraarcanacore$pairTo(toTarget.getOpposite());
            ((Pairable) target).adventuraarcanacore$recheck();
            world.markBlockForUpdate(target.xCoord, target.yCoord, target.zCoord);
        }
    }

    /**
     * Pairs an Aether chest as AdjaChest pairs vanilla ones: it keeps a pair that still fits, else takes the first
     * neighbor of its kind facing the same way that is free, the one on its left first. Manual chests take none and are
     * taken by none. Only the server decides; clients show the pairs they are sent.
     */
    public static void resolve(TileEntity tile) {
        World world = tile.getWorldObj();
        if (world == null || world.isRemote || tile.isInvalid() || partner(tile) != null) return;
        Pairable chest = (Pairable) tile;
        int x = tile.xCoord, y = tile.yCoord, z = tile.zCoord;
        int meta = world.getBlockMetadata(x, y, z);
        ForgeDirection found = ForgeDirection.UNKNOWN;
        TileEntity mate = null;
        if (!chest.adventuraarcanacore$manual() && meta >= 2 && meta <= 5) {
            for (ForgeDirection direction : meta <= 3 ? ALONG_X : ALONG_Z) {
                int nx = x + direction.offsetX, nz = z + direction.offsetZ;
                if (!chest.adventuraarcanacore$pairsWith(nx, y, nz) || world.getBlockMetadata(nx, y, nz) != meta)
                    continue;
                TileEntity other = world.getTileEntity(nx, y, nz);
                if (!(other instanceof Pairable) || ((Pairable) other).adventuraarcanacore$manual()
                    || partner(other) != null) continue;
                found = direction;
                mate = other;
                break;
            }
        }
        if (chest.adventuraarcanacore$pairedTo() != found) {
            chest.adventuraarcanacore$pairTo(found);
            world.markBlockForUpdate(x, y, z);
        }
        if (mate != null) {
            ((Pairable) mate).adventuraarcanacore$pairTo(found.getOpposite());
            ((Pairable) mate).adventuraarcanacore$recheck();
            world.markBlockForUpdate(mate.xCoord, mate.yCoord, mate.zCoord);
        }
    }

    /** A pair update reached the client: the chest and the one it points at look at their neighbors again. */
    public static void received(TileEntity tile) {
        Pairable chest = (Pairable) tile;
        chest.adventuraarcanacore$recheck();
        ForgeDirection direction = chest.adventuraarcanacore$pairedTo();
        if (direction == ForgeDirection.UNKNOWN || !tile.hasWorldObj()) return;
        TileEntity other = tile.getWorldObj()
            .getTileEntity(tile.xCoord + direction.offsetX, tile.yCoord, tile.zCoord + direction.offsetZ);
        if (other instanceof Pairable) ((Pairable) other).adventuraarcanacore$recheck();
    }

    /** The chest's bounds, reaching into its partner's block. */
    public static void bounds(Block block, ForgeDirection direction) {
        switch (direction) {
            case NORTH:
                block.setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
                break;
            case SOUTH:
                block.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
                break;
            case WEST:
                block.setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
                break;
            case EAST:
                block.setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
                break;
            default:
                block.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }
    }
}
