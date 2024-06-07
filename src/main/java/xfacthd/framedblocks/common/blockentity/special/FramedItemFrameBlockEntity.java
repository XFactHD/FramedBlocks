package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.component.FramedMap;

import java.util.List;
import java.util.Objects;

public class FramedItemFrameBlockEntity extends FramedBlockEntity
{
    public static final int ROTATION_STEPS = 8;
    private static final int MAP_UPDATE_INTERVAL = 10;

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

            MapItemSavedData mapData = MapItem.getSavedData(heldItem, level());
            if (mapData != null)
            {
                MapId mapId = Objects.requireNonNull(heldItem.get(DataComponents.MAP_ID));
                for (Player player : level().players())
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

    public ItemInteractionResult handleFrameInteraction(Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (hasItem())
        {
            if (!level().isClientSide())
            {
                rotation = (rotation + 1) % ROTATION_STEPS;

                playSound(glowing ? SoundEvents.GLOW_ITEM_FRAME_ROTATE_ITEM : SoundEvents.ITEM_FRAME_ROTATE_ITEM);

                setChangedWithoutSignalUpdate();
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            return ItemInteractionResult.sidedSuccess(level().isClientSide());
        }
        else if (!stack.isEmpty() && !hasItem())
        {
            if (!level().isClientSide())
            {
                setItem(stack);
                if (!player.isCreative())
                {
                    stack.shrink(1);
                }
                player.getInventory().setChanged();

                playSound(glowing ? SoundEvents.GLOW_ITEM_FRAME_ADD_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM);
            }
            return ItemInteractionResult.sidedSuccess(level().isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void removeItem(Player player)
    {
        if (heldItem.getItem() instanceof MapItem)
        {
            FramedMap map = heldItem.remove(FBContent.DC_TYPE_FRAMED_MAP);
            if (map != null)
            {
                MapItemSavedData mapData = MapItem.getSavedData(heldItem, level());
                if (mapData instanceof FramedMap.MarkerRemover remover)
                {
                    remover.framedblocks$removeMapMarker(worldPosition);
                }
            }
        }

        Utils.giveToPlayer(player, heldItem, true);

        // Don't clear rotation
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
                heldItem.set(FBContent.DC_TYPE_FRAMED_MAP, new FramedMap(worldPosition, dir));
            }
        }

        setChangedWithoutSignalUpdate();
        if (!changeMapStateIfNeeded())
        {
            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
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
        stack.remove(FBContent.DC_TYPE_FRAMED_MAP);
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
            level().setBlockAndUpdate(worldPosition, getBlockState().setValue(PropertyHolder.MAP_FRAME, mapItem));
            mapTickCount = mapTickOffset = mapItem ? level().random.nextInt(MAP_UPDATE_INTERVAL) : 0;
            return true;
        }
        return false;
    }

    private void playSound(SoundEvent sound)
    {
        level().playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), sound, SoundSource.BLOCKS, 1F, 1F);
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

    private void readFromNetwork(CompoundTag tag, HolderLookup.Provider provider)
    {
        heldItem = ItemStack.parseOptional(provider, tag.getCompound("item"));
        rotation = tag.getByte("rotation");
    }

    private void writeToNetwork(CompoundTag tag, HolderLookup.Provider provider)
    {
        tag.put("item", heldItem.saveOptional(provider));
        tag.putByte("rotation", (byte) rotation);
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag tag, HolderLookup.Provider lookupProvider)
    {
        readFromNetwork(tag, lookupProvider);
        return super.readFromDataPacket(tag, lookupProvider);
    }

    @Override
    protected void writeToDataPacket(CompoundTag tag, HolderLookup.Provider lookupProvider)
    {
        super.writeToDataPacket(tag, lookupProvider);
        writeToNetwork(tag, lookupProvider);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.handleUpdateTag(tag, provider);
        readFromNetwork(tag, provider);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider)
    {
        CompoundTag tag = super.getUpdateTag(provider);
        writeToNetwork(tag, provider);
        return tag;
    }

    // NBT

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);

        heldItem = ItemStack.parseOptional(provider, tag.getCompound("item"));
        rotation = tag.getByte("rotation");
        mapTickOffset = tag.getInt("map_tick_offset");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);

        tag.put("item", heldItem.saveOptional(provider));
        tag.putByte("rotation", (byte) rotation);
        tag.putInt("map_tick_offset", mapTickOffset);
    }
}
