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
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.*;

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
                .setValue(PropertyHolder.SOLID, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.AXIS, BlockStateProperties.WATERLOGGED, PropertyHolder.SOLID, PropertyHolder.GLOWING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = defaultBlockState();

        Direction face = context.getClickedFace();
        state = state.setValue(BlockStateProperties.FACING, face);

        Direction.Axis axis;
        if (face.getAxis() == Direction.Axis.Y)
        {
            axis = context.getHorizontalDirection().getAxis();
        }
        else
        {
            Vector3d subHit = Utils.fraction(context.getClickLocation());

            double xz = (face.getAxis() == Direction.Axis.X ? subHit.z() : subHit.x()) - .5;
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

        VoxelShape shapeBottom = VoxelShapes.or(
                box( 0, 0, 0,   16, .5, 16),
                box(.5, 0, 0, 15.5,  4, 16),
                box( 4, 0, 0,   12,  8, 16)
        ).optimize();

        VoxelShape shapeTop = VoxelShapes.or(
                box( 0, 15.5, 0,   16, 16, 16),
                box(.5,   12, 0, 15.5, 16, 16),
                box( 4,    8, 0,   12, 16, 16)
        ).optimize();

        VoxelShape shapeXZ = VoxelShapes.or(
                box(0,  0, 15.5, 16,   16, 16),
                box(0, .5,   12, 16, 15.5, 16),
                box(0,  4,    8, 16,   12, 16)
        ).optimize();

        VoxelShape shapeY = VoxelShapes.or(
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
                builder.put(state, VoxelShapes.block());
                continue;
            }

            if (facing.getAxis() == Direction.Axis.Y)
            {
                builder.put(
                        state,
                        Utils.rotateShape(
                                Direction.NORTH,
                                Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE),
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
