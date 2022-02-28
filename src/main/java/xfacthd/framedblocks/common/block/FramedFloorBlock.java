package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.CtmPredicate;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

@SuppressWarnings("deprecation")
public class FramedFloorBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) -> dir == Direction.DOWN;

    public static final SideSkipPredicate SKIP_PREDICATE = (world, pos, state, adjState, side) ->
    {
        if (side == Direction.DOWN) { return SideSkipPredicate.CTM.test(world, pos, state, adjState, side); }

        if (side.getAxis() != Direction.Axis.Y && adjState.getBlock() == FBContent.blockFramedFloor.get())
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    };

    public FramedFloorBlock() { super(BlockType.FRAMED_FLOOR_BOARD); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.WATERLOGGED, PropertyHolder.SOLID);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos)
    {
        return !world.isEmptyBlock(pos.below());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction side, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos)
    {
        return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, side, neighborState, world, pos, neighborPos);
    }
}