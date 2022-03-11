package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.CtmPredicate;
import xfacthd.framedblocks.common.util.Utils;

public class FramedElevatedSlopeSlabBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
            dir == state.getValue(PropertyHolder.FACING_HOR) ||
            (state.getValue(PropertyHolder.TOP) && dir == Direction.UP) ||
            (!state.getValue(PropertyHolder.TOP) && dir == Direction.DOWN);

    public FramedElevatedSlopeSlabBlock()
    {
        super(BlockType.FRAMED_ELEVATED_SLOPE_SLAB);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.TOP, BlockStateProperties.WATERLOGGED, PropertyHolder.SOLID, PropertyHolder.GLOWING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Direction face = context.getClickedFace();
        Direction facing = Utils.isY(face) ? context.getHorizontalDirection() : face.getOpposite();

        BlockState state = defaultBlockState().setValue(PropertyHolder.FACING_HOR, facing);
        state = withTop(state, context.getClickedFace(), context.getClickLocation());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBottom = VoxelShapes.or(
                FramedSlopeSlabBlock.SHAPE_BOTTOM.move(0, .5, 0),
                box(0, 0, 0, 16, 8, 16)
        );

        VoxelShape shapeTop = VoxelShapes.or(
                FramedSlopeSlabBlock.SHAPE_TOP,
                box(0, 8, 0, 16, 16, 16)
        );

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction facing = state.getValue(PropertyHolder.FACING_HOR);
            boolean top = state.getValue(PropertyHolder.TOP);
            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, top ? shapeTop : shapeBottom)
            );
        }

        return builder.build();
    }
}
