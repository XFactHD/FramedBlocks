package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedLadderBlock extends FramedBlock
{
    private static final VoxelShape SHAPE_NORTH = makeCuboidShape( 0, 0,  0, 16, 16,  2);
    private static final VoxelShape SHAPE_EAST =  makeCuboidShape(14, 0,  0, 16, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = makeCuboidShape( 0, 0, 14, 16, 16, 16);
    private static final VoxelShape SHAPE_WEST =  makeCuboidShape( 0, 0,  0,  2, 16, 16);
    private static final VoxelShape[] SHAPES = new VoxelShape[] { SHAPE_SOUTH, SHAPE_WEST, SHAPE_NORTH, SHAPE_EAST };

    public FramedLadderBlock() { super("framed_ladder", BlockType.FRAMED_LADDER); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState().with(PropertyHolder.FACING_HOR, context.getPlacementHorizontalFacing());
        return withWater(state, context.getWorld(), context.getPos());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPES[state.get(PropertyHolder.FACING_HOR).getHorizontalIndex()];
    }

    @Override
    public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) { return true; }
}