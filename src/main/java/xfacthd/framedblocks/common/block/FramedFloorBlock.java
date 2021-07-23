package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos)
    {
        return !world.isEmptyBlock(pos.below());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction side, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos)
    {
        return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, side, neighborState, world, pos, neighborPos);
    }
}