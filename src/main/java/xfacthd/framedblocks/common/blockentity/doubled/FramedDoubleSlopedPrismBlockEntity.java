package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoubleSlopedPrismBlockEntity extends FramedDoublePrismBlockEntity
{
    public FramedDoubleSlopedPrismBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DOUBLE_SLOPED_PRISM.get(), pos, state);
    }

    @Override
    protected boolean isDoubleSide(Direction side)
    {
        return side == getBlockState().getValue(PropertyHolder.FACING_DIR).orientation();
    }

    @Override
    protected Direction getFacing(BlockState state)
    {
        return state.getValue(PropertyHolder.FACING_DIR).direction();
    }
}
