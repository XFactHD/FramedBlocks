package io.github.xfacthd.framedblocks.common.block.slope;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.SlopeToggleBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.ExtPlacementStateBuilder;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slope.FramedDoubleSlopeBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedDoubleSlopeBlock extends FramedDoubleBlock implements SlopeToggleBlock {
    public FramedDoubleSlopeBlock(Properties props) {
        super(BlockType.FRAMED_DOUBLE_SLOPE, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.SLOPE_TYPE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return ExtPlacementStateBuilder.of(this, ctx).withHorizontalFacingAndSlopeType().build();
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
    public TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        return mode.getDefaultNotifyBlockEntity();
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        boolean altSlope = state.getValue(FramedProperties.ALT_SLOPE);

        BlockState defState = FBContent.BLOCK_FRAMED_SLOPE.value().defaultBlockState();
        return new DoubleBlockParts(
                defState.setValue(PropertyHolder.SLOPE_TYPE, type)
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.ALT_SLOPE, altSlope),
                defState.setValue(PropertyHolder.SLOPE_TYPE, type == SlopeType.HORIZONTAL ? type : type.getOpposite())
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.ALT_SLOPE, altSlope)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        return switch (state.getValue(PropertyHolder.SLOPE_TYPE)) {
            case BOTTOM -> DoubleBlockTopInteractionMode.SECOND;
            case TOP -> DoubleBlockTopInteractionMode.FIRST;
            case HORIZONTAL -> DoubleBlockTopInteractionMode.BOTH;
        };
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        return switch (type) {
            case HORIZONTAL -> {
                if (matchesHor(side, facing) || (DirUtils.isY(side) && matchesHor(edge, facing))) {
                    yield CamoGetter.FIRST;
                }
                Direction oppFacing = facing.getOpposite();
                if (matchesHor(side, oppFacing) || (DirUtils.isY(side) && matchesHor(edge, oppFacing))) {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
            case TOP -> {
                if (side.getAxis() == facing.getClockWise().getAxis()) {
                    if (edge == facing || edge == Direction.UP) {
                        yield CamoGetter.FIRST;
                    }
                    if (edge == facing.getOpposite() || edge == Direction.DOWN) {
                        yield CamoGetter.SECOND;
                    }
                    yield CamoGetter.NONE;
                }
                if (side == facing || side == Direction.UP) {
                    yield CamoGetter.FIRST;
                }
                if (side == facing.getOpposite() || side == Direction.DOWN) {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
            case BOTTOM -> {
                if (side.getAxis() == facing.getClockWise().getAxis()) {
                    if (edge == facing || edge == Direction.DOWN) {
                        yield CamoGetter.FIRST;
                    }
                    if (edge == facing.getOpposite() || edge == Direction.UP) {
                        yield CamoGetter.SECOND;
                    }
                    yield CamoGetter.NONE;
                }
                if (side == facing || side == Direction.DOWN) {
                    yield CamoGetter.FIRST;
                }
                if (side == facing.getOpposite() || side == Direction.UP) {
                    yield CamoGetter.SECOND;
                }
                yield CamoGetter.NONE;
            }
        };
    }

    private static boolean matchesHor(@Nullable Direction side, Direction facing) {
        return side == facing || side == facing.getCounterClockWise();
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        return switch (type) {
            case HORIZONTAL -> {
                if (side == facing || side == facing.getCounterClockWise()) {
                    yield SolidityCheck.FIRST;
                } else if (side == facing.getOpposite() || side == facing.getClockWise()) {
                    yield SolidityCheck.SECOND;
                }
                yield SolidityCheck.BOTH;
            }
            case TOP -> {
                if (side == facing || side == Direction.UP) {
                    yield SolidityCheck.FIRST;
                } else if (side == facing.getOpposite() || side == Direction.DOWN) {
                    yield SolidityCheck.SECOND;
                }
                yield SolidityCheck.BOTH;
            }
            case BOTTOM -> {
                if (side == facing || side == Direction.UP) {
                    yield SolidityCheck.SECOND;
                } else if (side == facing.getOpposite() || side == Direction.DOWN) {
                    yield SolidityCheck.FIRST;
                }
                yield SolidityCheck.BOTH;
            }
        };
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedDoubleSlopeBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.WEST)
                .setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    @Override
    public SlopeOrientation getSlopeOrientation(BlockState state) {
        return state.getValue(PropertyHolder.SLOPE_TYPE).getOrientation();
    }
}
