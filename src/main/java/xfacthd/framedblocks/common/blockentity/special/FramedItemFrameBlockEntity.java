package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.saveddata.maps.MapFrame;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Objects;

public class FramedItemFrameBlockEntity extends FramedBlockEntity
{
    public static final int ROTATION_STEPS = 8;
    private static final int MAP_UPDATE_INTERVAL = 10;
    public static final String NBT_KEY_FRAMED_MAP = FramedConstants.MOD_ID + ":framed";

    private final boolean glowing;
    private ItemStack heldItem = ItemStack.EMPTY;
    private int rotation = 0;
    private int mapTickOffset = 0;
    private long mapTickCount = 0;

    public FramedItemFrameBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_ITEM_FRAME.value(), pos, state);
        this.glowing = getBlockType() == BlockType.FRAMED_GLOWING_ITEM_FRAME;
    }

    public void tickWithMap()
    {
        if (mapTickCount % MAP_UPDATE_INTERVAL == 0)
        {
            if (mapTickCount == 0)
            {
                // Only start ticking with offset after first tick. Ensures the client will receive the map data ASAP
                mapTickCount = mapTickOffset;
            }

            //noinspection ConstantConditions
            MapItemSavedData mapData = MapItem.getSavedData(heldItem, level);
            if (mapData != null)
            {
                int mapId = Objects.requireNonNull(MapItem.getMapId(heldItem));
                for (Player player : level.players())
                {
                    mapData.tickCarriedBy(player, heldItem);
                    Packet<?> packet = mapData.getUpdatePacket(mapId, player);
                    if (packet != null)
                    {
                        ((ServerPlayer) player).connection.send(packet);
                    }
                }
            }
        }
        mapTickCount++;
    }

    public InteractionResult handleFrameInteraction(Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() && hasItem())
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                rotation = (rotation + 1) % ROTATION_STEPS;

                playSound(glowing ? SoundEvents.GLOW_ITEM_FRAME_ROTATE_ITEM : SoundEvents.ITEM_FRAME_ROTATE_ITEM);

                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        else if (!stack.isEmpty() && !hasItem())
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                setItem(stack);
                if (!player.isCreative())
                {
                    stack.shrink(1);
                }
                player.getInventory().setChanged();

                playSound(glowing ? SoundEvents.GLOW_ITEM_FRAME_ADD_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    public void removeItem(Player player)
    {
        if (heldItem.getItem() instanceof MapItem && heldItem.hasTag())
        {
            //noinspection ConstantConditions
            heldItem.getTag().remove(NBT_KEY_FRAMED_MAP);
            //noinspection ConstantConditions
            MapItemSavedData mapData = MapItem.getSavedData(heldItem, level);
            if (mapData instanceof MapMarkerRemover remover)
            {
                remover.framedblocks$removeMapMarker(worldPosition);
            }
        }

        if (!player.isCreative() && !player.getInventory().add(heldItem))
        {
            player.drop(heldItem, false);
        }

        rotation = 0;
        setItem(ItemStack.EMPTY);

        playSound(glowing ? SoundEvents.GLOW_ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_REMOVE_ITEM);
    }

    private void setItem(ItemStack item)
    {
        if (item.isEmpty())
        {
            heldItem = ItemStack.EMPTY;
        }
        else
        {
            heldItem = item.copy();
            heldItem.setCount(1);

            if (heldItem.getItem() instanceof MapItem)
            {
                Direction dir = getBlockState().getValue(BlockStateProperties.FACING).getOpposite();
                CompoundTag tag = new CompoundTag();
                tag.putLong("pos", worldPosition.asLong());
                tag.putByte("y_rot", (byte) dir.get2DDataValue());
                heldItem.getOrCreateTag().put(NBT_KEY_FRAMED_MAP, tag);
            }
        }

        setChanged();
        if (!changeMapStateIfNeeded())
        {
            //noinspection ConstantConditions
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public boolean hasItem()
    {
        return !heldItem.isEmpty();
    }

    public ItemStack getItem()
    {
        return heldItem;
    }

    public ItemStack getCloneItem()
    {
        ItemStack stack = heldItem.copy();
        if (stack.hasTag())
        {
            //noinspection ConstantConditions
            stack.getTag().remove(NBT_KEY_FRAMED_MAP);
        }
        return stack;
    }

    public int getRotation()
    {
        return rotation;
    }

    public boolean isGlowingFrame()
    {
        return glowing;
    }

    private boolean changeMapStateIfNeeded()
    {
        boolean mapItem = !heldItem.isEmpty() && heldItem.getItem() instanceof MapItem;
        boolean mapState = getBlockState().getValue(PropertyHolder.MAP_FRAME);

        if (mapItem != mapState)
        {
            //noinspection ConstantConditions
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(PropertyHolder.MAP_FRAME, mapItem));
            mapTickCount = mapTickOffset = mapItem ? level.random.nextInt(MAP_UPDATE_INTERVAL) : 0;
            return true;
        }
        return false;
    }

    private void playSound(SoundEvent sound)
    {
        //noinspection ConstantConditions
        level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), sound, SoundSource.BLOCKS, 1F, 1F);
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean dropCamo)
    {
        super.addAdditionalDrops(drops, dropCamo);
        if (!heldItem.isEmpty())
        {
            drops.add(getCloneItem());
        }
    }

    // Network

    private void readFromNetwork(CompoundTag tag)
    {
        heldItem = ItemStack.of(tag.getCompound("item"));
        rotation = tag.getByte("rotation");
    }

    private void writeToNetwork(CompoundTag tag)
    {
        tag.put("item", heldItem.save(new CompoundTag()));
        tag.putByte("rotation", (byte) rotation);
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag tag)
    {
        readFromNetwork(tag);
        return super.readFromDataPacket(tag);
    }

    @Override
    protected void writeToDataPacket(CompoundTag tag)
    {
        super.writeToDataPacket(tag);
        writeToNetwork(tag);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag)
    {
        super.handleUpdateTag(tag);
        readFromNetwork(tag);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag tag = super.getUpdateTag();
        writeToNetwork(tag);
        return tag;
    }

    // NBT

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);

        heldItem = ItemStack.of(tag.getCompound("item"));
        rotation = tag.getByte("rotation");
        mapTickOffset = tag.getInt("map_tick_offset");
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);

        tag.put("item", heldItem.save(new CompoundTag()));
        tag.putByte("rotation", (byte) rotation);
        tag.putInt("map_tick_offset", mapTickOffset);
    }



    public record FramedMap(BlockPos pos, int yRot)
    {
        public static FramedMap load(CompoundTag tag)
        {
            return new FramedMap(
                    BlockPos.of(tag.getLong("pos")),
                    tag.getInt("y_rot")
            );
        }

        public CompoundTag save()
        {
            CompoundTag tag = new CompoundTag();
            tag.putLong("pos", pos.asLong());
            tag.putInt("y_rot", yRot);
            return tag;
        }

        public static String makeFrameId(BlockPos pos)
        {
            return FramedConstants.MOD_ID + ":" + MapFrame.frameId(pos);
        }
    }

    public interface MapMarkerRemover
    {
        void framedblocks$removeMapMarker(BlockPos pos);
    }
}
