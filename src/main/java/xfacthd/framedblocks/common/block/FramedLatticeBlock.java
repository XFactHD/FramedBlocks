package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class FramedLatticeBlock extends FramedBlock
{
    public static final SideSkipPredicate SKIP_PREDICATE = (world, pos, state, adjState, side) ->
    {
        if (adjState.getBlock() == FBContent.blockFramedLattice.get())
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    };

    public FramedLatticeBlock()
    {
        super(BlockType.FRAMED_LATTICE_BLOCK);
        registerDefaultState(defaultBlockState()
                .setValue(PropertyHolder.X_AXIS, false)
                .setValue(PropertyHolder.Y_AXIS, false)
                .setValue(PropertyHolder.Z_AXIS, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.X_AXIS, PropertyHolder.Y_AXIS, PropertyHolder.Z_AXIS, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockState state = defaultBlockState();
        state = state.setValue(PropertyHolder.X_AXIS, world.getBlockState(pos.east()).is(this) || world.getBlockState(pos.west()).is(this));
        state = state.setValue(PropertyHolder.Y_AXIS, world.getBlockState(pos.above()).is(this) || world.getBlockState(pos.below()).is(this));
        state = state.setValue(PropertyHolder.Z_AXIS, world.getBlockState(pos.north()).is(this) || world.getBlockState(pos.south()).is(this));
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
    {
        state = state.setValue(
                getPropFromAxis(facing),
                facingState.is(this) || world.getBlockState(pos.relative(facing.getOpposite())).is(this)
        );

        return super.updateShape(state, facing, facingState, world, pos, facingPos);
    }

    public static ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape xShape = box(0, 6, 6, 16, 10, 10);
        VoxelShape yShape = box(6, 0, 6, 10, 16, 10);
        VoxelShape zShape = box(6, 6, 0, 10, 10, 16);

        for (BlockState state : states)
        {
            VoxelShape shape = box(6, 6, 6, 10, 10, 10);

            if (state.getValue(PropertyHolder.X_AXIS)) { shape = VoxelShapes.or(shape, xShape); }
            if (state.getValue(PropertyHolder.Y_AXIS)) { shape = VoxelShapes.or(shape, yShape); }
            if (state.getValue(PropertyHolder.Z_AXIS)) { shape = VoxelShapes.or(shape, zShape); }

            builder.put(state, shape.optimize());
        }

        return builder.build();
    }

    public static BooleanProperty getPropFromAxis(Direction dir)
    {
        return switch (dir.getAxis())
        {
            case X -> PropertyHolder.X_AXIS;
            case Y -> PropertyHolder.Y_AXIS;
            case Z -> PropertyHolder.Z_AXIS;
        };
    }
}