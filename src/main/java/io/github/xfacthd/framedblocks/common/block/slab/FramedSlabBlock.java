package io.github.xfacthd.framedblocks.common.block.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.item.block.FramedSpecialBlockItem;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jspecify.annotations.Nullable;

public class FramedSlabBlock extends FramedBlock {
    public FramedSlabBlock(Properties props) {
        super(BlockType.FRAMED_SLAB, props);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.TOP);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withTop()
                .withWater()
                .build();
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext ctx) {
        if (ctx.getPlayer() != null && !ctx.getPlayer().isShiftKeyDown() && ctx.getItemInHand().is(asItem())) {
            if (!ctx.replacingClickedOnBlock()) {
                return true;
            }

            boolean top = state.getValue(FramedProperties.TOP);
            Direction side = ctx.getClickedFace();
            if ((!top && side == Direction.UP) || (top && side == Direction.DOWN)) {
                return true;
            }
            return MathUtils.fractionInDir(ctx.getClickLocation(), top ? Direction.DOWN : Direction.UP) > .5D;
        }
        return false;
    }

    @Override
    public IFramedBlockItem createBlockItem(Item.Properties props) {
        return new FramedSpecialBlockItem(this, true, props) {
            @Override
            protected BlockState getReplacementState(BlockPlaceContext ctx, BlockState originalState) {
                return FBContent.BLOCK_FRAMED_DOUBLE_SLAB.value().defaultBlockState();
            }

            @Override
            protected boolean shouldWriteToCamoTwo(BlockPlaceContext ctx, BlockState originalState) {
                return originalState.getValue(FramedProperties.TOP);
            }
        };
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return type == PathComputationType.WATER && state.getFluidState().is(FluidTags.WATER);
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return state.cycle(FramedProperties.TOP);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState();
    }
}
