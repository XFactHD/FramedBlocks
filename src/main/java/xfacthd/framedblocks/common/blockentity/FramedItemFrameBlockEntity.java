package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedItemFrameBlockEntity extends FramedBlockEntity
{
    public static final int ROTATION_STEPS = 8;

    private final boolean glowing;
    private ItemStack heldItem = ItemStack.EMPTY;
    private int rotation = 0;

    public FramedItemFrameBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedItemFrame.get(), pos, state);
        this.glowing = getBlockType() == BlockType.FRAMED_GLOWING_ITEM_FRAME;
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
        }

        setChanged();
        if (!changeMapStateIfNeeded())
        {
            //noinspection ConstantConditions
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public boolean hasItem() { return !heldItem.isEmpty(); }

    public ItemStack getItem() { return heldItem; }

    public int getRotation() { return rotation; }

    public boolean isGlowingFrame() { return glowing; }

    private boolean changeMapStateIfNeeded()
    {
        boolean mapItem = !heldItem.isEmpty() && heldItem.getItem() instanceof MapItem;
        boolean mapState = getBlockState().getValue(PropertyHolder.MAP_FRAME);

        if (mapItem != mapState)
        {
            //noinspection ConstantConditions
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(PropertyHolder.MAP_FRAME, mapItem));
            return true;
        }
        return false;
    }

    private void playSound(SoundEvent sound)
    {
        //noinspection ConstantConditions
        level.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), sound, SoundSource.BLOCKS, 1F, 1F);
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
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);

        tag.put("item", heldItem.save(new CompoundTag()));
        tag.putByte("rotation", (byte) rotation);
    }
}
