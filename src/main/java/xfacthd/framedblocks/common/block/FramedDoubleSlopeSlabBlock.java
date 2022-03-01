package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedDoubleSlopeSlabTileEntity;
import xfacthd.framedblocks.common.util.CtmPredicate;
import xfacthd.framedblocks.common.util.Utils;

public class FramedDoubleSlopeSlabBlock extends AbstractFramedDoubleBlock
{
    private static final VoxelShape SHAPE_BOTTOM = box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    private static final VoxelShape SHAPE_TOP = box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
            (state.getValue(PropertyHolder.TOP_HALF) && dir == Direction.UP) ||
            (!state.getValue(PropertyHolder.TOP_HALF) && dir == Direction.DOWN);

    public FramedDoubleSlopeSlabBlock()
    {
        super(BlockType.FRAMED_DOUBLE_SLOPE_SLAB);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.TOP_HALF, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP_HALF, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Direction face = context.getClickedFace();
        Direction facing = Utils.isY(face) ? context.getHorizontalDirection() : face.getOpposite();
        BlockState state = defaultBlockState().setValue(PropertyHolder.FACING_HOR, facing);
        state = withWater(state, context.getLevel(), context.getClickedPos());
        return withTop(state, PropertyHolder.TOP_HALF, context.getClickedFace(), context.getClickLocation());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
    {
        return state.getValue(PropertyHolder.TOP_HALF) ? SHAPE_TOP : SHAPE_BOTTOM;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new FramedDoubleSlopeSlabTileEntity();
    }
}
