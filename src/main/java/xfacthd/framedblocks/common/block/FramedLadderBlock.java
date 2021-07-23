package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedLadderBlock extends FramedBlock
{
    private static final VoxelShape SHAPE_NORTH = box( 0, 0,  0, 16, 16,  2);
    private static final VoxelShape SHAPE_EAST =  box(14, 0,  0, 16, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = box( 0, 0, 14, 16, 16, 16);
    private static final VoxelShape SHAPE_WEST =  box( 0, 0,  0,  2, 16, 16);
    private static final VoxelShape[] SHAPES = new VoxelShape[] { SHAPE_SOUTH, SHAPE_WEST, SHAPE_NORTH, SHAPE_EAST };

    public FramedLadderBlock() { super(BlockType.FRAMED_LADDER); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState().setValue(PropertyHolder.FACING_HOR, context.getHorizontalDirection());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
    {
        return SHAPES[state.getValue(PropertyHolder.FACING_HOR).get2DDataValue()];
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) { return true; }
}