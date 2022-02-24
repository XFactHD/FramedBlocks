package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedElevatedSlopeSlabBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
            dir == state.getValue(FramedProperties.FACING_HOR) ||
            (state.getValue(FramedProperties.TOP) && dir == Direction.UP) ||
            (!state.getValue(FramedProperties.TOP) && dir == Direction.DOWN);

    public FramedElevatedSlopeSlabBlock()
    {
        super(BlockType.FRAMED_ELEVATED_SLOPE_SLAB);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, BlockStateProperties.WATERLOGGED, FramedProperties.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction face = context.getClickedFace();
        Direction facing = Utils.isY(face) ? context.getHorizontalDirection() : face.getOpposite();

        BlockState state = defaultBlockState().setValue(FramedProperties.FACING_HOR, facing);
        state = withTop(state, context.getClickedFace(), context.getClickLocation());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        VoxelShape shapeBottom = Shapes.or(
                FramedSlopeSlabBlock.SHAPE_BOTTOM.move(0, .5, 0),
                box(0, 0, 0, 16, 8, 16)
        );

        VoxelShape shapeTop = Shapes.or(
                FramedSlopeSlabBlock.SHAPE_TOP,
                box(0, 8, 0, 16, 16, 16)
        );

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(
                    state,
                    Utils.rotateShape(Direction.NORTH, facing, top ? shapeTop : shapeBottom)
            );
        }

        return builder.build();
    }
}
