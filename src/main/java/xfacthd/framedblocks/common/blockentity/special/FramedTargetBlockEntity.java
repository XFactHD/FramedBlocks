package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.common.FBContent;

public class FramedTargetBlockEntity extends FramedBlockEntity
{
    private DyeColor overlayColor = DyeColor.RED;

    public FramedTargetBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_TARGET.value(), pos, state);
    }

    public boolean setOverlayColor(DyeColor overlayColor)
    {
        if (this.overlayColor != overlayColor)
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                this.overlayColor = overlayColor;

                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                setChanged();
            }

            return true;
        }
        return false;
    }

    public int getOverlayColor()
    {
        return overlayColor.getTextColor();
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("overlay_color", overlayColor.getId());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt)
    {
        super.handleUpdateTag(nbt);
        if (nbt.contains("overlay_color"))
        {
            overlayColor = DyeColor.byId(nbt.getInt("overlay_color"));
        }
    }

    @Override
    protected void writeToDataPacket(CompoundTag tag)
    {
        super.writeToDataPacket(tag);
        tag.putInt("overlay_color", overlayColor.getId());
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        boolean colored = false;
        if (nbt.contains("overlay_color"))
        {
            DyeColor color = DyeColor.byId(nbt.getInt("overlay_color"));
            if (overlayColor != color)
            {
                overlayColor = color;
                colored = true;
            }
        }
        return super.readFromDataPacket(nbt) || colored;
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putInt("overlay_color", overlayColor.getId());
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        if (tag.contains("overlay_color"))
        {
            overlayColor = DyeColor.byId(tag.getInt("overlay_color"));
        }
    }
}
