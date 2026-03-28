package io.github.xfacthd.framedblocks.common.block.stairs.vertical;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.ShapeLockableBlock;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.block.stairs.standard.FramedHalfStairsBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.StairsType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class FramedVerticalStairsBlock extends FramedBlock implements ShapeLockableBlock {
    public FramedVerticalStairsBlock(BlockType type, Properties props) {
        super(type, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.STAIRS_TYPE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withHalfFacing()
                .withCustom((state, modCtx) -> getStateFromContext(state, modCtx.getLevel(), modCtx.getClickedPos()))
                .tryWithWater() // Vertical double stairs don't support waterlogging
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
    ) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (side != dir.getOpposite() && side != dir.getClockWise()) {
            state = getStateFromContext(state, level, pos);
        }
        return super.updateShape(state, level, tickAccess, pos, side, adjPos, adjState, random);
    }

    private static BlockState getStateFromContext(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(FramedProperties.STATE_LOCKED)) {
            return state;
        }

        Direction dir = state.getValue(FramedProperties.FACING_HOR);

        BlockState front = level.getBlockState(pos.relative(dir));
        BlockState left = level.getBlockState(pos.relative(dir.getCounterClockWise()));

        if (isNoStair(front) && isNoStair(left)) {
            return state.setValue(PropertyHolder.STAIRS_TYPE, StairsType.VERTICAL);
        } else {
            boolean topCornerFront = false;
            boolean bottomCornerFront = false;

            if (front.getBlock() instanceof StairBlock && front.getValue(BlockStateProperties.HORIZONTAL_FACING) == dir.getCounterClockWise()) {
                topCornerFront = front.getValue(BlockStateProperties.HALF) == Half.BOTTOM;
                bottomCornerFront = front.getValue(BlockStateProperties.HALF) == Half.TOP;
            } else if (front.getBlock() instanceof FramedHalfStairsBlock && front.getValue(FramedProperties.FACING_HOR) == dir.getCounterClockWise()) {
                boolean top = front.getValue(FramedProperties.TOP);

                if (!front.getValue(PropertyHolder.RIGHT)) {
                    topCornerFront = !top;
                    bottomCornerFront = top;
                }
            }

            boolean topCornerLeft = false;
            boolean bottomCornerLeft = false;

            if (left.getBlock() instanceof StairBlock && left.getValue(BlockStateProperties.HORIZONTAL_FACING) == dir) {
                topCornerLeft = left.getValue(BlockStateProperties.HALF) == Half.BOTTOM;
                bottomCornerLeft = left.getValue(BlockStateProperties.HALF) == Half.TOP;
            } else if (left.getBlock() instanceof FramedHalfStairsBlock && left.getValue(FramedProperties.FACING_HOR) == dir) {
                boolean top = left.getValue(FramedProperties.TOP);

                if (left.getValue(PropertyHolder.RIGHT)) {
                    topCornerLeft = !top;
                    bottomCornerLeft = top;
                }
            }

            BlockState above = level.getBlockState(pos.above());
            BlockState below = level.getBlockState(pos.below());

            StairsType type = StairsType.VERTICAL;
            if ((topCornerFront || topCornerLeft) && !(above.getBlock() instanceof FramedVerticalStairsBlock)) {
                if (!topCornerLeft) {
                    type = StairsType.TOP_FWD;
                } else if (!topCornerFront) {
                    type = StairsType.TOP_CCW;
                } else {
                    type = StairsType.TOP_BOTH;
                }
            } else if ((bottomCornerFront || bottomCornerLeft) && !(below.getBlock() instanceof FramedVerticalStairsBlock)) {
                if (!bottomCornerLeft) {
                    type = StairsType.BOTTOM_FWD;
                } else if (!bottomCornerFront) {
                    type = StairsType.BOTTOM_CCW;
                } else {
                    type = StairsType.BOTTOM_BOTH;
                }
            }

            return state.setValue(PropertyHolder.STAIRS_TYPE, type);
        }
    }

    private static boolean isNoStair(BlockState state) {
        return !(state.getBlock() instanceof StairBlock) && !(state.getBlock() instanceof FramedHalfStairsBlock);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return BlockUtils.rotate(state, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return BlockUtils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public Set<Property<?>> getPropertiesToCopy() {
        return Set.of(PropertyHolder.STAIRS_TYPE);
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
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}
