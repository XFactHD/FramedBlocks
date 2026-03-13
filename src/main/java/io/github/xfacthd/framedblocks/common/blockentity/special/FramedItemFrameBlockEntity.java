package io.github.xfacthd.framedblocks.common.blockentity.special;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.NetworkValueInput;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.component.FramedMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.function.Consumer;

public class FramedItemFrameBlockEntity extends FramedBlockEntity implements ItemOwner
{
    public static final int ROTATION_STEPS = 8;
    private static final int MAP_UPDATE_INTERVAL = 10;
    public static final String ITEM_NBT_KEY = "item";

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

    public InteractionResult handleFrameInteraction(Player player, InteractionHand hand)
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
            return InteractionResult.SUCCESS;
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
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
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
    public void addAdditionalDrops(Consumer<ItemStack> drops, boolean dropCamo)
    {
        super.addAdditionalDrops(drops, dropCamo);
        if (!heldItem.isEmpty())
        {
            drops.accept(getCloneItem());
        }
    }

    @Override
    public Vec3 position()
    {
        return new Vec3(worldPosition);
    }

    @Override
    public float getVisualRotationYInDegrees()
    {
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING).getOpposite();
        int yRot = facing.getAxis().isVertical() ? 90 * facing.getAxisDirection().getStep() : 0;
        return Mth.wrapDegrees(180 + facing.get2DDataValue() * 90 + getRotation() * 45 + yRot);
    }

    // Network

    @Override
    protected void readFromDataPacket(NetworkValueInput input)
    {
        super.readFromDataPacket(input);

        heldItem = input.read(ITEM_NBT_KEY, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        rotation = input.getByteOr("rotation", (byte) 0);
    }

    @Override
    protected void writeToDataPacket(ValueOutput valueOutput)
    {
        super.writeToDataPacket(valueOutput);
        valueOutput.store(ITEM_NBT_KEY, ItemStack.OPTIONAL_CODEC, heldItem);
        valueOutput.putByte("rotation", (byte) rotation);
    }

    // NBT

    @Override
    public void loadAdditional(ValueInput valueInput)
    {
        super.loadAdditional(valueInput);

        heldItem = valueInput.read(ITEM_NBT_KEY, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        rotation = valueInput.getByteOr("rotation", (byte) 0);
        mapTickOffset = valueInput.getIntOr("map_tick_offset", 0);
    }

    @Override
    public void saveAdditional(ValueOutput valueInput)
    {
        super.saveAdditional(valueInput);

        valueInput.store(ITEM_NBT_KEY, ItemStack.OPTIONAL_CODEC, heldItem);
        valueInput.putByte("rotation", (byte) rotation);
        valueInput.putInt("map_tick_offset", mapTickOffset);
    }
}
