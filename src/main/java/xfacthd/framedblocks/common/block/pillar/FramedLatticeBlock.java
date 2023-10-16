package xfacthd.framedblocks.common.block.pillar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.function.BiPredicate;

public class FramedLatticeBlock extends FramedBlock
{
    private final BiPredicate<Direction, BlockState> connectionTest;

    public FramedLatticeBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.X_AXIS, false)
                .setValue(FramedProperties.Y_AXIS, false)
                .setValue(FramedProperties.Z_AXIS, false)
                .setValue(FramedProperties.STATE_LOCKED, false)
        );
        this.connectionTest = switch (type)
        {
            case FRAMED_LATTICE_BLOCK -> FramedLatticeBlock::canConnectThin;
            case FRAMED_THICK_LATTICE -> FramedLatticeBlock::canConnectThick;
            default -> throw new IllegalArgumentException("Unexpected lattice type: " + type);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.X_AXIS, FramedProperties.Y_AXIS, FramedProperties.Z_AXIS,
                BlockStateProperties.WATERLOGGED, FramedProperties.STATE_LOCKED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) ->
                {
                    Level level = modCtx.getLevel();
                    BlockPos pos = modCtx.getClickedPos();

                    state = state.setValue(
                            FramedProperties.X_AXIS,
                            canConnectTo(level, pos, Direction.EAST) || canConnectTo(level, pos, Direction.WEST)
                    );
                    state = state.setValue(
                            FramedProperties.Y_AXIS,
                            canConnectTo(level, pos, Direction.UP) || canConnectTo(level, pos, Direction.DOWN)
                    );
                    state = state.setValue(
                            FramedProperties.Z_AXIS,
                            canConnectTo(level, pos, Direction.NORTH) || canConnectTo(level, pos, Direction.SOUTH)
                    );

                    return state;
                })
                .withWater()
                .build();
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction facing,
            BlockState facingState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos facingPos
    )
    {
        if (!state.getValue(FramedProperties.STATE_LOCKED))
        {
            Direction opposite = facing.getOpposite();
            state = state.setValue(
                    getPropFromAxis(facing),
                    canConnectTo(facingState, facing) || canConnectTo(level, pos, opposite)
            );
        }

        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    private boolean canConnectTo(LevelAccessor level, BlockPos pos, Direction side)
    {
        BlockState state = level.getBlockState(pos.relative(side));
        return canConnectTo(state, side);
    }

    private boolean canConnectTo(BlockState state, Direction side)
    {
        return state.is(this) || connectionTest.test(side, state);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        //Not rotatable by wrench
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        if (rot != Rotation.NONE && rot != Rotation.CLOCKWISE_180)
        {
            boolean xAxis = state.getValue(FramedProperties.Z_AXIS);
            boolean zAxis = state.getValue(FramedProperties.X_AXIS);

            return state.setValue(FramedProperties.X_AXIS, xAxis)
                    .setValue(FramedProperties.Z_AXIS, zAxis);
        }

        return state;
    }



    public static ShapeProvider generateThinShapes(ImmutableList<BlockState> states)
    {
        return generateShapes(
                states,
                box(6, 6, 6, 10, 10, 10),
                box(0, 6, 6, 16, 10, 10),
                box(6, 0, 6, 10, 16, 10),
                box(6, 6, 0, 10, 10, 16)
        );
    }

    public static ShapeProvider generateThickShapes(ImmutableList<BlockState> states)
    {
        return generateShapes(
                states,
                box(4, 4, 4, 12, 12, 12),
                box(0, 4, 4, 16, 12, 12),
                box(4, 0, 4, 12, 16, 12),
                box(4, 4, 0, 12, 12, 16)
        );
    }

    private static ShapeProvider generateShapes(
            ImmutableList<BlockState> states, VoxelShape centerShape, VoxelShape xShape, VoxelShape yShape, VoxelShape zShape
    )
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        int maskX = 0b001;
        int maskY = 0b010;
        int maskZ = 0b100;
        VoxelShape[] shapes = new VoxelShape[8];
        for (int i = 0; i < 8; i++)
        {
            VoxelShape shape = centerShape;
            if ((i & maskX) != 0)
            {
                shape = ShapeUtils.orUnoptimized(shape, xShape);
            }
            if ((i & maskY) != 0)
            {
                shape = ShapeUtils.orUnoptimized(shape, yShape);
            }
            if ((i & maskZ) != 0)
            {
                shape = ShapeUtils.orUnoptimized(shape, zShape);
            }
            shapes[i] = shape.optimize();
        }

        for (BlockState state : states)
        {
            int x = state.getValue(FramedProperties.X_AXIS) ? maskX : 0;
            int y = state.getValue(FramedProperties.Y_AXIS) ? maskY : 0;
            int z = state.getValue(FramedProperties.Z_AXIS) ? maskZ : 0;
            builder.put(state, shapes[x | y | z]);
        }

        return ShapeProvider.of(builder.build());
    }

    public static BooleanProperty getPropFromAxis(Direction dir)
    {
        return switch (dir.getAxis())
        {
            case X -> FramedProperties.X_AXIS;
            case Y -> FramedProperties.Y_AXIS;
            case Z -> FramedProperties.Z_AXIS;
        };
    }

    private static boolean canConnectThin(Direction side, BlockState state)
    {
        if (state.is(FBContent.BLOCK_FRAMED_POST.get()))
        {
            return side.getAxis() == state.getValue(BlockStateProperties.AXIS);
        }
        return Utils.isY(side) && state.is(BlockTags.FENCES);
    }

    private static boolean canConnectThick(Direction side, BlockState state)
    {
        if (state.is(BlockTags.WALLS))
        {
            return side == Direction.DOWN || (side == Direction.UP && state.getValue(BlockStateProperties.UP));
        }
        if (state.is(FBContent.BLOCK_FRAMED_PILLAR.get()))
        {
            return side.getAxis() == state.getValue(BlockStateProperties.AXIS);
        }
        if (state.is(FBContent.BLOCK_FRAMED_HALF_PILLAR.get()))
        {
            return side == state.getValue(BlockStateProperties.FACING).getOpposite();
        }
        return false;
    }
}