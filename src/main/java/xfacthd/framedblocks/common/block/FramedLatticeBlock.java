package xfacthd.framedblocks.common.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.X_AXIS, PropertyHolder.Y_AXIS, PropertyHolder.Z_AXIS, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockState state = defaultBlockState();
        state = state.setValue(PropertyHolder.X_AXIS, world.getBlockState(pos.east()).is(this) || world.getBlockState(pos.west()).is(this));
        state = state.setValue(PropertyHolder.Y_AXIS, world.getBlockState(pos.above()).is(this) || world.getBlockState(pos.below()).is(this));
        state = state.setValue(PropertyHolder.Z_AXIS, world.getBlockState(pos.north()).is(this) || world.getBlockState(pos.south()).is(this));
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos)
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

            if (state.getValue(PropertyHolder.X_AXIS)) { shape = Shapes.or(shape, xShape); }
            if (state.getValue(PropertyHolder.Y_AXIS)) { shape = Shapes.or(shape, yShape); }
            if (state.getValue(PropertyHolder.Z_AXIS)) { shape = Shapes.or(shape, zShape); }

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