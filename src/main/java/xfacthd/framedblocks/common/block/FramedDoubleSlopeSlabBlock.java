package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.blockentity.FramedDoubleSlopeSlabBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoubleSlopeSlabBlock extends AbstractFramedDoubleBlock
{
    private static final VoxelShape SHAPE_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    private static final VoxelShape SHAPE_TOP = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
            (state.getValue(PropertyHolder.TOP_HALF) && dir == Direction.UP) ||
            (!state.getValue(PropertyHolder.TOP_HALF) && dir == Direction.DOWN);

    public FramedDoubleSlopeSlabBlock()
    {
        super(BlockType.FRAMED_DOUBLE_SLOPE_SLAB);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.TOP_HALF, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.TOP_HALF, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction face = context.getClickedFace();
        Direction facing = Utils.isY(face) ? context.getHorizontalDirection() : face.getOpposite();
        BlockState state = defaultBlockState().setValue(FramedProperties.FACING_HOR, facing);
        state = withWater(state, context.getLevel(), context.getClickedPos());
        return withTop(state, PropertyHolder.TOP_HALF, context.getClickedFace(), context.getClickLocation());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return state.getValue(PropertyHolder.TOP_HALF) ? SHAPE_TOP : SHAPE_BOTTOM;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleSlopeSlabBlockEntity(pos, state);
    }
}
