package io.github.xfacthd.framedblocks.common.block.slopeedge;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import org.jspecify.annotations.Nullable;

public class FramedStackedSlopeEdgeBlock extends FramedDoubleBlock implements SlopeToggleBlock {
    public FramedStackedSlopeEdgeBlock(Properties props) {
        super(BlockType.FRAMED_STACKED_SLOPE_EDGE, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.SLOPE_TYPE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return ExtPlacementStateBuilder.of(this, ctx)
                .withHorizontalFacingAndSlopeType()
                .withCustom((state, modCtx) -> {
                    Direction dir = state.getValue(FramedProperties.FACING_HOR);
                    SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
                    if (dir != modCtx.getHorizontalDirection() && type == SlopeType.HORIZONTAL) {
                        state = state.setValue(FramedProperties.ALT_SLOPE, true);
                    }
                    return state;
                })
                .withWater()
                .build();
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return switch (mode) {
            case PRIMARY -> super.rotate(state, direction, mode);
            case SECONDARY -> direction.cycle(state, PropertyHolder.SLOPE_TYPE);
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL) {
            return BlockUtils.mirrorCornerBlock(state, mirror);
        } else {
            return BlockUtils.mirrorFaceBlock(state, mirror);
        }
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (type == SlopeType.TOP) {
            return DoubleBlockTopInteractionMode.FIRST;
        }
        return DoubleBlockTopInteractionMode.BOTH;
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        BlockState edgeState = FBContent.BLOCK_FRAMED_SLOPE_EDGE.value()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, dir)
                .setValue(PropertyHolder.SLOPE_TYPE, type)
                .setValue(PropertyHolder.ALT_TYPE, true)
                .setValue(FramedProperties.ALT_SLOPE, altSlope);

        if (type == SlopeType.HORIZONTAL) {
            return new DoubleBlockParts(
                    FBContent.BLOCK_FRAMED_VERTICAL_STAIRS.value()
                            .defaultBlockState()
                            .setValue(FramedProperties.FACING_HOR, dir),
                    edgeState
            );
        } else {
            Half half = type == SlopeType.TOP ? Half.TOP : Half.BOTTOM;
            return new DoubleBlockParts(
                    FBContent.BLOCK_FRAMED_STAIRS.value()
                            .defaultBlockState()
                            .setValue(BlockStateProperties.HORIZONTAL_FACING, dir)
                            .setValue(BlockStateProperties.HALF, half),
                    edgeState
            );
        }
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (side == dir) {
            return SolidityCheck.FIRST;
        }

        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        Direction dirTwo = switch (type) {
            case BOTTOM -> Direction.DOWN;
            case HORIZONTAL -> dir.getCounterClockWise();
            case TOP -> Direction.UP;
        };
        if (side == dirTwo) {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (side == dir) {
            return CamoGetter.FIRST;
        }

        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        Direction dirTwo = switch (type) {
            case BOTTOM -> Direction.DOWN;
            case HORIZONTAL -> dir.getCounterClockWise();
            case TOP -> Direction.UP;
        };
        if (side == dirTwo) {
            return CamoGetter.FIRST;
        } else if (side == dirTwo.getOpposite()) {
            if (edge == dir) {
                return CamoGetter.FIRST;
            }
            return CamoGetter.NONE;
        } else if (side == dir.getOpposite()) {
            if (edge == dirTwo) {
                return CamoGetter.FIRST;
            }
            return CamoGetter.NONE;
        } else { // Triangle faces
            if (edge == dir || edge == dirTwo) {
                return CamoGetter.FIRST;
            }
            return CamoGetter.NONE;
        }
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state) {
        return state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return getItemModelSource();
    }

    @Override
    public SlopeOrientation getSlopeOrientation(BlockState state) {
        return state.getValue(PropertyHolder.SLOPE_TYPE).getOrientation();
    }
}
