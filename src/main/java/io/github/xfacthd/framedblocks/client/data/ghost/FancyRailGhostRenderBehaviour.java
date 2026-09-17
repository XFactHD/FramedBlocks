package io.github.xfacthd.framedblocks.client.data.ghost;

import io.github.xfacthd.framedblocks.api.ghost.SimpleGhostRenderBehaviour;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public final class FancyRailGhostRenderBehaviour implements SimpleGhostRenderBehaviour {
    @Override
    public boolean mayRender(ItemStack stack, Unit context) {
        return RailSlopeGhostRenderBehaviour.INSTANCE.mayRender(stack, context) ||
                SimpleGhostRenderBehaviour.super.mayRender(stack, context);
    }

    @Override
    public @Nullable BlockState getRenderState(
            ItemStack stack,
            Unit context,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            int renderPass
    ) {
        BlockState state = RailSlopeGhostRenderBehaviour.INSTANCE.getRenderState(stack, context, hit, ctx, hitState, renderPass);
        if (state != null) {
            return state;
        }
        return SimpleGhostRenderBehaviour.super.getRenderState(stack, context, hit, ctx, hitState, renderPass);
    }

    @Override
    public BlockPos getRenderPos(
            ItemStack stack,
            Unit context,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            BlockPos defaultPos,
            int renderPass
    ) {
        if (hitState.getBlock() == FBContent.BLOCK_FRAMED_SLOPE.value()) {
            return RailSlopeGhostRenderBehaviour.INSTANCE.getRenderPos(stack, context, hit, ctx, hitState, defaultPos, renderPass);
        }
        return SimpleGhostRenderBehaviour.super.getRenderPos(stack, context, hit, ctx, hitState, defaultPos, renderPass);
    }

    @Override
    public boolean canRenderAt(
            ItemStack stack,
            Unit context,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            BlockState renderState,
            BlockPos renderPos
    ) {
        if (renderPos.equals(hit.getBlockPos())) {
            return RailSlopeGhostRenderBehaviour.INSTANCE.canRenderAt(stack, context, hit, ctx, hitState, renderState, renderPos);
        }
        return SimpleGhostRenderBehaviour.super.canRenderAt(stack, context, hit, ctx, hitState, renderState, renderPos);
    }
}
