package xfacthd.framedblocks.common.block.stairs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.CtmPredicate;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.block.slope.FramedVerticalHalfSlopeBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedSlopedStairsBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (side == dir || side == dir.getCounterClockWise())
        {
            return true;
        }

        boolean top = state.getValue(FramedProperties.TOP);
        return top ? side == Direction.UP : side == Direction.DOWN;
    };

    public FramedSlopedStairsBlock()
    {
        super(BlockType.FRAMED_SLOPED_STAIRS);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(PropertyHolder.RIGHT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, FramedProperties.SOLID, PropertyHolder.RIGHT, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction side = context.getClickedFace();
        Vec3 hitVec = context.getClickLocation();

        Direction dir;
        if (Utils.isY(side))
        {
            dir = context.getHorizontalDirection();
        }
        else
        {
            dir = side.getOpposite();
            if (Utils.fractionInDir(hitVec, side.getCounterClockWise()) > .5D)
            {
                dir = dir.getClockWise();
            }
        }
        BlockState state = defaultBlockState().setValue(FramedProperties.FACING_HOR, dir);

        state = withTop(state, side, hitVec);
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (Utils.isY(face))
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else
        {
            return state.cycle(FramedProperties.TOP);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        return rotate(state, Direction.UP, rotation);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = Shapes.or(
                FramedVerticalHalfSlopeBlock.SHAPE_TOP,
                box(0, 0, 0, 16, 8, 16)
        ).optimize();

        VoxelShape shapeTop = Shapes.or(
                FramedVerticalHalfSlopeBlock.SHAPE_BOTTOM,
                box(0, 8, 0, 16, 16, 16)
        ).optimize();

        for (BlockState state : states)
        {
            boolean top = state.getValue(FramedProperties.TOP);
            VoxelShape shape = top ? shapeTop : shapeBottom;

            builder.put(
                    state,
                    Utils.rotateShape(
                            Direction.NORTH,
                            state.getValue(FramedProperties.FACING_HOR),
                            shape
                    )
            );
        }

        return ShapeProvider.of(builder.build());
    }
}
