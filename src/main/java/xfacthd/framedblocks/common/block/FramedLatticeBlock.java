package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IWorld;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class FramedLatticeBlock extends FramedBlock
{
    public static final SideSkipPredicate SKIP_PREDICATE = (world, pos, state, adjState, side) ->
    {
        if (adjState.getBlock() == FBContent.blockFramedLattice)
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    };

    public FramedLatticeBlock()
    {
        super("framed_lattice_block", BlockType.FRAMED_LATTICE);
        setDefaultState(getDefaultState()
                .with(PropertyHolder.X_AXIS, false)
                .with(PropertyHolder.Y_AXIS, false)
                .with(PropertyHolder.Z_AXIS, false)
        );
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.X_AXIS, PropertyHolder.Y_AXIS, PropertyHolder.Z_AXIS, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
    {
        state = state.with(
                getPropFromAxis(facing),
                facingState.matchesBlock(this) || world.getBlockState(pos.offset(facing.getOpposite())).matchesBlock(this)
        );

        return super.updatePostPlacement(state, facing, facingState, world, pos, facingPos);
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape xShape = makeCuboidShape(0, 6, 6, 16, 10, 10);
        VoxelShape yShape = makeCuboidShape(6, 0, 6, 10, 16, 10);
        VoxelShape zShape = makeCuboidShape(6, 6, 0, 10, 10, 16);

        for (BlockState state : states)
        {
            VoxelShape shape = makeCuboidShape(6, 6, 6, 10, 10, 10);

            if (state.get(PropertyHolder.X_AXIS)) { shape = VoxelShapes.or(shape, xShape); }
            if (state.get(PropertyHolder.Y_AXIS)) { shape = VoxelShapes.or(shape, yShape); }
            if (state.get(PropertyHolder.Z_AXIS)) { shape = VoxelShapes.or(shape, zShape); }

            builder.put(state, shape.simplify());
        }

        return builder.build();
    }

    public static BooleanProperty getPropFromAxis(Direction dir)
    {
        switch (dir.getAxis())
        {
            case X:
            {
                return PropertyHolder.X_AXIS;
            }
            case Y:
            {
                return PropertyHolder.Y_AXIS;
            }
            case Z:
            {
                return PropertyHolder.Z_AXIS;
            }
            default: throw new IllegalArgumentException("Facing with invalid axis: " + dir);
        }
    }
}