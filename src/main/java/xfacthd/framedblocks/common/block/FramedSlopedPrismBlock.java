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

public class FramedSlopedPrismBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) -> side == state.get(BlockStateProperties.FACING).getOpposite();

    public static final SideSkipPredicate SKIP_PREDICATE = (level, pos, state, adjState, side) ->
    {
        Direction facing = state.get(BlockStateProperties.FACING);
        Direction orientation = state.get(PropertyHolder.ORIENTATION);
        if (side != orientation)
        {
            return false;
        }

        if (adjState.matchesBlock(FBContent.blockFramedSlopedPrism.get()))
        {
            Direction adjFacing = adjState.get(BlockStateProperties.FACING);
            Direction adjOrientation = adjState.get(PropertyHolder.ORIENTATION);

            return adjFacing == facing && adjOrientation == orientation.getOpposite() && SideSkipPredicate.compareState(level, pos, side);
        }
        else if (adjState.matchesBlock(FBContent.blockFramedPrism.get()))
        {
            Direction.Axis adjAxis = adjState.get(BlockStateProperties.AXIS);
            Direction adjFacing = adjState.get(BlockStateProperties.FACING);

            return adjFacing == facing && adjAxis == orientation.getAxis() && SideSkipPredicate.compareState(level, pos, side);
        }

        return false;
    };

    public FramedSlopedPrismBlock()
    {
        super(BlockType.FRAMED_SLOPED_PRISM);
        setDefaultState(getDefaultState()
                .with(BlockStateProperties.WATERLOGGED, false)
                .with(PropertyHolder.SOLID, false)
        );
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.FACING, PropertyHolder.ORIENTATION, BlockStateProperties.WATERLOGGED, PropertyHolder.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = getDefaultState();

        Direction face = context.getFace();
        state = state.with(BlockStateProperties.FACING, face);

        Direction orientation;
        if (face.getAxis() == Direction.Axis.Y)
        {
            orientation = context.getPlacementHorizontalFacing();
        }
        else
        {
            Vector3d subHit = Utils.fraction(context.getHitVec());

            double xz = (face.getAxis() == Direction.Axis.X ? subHit.getZ() : subHit.getX()) - .5;
            double y = subHit.getY() - .5;

            if (Math.max(Math.abs(xz), Math.abs(y)) == Math.abs(xz))
            {
                if (face.getAxis() == Direction.Axis.X)
                {
                    orientation = xz < 0 ? Direction.SOUTH : Direction.NORTH;
                }
                else
                {
                    orientation = xz < 0 ? Direction.EAST : Direction.WEST;
                }
            }
            else
            {
                orientation = y < 0 ? Direction.UP : Direction.DOWN;
            }
        }
        state = state.with(PropertyHolder.ORIENTATION, orientation);

        return withWater(state, context.getWorld(), context.getPos());
    }



    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBottom = VoxelShapes.or(
                makeCuboidShape(0, 0, 0, 16, 4, 16),
                makeCuboidShape(4, 0, 0, 12, 8, 12)
        ).simplify();

        VoxelShape shapeTop = VoxelShapes.or(
                makeCuboidShape(0, 12, 0, 16, 16, 16),
                makeCuboidShape(4,  8, 0, 12, 16, 12)
        ).simplify();

        VoxelShape shapeRight = VoxelShapes.or(
                makeCuboidShape(0, 0, 12, 16, 16, 16),
                makeCuboidShape(0, 4,  8, 12, 12, 16)
        ).simplify();

        VoxelShape shapeLeft = VoxelShapes.or(
                makeCuboidShape(0, 0, 12, 16, 16, 16),
                makeCuboidShape(4, 4,  8, 16, 12, 16)
        ).simplify();

        VoxelShape shapeUp = VoxelShapes.or(
                makeCuboidShape(0, 0, 12, 16, 16, 16),
                makeCuboidShape(4, 4,  8, 12, 16, 16)
        ).simplify();

        VoxelShape shapeDown = VoxelShapes.or(
                makeCuboidShape(0, 0, 12, 16, 16, 16),
                makeCuboidShape(4, 0,  8, 12, 12, 16)
        ).simplify();

        for (BlockState state : states)
        {
            Direction facing = state.get(BlockStateProperties.FACING);
            Direction orientation = state.get(PropertyHolder.ORIENTATION);

            if (orientation == facing || orientation == facing.getOpposite())
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
                                orientation,
                                facing == Direction.UP ? shapeBottom : shapeTop
                        )
                );
            }
            else
            {
                VoxelShape shape;
                if (orientation == Direction.UP) { shape = shapeUp; }
                else if (orientation == Direction.DOWN) { shape = shapeDown; }
                else if (orientation == facing.rotateY()) { shape = shapeLeft; }
                else if (orientation == facing.rotateYCCW()) { shape = shapeRight; }
                else { throw new IllegalArgumentException("Invalid orientation for direction!"); }

                builder.put(
                        state,
                        Utils.rotateShape(Direction.NORTH, facing, shape)
                );
            }
        }

        return builder.build();
    }
}
