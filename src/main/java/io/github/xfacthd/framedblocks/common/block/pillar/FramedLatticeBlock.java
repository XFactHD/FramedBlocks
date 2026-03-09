package io.github.xfacthd.framedblocks.common.block.pillar;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.block.IPillarLikeBlock;
import io.github.xfacthd.framedblocks.common.block.slope.FramedConnectingPyramidBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.property.PillarConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jspecify.annotations.Nullable;

import java.util.function.BiPredicate;

public class FramedLatticeBlock extends FramedBlock implements IPillarLikeBlock
{
    private final BiPredicate<Direction, BlockState> connectionTest;
    private final PillarConnection pillarConnection;

    public FramedLatticeBlock(BlockType type, Properties props)
    {
        super(type, props);
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
        this.pillarConnection = switch (type)
        {
            case FRAMED_LATTICE_BLOCK -> PillarConnection.POST;
            case FRAMED_THICK_LATTICE -> PillarConnection.PILLAR;
            default -> throw new IllegalArgumentException("Unexpected BlockType in FramedLatticeBlock: " + type);
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
    @Nullable
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
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess tickAccess,
            BlockPos pos,
            Direction side,
            BlockPos adjPos,
            BlockState adjState,
            RandomSource random
    )
    {
        if (!state.getValue(FramedProperties.STATE_LOCKED))
        {
            Direction opposite = side.getOpposite();
            state = state.setValue(
                    getPropFromAxis(side),
                    canConnectTo(adjState, side) || canConnectTo(level, pos, opposite)
            );
        }

        return super.updateShape(state, level, tickAccess, pos, side, adjPos, adjState, random);
    }

    private boolean canConnectTo(LevelReader level, BlockPos pos, Direction side)
    {
        BlockState state = level.getBlockState(pos.relative(side));
        return canConnectTo(state, side);
    }

    private boolean canConnectTo(BlockState state, Direction side)
    {
        if (state.is(this) || connectionTest.test(side, state))
        {
            return true;
        }
        if (state.getBlock() instanceof FramedConnectingPyramidBlock)
        {
            return state.getValue(BlockStateProperties.FACING) == side.getOpposite();
        }
        return false;
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        //Not rotatable by wrench
        return state;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        if (DirUtils.isNinetyDegree(rotation))
        {
            boolean xAxis = state.getValue(FramedProperties.Z_AXIS);
            boolean zAxis = state.getValue(FramedProperties.X_AXIS);

            return state.setValue(FramedProperties.X_AXIS, xAxis)
                    .setValue(FramedProperties.Z_AXIS, zAxis);
        }

        return state;
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState()
                .setValue(FramedProperties.X_AXIS, true)
                .setValue(FramedProperties.Y_AXIS, true)
                .setValue(FramedProperties.Z_AXIS, true);
    }

    @Override
    @Nullable
    public Direction getHorizontalOrientation(BlockState state)
    {
        return null;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState()
                .setValue(FramedProperties.X_AXIS, true)
                .setValue(FramedProperties.Y_AXIS, true)
                .setValue(FramedProperties.Z_AXIS, true);
    }

    @Override
    public PillarConnection getPillarConnection(BlockState state, Direction side)
    {
        return state.getValue(getPropFromAxis(side)) ? pillarConnection : PillarConnection.NONE;
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
        if (state.is(FBContent.BLOCK_FRAMED_POST.value()))
        {
            return side.getAxis() == state.getValue(BlockStateProperties.AXIS);
        }
        return DirUtils.isY(side) && state.is(BlockTags.FENCES);
    }

    private static boolean canConnectThick(Direction side, BlockState state)
    {
        if (state.is(BlockTags.WALLS))
        {
            // Work around mods incorrectly tagging non-WallBlock blocks as walls
            if (!state.hasProperty(BlockStateProperties.UP))
            {
                return false;
            }
            return side == Direction.DOWN || (side == Direction.UP && state.getValue(BlockStateProperties.UP));
        }
        if (state.is(FBContent.BLOCK_FRAMED_PILLAR.value()))
        {
            return side.getAxis() == state.getValue(BlockStateProperties.AXIS);
        }
        if (state.is(FBContent.BLOCK_FRAMED_HALF_PILLAR.value()))
        {
            return side == state.getValue(BlockStateProperties.FACING).getOpposite();
        }
        return false;
    }
}
