package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoubleSlopedPrismBlockEntity extends FramedDoublePrismBlockEntity
{
    public FramedDoubleSlopedPrismBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedDoubleSlopedPrism.get(), pos, state);
    }

    @Override
    protected boolean isDoubleSide(Direction side)
    {
        return side == getBlockState().getValue(PropertyHolder.ORIENTATION);
    }
}
