package xfacthd.framedblocks.common.blockentity;

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
        super(FBContent.blockEntityTypeFramedTarget.get(), pos, state);
    }

    public boolean setOverlayColor(DyeColor overlayColor)
    {
        if (this.overlayColor != overlayColor)
        {
            //noinspection ConstantConditions
            if (level.isClientSide())
            {
                this.overlayColor = overlayColor;

                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                setChanged();
            }

            return true;
        }
        return false;
    }

    public int getOverlayColor() { return overlayColor.getTextColor(); }

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
    }

    @Override
    protected void writeToDataPacket(CompoundTag tag)
    {
        super.writeToDataPacket(tag);
        if (tag.contains("overlay_color"))
        {
            overlayColor = DyeColor.byId(tag.getInt("overlay_color"));
        }
    }

    @Override
    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        return super.readFromDataPacket(nbt);
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
