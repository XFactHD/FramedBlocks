package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        return getBlockPair(
                state.getValue(BlockStateProperties.FACING),
                state.getValue(PropertyHolder.ORIENTATION)
        );
    }

    public static Tuple<BlockState, BlockState> getBlockPair(Direction facing, Direction orientation)
    {
        return new Tuple<>(
                FBContent.blockFramedInnerSlopedPrism.get()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.FACING, facing)
                        .setValue(PropertyHolder.ORIENTATION, orientation),
                FBContent.blockFramedSlopedPrism.get()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.FACING, facing.getOpposite())
                        .setValue(PropertyHolder.ORIENTATION, orientation)
        );
    }
}
