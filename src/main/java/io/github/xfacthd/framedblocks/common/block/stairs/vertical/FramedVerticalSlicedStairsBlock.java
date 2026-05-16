package io.github.xfacthd.framedblocks.common.block.stairs.vertical;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyLabels;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.IFramedDoubleBlockInternal;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jspecify.annotations.Nullable;

public class FramedVerticalSlicedStairsBlock extends FramedVerticalStairsBlock implements IFramedDoubleBlockInternal {
    public FramedVerticalSlicedStairsBlock(BlockType type, Properties props) {
        super(type, props);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.RIGHT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.RIGHT);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        if (state == null) {
            return null;
        }

        Direction face = ctx.getClickedFace();
        Direction fracDir = DirUtils.isY(face) ? ctx.getHorizontalDirection().getClockWise() : face.getCounterClockWise();
        boolean right = MathUtils.fractionInDir(ctx.getClickLocation(), fracDir) > .5;
        return state.setValue(PropertyHolder.RIGHT, right);
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state) {
        return DoubleBlockTopInteractionMode.BOTH;
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean right = state.getValue(PropertyHolder.RIGHT);
        if (right) {
            return switch (state.getValue(PropertyHolder.STAIRS_TYPE)) {
                case VERTICAL -> new DoubleBlockParts(
                        FBContent.BLOCK_FRAMED_PANEL.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir),
                        FBContent.BLOCK_FRAMED_CORNER_PILLAR.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise())
                );
                case TOP_FWD -> new DoubleBlockParts(
                        FBContent.BLOCK_FRAMED_HALF_STAIRS.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise())
                                .setValue(PropertyHolder.RIGHT, true),
                        FBContent.BLOCK_FRAMED_CORNER_PILLAR.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise())
                );
                case TOP_CCW -> new DoubleBlockParts(
                        FBContent.BLOCK_FRAMED_PANEL.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir),
                        FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise())
                );
                case TOP_BOTH -> new DoubleBlockParts(
                        FBContent.BLOCK_FRAMED_HALF_STAIRS.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise())
                                .setValue(PropertyHolder.RIGHT, true),
                        FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise())
                );
                case BOTTOM_FWD -> new DoubleBlockParts(
                        FBContent.BLOCK_FRAMED_HALF_STAIRS.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise())
                                .setValue(PropertyHolder.RIGHT, true)
                                .setValue(FramedProperties.TOP, true),
                        FBContent.BLOCK_FRAMED_CORNER_PILLAR.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise())
                );
                case BOTTOM_CCW -> new DoubleBlockParts(
                        FBContent.BLOCK_FRAMED_PANEL.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir),
                        FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise())
                                .setValue(FramedProperties.TOP, true)
                );
                case BOTTOM_BOTH -> new DoubleBlockParts(
                        FBContent.BLOCK_FRAMED_HALF_STAIRS.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise())
                                .setValue(PropertyHolder.RIGHT, true)
                                .setValue(FramedProperties.TOP, true),
                        FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise())
                                .setValue(FramedProperties.TOP, true)
                );
            };
        } else {
            return switch (state.getValue(PropertyHolder.STAIRS_TYPE)) {
                case VERTICAL -> new DoubleBlockParts(
                        FBContent.BLOCK_FRAMED_PANEL.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise()),
                        FBContent.BLOCK_FRAMED_CORNER_PILLAR.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getClockWise())
                );
                case TOP_FWD -> new DoubleBlockParts(
                        FBContent.BLOCK_FRAMED_PANEL.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise()),
                        FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getClockWise())
                );
                case TOP_CCW -> new DoubleBlockParts(
                        FBContent.BLOCK_FRAMED_HALF_STAIRS.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir),
                        FBContent.BLOCK_FRAMED_CORNER_PILLAR.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getClockWise())
                );
                case TOP_BOTH -> new DoubleBlockParts(
                        FBContent.BLOCK_FRAMED_HALF_STAIRS.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir),
                        FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getClockWise())
                );
                case BOTTOM_FWD -> new DoubleBlockParts(
                        FBContent.BLOCK_FRAMED_PANEL.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getCounterClockWise()),
                        FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getClockWise())
                                .setValue(FramedProperties.TOP, true)
                );
                case BOTTOM_CCW -> new DoubleBlockParts(
                        FBContent.BLOCK_FRAMED_HALF_STAIRS.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir)
                                .setValue(FramedProperties.TOP, true),
                        FBContent.BLOCK_FRAMED_CORNER_PILLAR.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getClockWise())
                );
                case BOTTOM_BOTH -> new DoubleBlockParts(
                        FBContent.BLOCK_FRAMED_HALF_STAIRS.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir)
                                .setValue(FramedProperties.TOP, true),
                        FBContent.BLOCK_FRAMED_SLAB_CORNER.value()
                                .defaultBlockState()
                                .setValue(FramedProperties.FACING_HOR, dir.getClockWise())
                                .setValue(FramedProperties.TOP, true)
                );
            };
        }
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
        boolean right = state.getValue(PropertyHolder.RIGHT);

        if (side == facing && !type.isForward()) {
            return right ? SolidityCheck.FIRST : SolidityCheck.BOTH;
        }
        if (side == facing.getCounterClockWise() && !type.isCounterClockwise()) {
            return right ? SolidityCheck.BOTH : SolidityCheck.FIRST;
        }
        return SolidityCheck.NONE;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge) {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
        boolean right = state.getValue(PropertyHolder.RIGHT);

        if (side == facing) {
            return switch (type) {
                case VERTICAL, TOP_CCW, BOTTOM_CCW -> {
                    if (!right) {
                        if (edge == facing.getCounterClockWise()) {
                            yield CamoGetter.FIRST;
                        }
                        if (edge == facing.getClockWise()) {
                            yield CamoGetter.SECOND;
                        }
                        yield CamoGetter.NONE;
                    }
                    yield CamoGetter.FIRST;
                }
                case TOP_FWD, TOP_BOTH -> {
                    if (edge == facing.getCounterClockWise() || (right && edge == Direction.DOWN)) {
                        yield CamoGetter.FIRST;
                    }
                    yield CamoGetter.NONE;
                }
                case BOTTOM_FWD, BOTTOM_BOTH -> {
                    if (edge == facing.getCounterClockWise() || (right && edge == Direction.UP)) {
                        yield CamoGetter.FIRST;
                    }
                    yield CamoGetter.NONE;
                }
            };
        }
        if (side == facing.getCounterClockWise()) {
            return switch (type) {
                case VERTICAL, TOP_FWD, BOTTOM_FWD -> {
                    if (right) {
                        if (edge == facing) {
                            yield CamoGetter.FIRST;
                        }
                        if (edge == facing.getOpposite()) {
                            yield CamoGetter.SECOND;
                        }
                        yield CamoGetter.NONE;
                    }
                    yield CamoGetter.FIRST;
                }
                case TOP_CCW, TOP_BOTH -> {
                    if (edge == facing || (!right && edge == Direction.DOWN)) {
                        yield CamoGetter.FIRST;
                    }
                    yield CamoGetter.NONE;
                }
                case BOTTOM_CCW, BOTTOM_BOTH -> {
                    if (edge == facing || (!right && edge == Direction.UP)) {
                        yield CamoGetter.FIRST;
                    }
                    yield CamoGetter.NONE;
                }
            };
        }
        if (side == Direction.UP) {
            if (!right && (!type.isTop() || !type.isCounterClockwise()) && edge == facing.getCounterClockWise()) {
                return CamoGetter.FIRST;
            }
            if (right && (!type.isTop() || !type.isForward()) && edge == facing) {
                return CamoGetter.FIRST;
            }
            return CamoGetter.NONE;
        }
        if (side == Direction.DOWN) {
            if (!right && (!type.isBottom() || !type.isCounterClockwise()) && edge == facing.getCounterClockWise()) {
                return CamoGetter.FIRST;
            }
            if (right && (!type.isBottom() || !type.isForward()) && edge == facing) {
                return CamoGetter.FIRST;
            }
            return CamoGetter.NONE;
        }
        if (side == facing.getOpposite()) {
            if (edge == facing.getCounterClockWise() && !type.isCounterClockwise()) {
                return right ? CamoGetter.SECOND : CamoGetter.FIRST;
            }
            return CamoGetter.NONE;
        }
        if (side == facing.getClockWise()) {
            if (edge == facing && !type.isForward()) {
                return right ? CamoGetter.SECOND : CamoGetter.FIRST;
            }
            return CamoGetter.NONE;
        }
        return CamoGetter.NONE;
    }

    @Override
    public FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return IFramedDoubleBlockInternal.super.newBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.RIGHT, true);
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.builder(this)
                .property(FramedProperties.FACING_HOR, PropertyLabels.FACING)
                .property(PropertyHolder.STAIRS_TYPE, PropertyLabels.SHAPE)
                .property(PropertyHolder.RIGHT, builder ->
                        builder.printer(PropertyHolder.Labels.RIGHT)
                )
                .build();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}
