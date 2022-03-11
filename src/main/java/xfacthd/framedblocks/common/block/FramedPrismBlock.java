package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedPrismBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) -> side == state.getValue(BlockStateProperties.FACING).getOpposite();

    public static final SideSkipPredicate SKIP_PREDICATE = (level, pos, state, adjState, side) ->
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        if (side.getAxis() != axis)
        {
            return false;
        }

        if (adjState.is(FBContent.blockFramedPrism.get()))
        {
            Direction adjFacing = adjState.getValue(BlockStateProperties.FACING);
            Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);

            return adjFacing == facing && adjAxis == axis && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (adjState.is(FBContent.blockFramedSlopedPrism.get()))
        {
            Direction adjFacing = adjState.getValue(BlockStateProperties.FACING);
            Direction adjOrientation = adjState.getValue(PropertyHolder.ORIENTATION);

            return adjFacing == facing && adjOrientation == side.getOpposite() && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    };

    public FramedPrismBlock()
    {
        super(BlockType.FRAMED_PRISM);
        registerDefaultState(defaultBlockState()
                .setValue(BlockStateProperties.WATERLOGGED, false)
                .setValue(FramedProperties.SOLID, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.AXIS, BlockStateProperties.WATERLOGGED, FramedProperties.SOLID, FramedProperties.GLOWING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState();

        Direction face = context.getClickedFace();
        state = state.setValue(BlockStateProperties.FACING, face);

        Direction.Axis axis;
        if (Utils.isY(face))
        {
            axis = context.getHorizontalDirection().getAxis();
        }
        else
        {
            Vec3 subHit = Utils.fraction(context.getClickLocation());

            double xz = (Utils.isX(face) ? subHit.z() : subHit.x()) - .5;
            double y = subHit.y() - .5;

            if (Math.max(Math.abs(xz), Math.abs(y)) == Math.abs(xz))
            {
                axis = face.getClockWise().getAxis();
            }
            else
            {
                axis = Direction.Axis.Y;
            }
        }
        state = state.setValue(BlockStateProperties.AXIS, axis);

        return withWater(state, context.getLevel(), context.getClickedPos());
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = Shapes.or(
                box( 0, 0, 0,   16, .5, 16),
                box(.5, 0, 0, 15.5,  4, 16),
                box( 4, 0, 0,   12,  8, 16)
        ).optimize();

        VoxelShape shapeTop = Shapes.or(
                box( 0, 15.5, 0,   16, 16, 16),
                box(.5,   12, 0, 15.5, 16, 16),
                box( 4,    8, 0,   12, 16, 16)
        ).optimize();

        VoxelShape shapeXZ = Shapes.or(
                box(0,  0, 15.5, 16,   16, 16),
                box(0, .5,   12, 16, 15.5, 16),
                box(0,  4,    8, 16,   12, 16)
        ).optimize();

        VoxelShape shapeY = Shapes.or(
                box( 0, 0, 15.5,   16, 16, 16),
                box(.5, 0,   12, 15.5, 16, 16),
                box( 4, 0,    8,   12, 16, 16)
        ).optimize();

        for (BlockState state : states)
        {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);

            if (axis == facing.getAxis()) //Invalid combination
            {
                builder.put(state, Shapes.block());
                continue;
            }

            if (Utils.isY(facing))
            {
                builder.put(
                        state,
                        Utils.rotateShape(
                                Direction.NORTH,
                                Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE),
                                facing == Direction.UP ? shapeBottom : shapeTop
                        )
                );
            }
            else
            {
                builder.put(
                        state,
                        Utils.rotateShape(Direction.NORTH, facing, axis == Direction.Axis.Y ? shapeY : shapeXZ)
                );
            }
        }

        return builder.build();
    }
}
