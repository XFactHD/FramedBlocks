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
    public static final CtmPredicate CTM_PREDICATE = (state, side) -> side == state.get(BlockStateProperties.FACING).getOpposite();

    public static final SideSkipPredicate SKIP_PREDICATE = (level, pos, state, adjState, side) ->
    {
        Direction facing = state.get(BlockStateProperties.FACING);
        Direction.Axis axis = state.get(BlockStateProperties.AXIS);
        if (side.getAxis() != axis)
        {
            return false;
        }

        if (adjState.matchesBlock(FBContent.blockFramedPrism.get()))
        {
            Direction adjFacing = adjState.get(BlockStateProperties.FACING);
            Direction.Axis adjAxis = adjState.get(BlockStateProperties.AXIS);

            return adjFacing == facing && adjAxis == axis && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (adjState.matchesBlock(FBContent.blockFramedSlopedPrism.get()))
        {
            Direction adjFacing = adjState.get(BlockStateProperties.FACING);
            Direction adjOrientation = adjState.get(PropertyHolder.ORIENTATION);

            return adjFacing == facing && adjOrientation == side.getOpposite() && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    };

    public FramedPrismBlock()
    {
        super(BlockType.FRAMED_PRISM);
        setDefaultState(getDefaultState()
                .with(BlockStateProperties.WATERLOGGED, false)
                .with(PropertyHolder.SOLID, false)
        );
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.AXIS, BlockStateProperties.WATERLOGGED, PropertyHolder.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState();

        Direction face = context.getFace();
        state = state.with(BlockStateProperties.FACING, face);

        Direction.Axis axis;
        if (face.getAxis() == Direction.Axis.Y)
        {
            axis = context.getPlacementHorizontalFacing().getAxis();
        }
        else
        {
            Vector3d subHit = Utils.fraction(context.getHitVec());

            double xz = (face.getAxis() == Direction.Axis.X ? subHit.getZ() : subHit.getX()) - .5;
            double y = subHit.getY() - .5;

            if (Math.max(Math.abs(xz), Math.abs(y)) == Math.abs(xz))
            {
                axis = face.rotateY().getAxis();
            }
            else
            {
                axis = Direction.Axis.Y;
            }
        }
        state = state.with(BlockStateProperties.AXIS, axis);

        return withWater(state, context.getWorld(), context.getPos());
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = VoxelShapes.or(
                makeCuboidShape(0, 0, 0, 16, 4, 16),
                makeCuboidShape(4, 0, 0, 12, 8, 16)
        ).simplify();

        VoxelShape shapeTop = VoxelShapes.or(
                makeCuboidShape(0, 12, 0, 16, 16, 16),
                makeCuboidShape(4,  8, 0, 12, 16, 16)
        ).simplify();

        VoxelShape shapeXZ = VoxelShapes.or(
                makeCuboidShape(0, 0, 12, 16, 16, 16),
                makeCuboidShape(0, 4,  8, 16, 12, 16)
        ).simplify();

        VoxelShape shapeY = VoxelShapes.or(
                makeCuboidShape(0, 0, 12, 16, 16, 16),
                makeCuboidShape(4, 0,  8, 12, 16, 16)
        ).simplify();

        for (BlockState state : states)
        {
            Direction facing = state.get(BlockStateProperties.FACING);
            Direction.Axis axis = state.get(BlockStateProperties.AXIS);

            if (axis == facing.getAxis()) //Invalid combination
            {
                builder.put(state, VoxelShapes.fullCube());
                continue;
            }

            if (facing.getAxis() == Direction.Axis.Y)
            {
                builder.put(
                        state,
                        Utils.rotateShape(
                                Direction.NORTH,
                                Direction.getFacingFromAxisDirection(axis, Direction.AxisDirection.POSITIVE),
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
