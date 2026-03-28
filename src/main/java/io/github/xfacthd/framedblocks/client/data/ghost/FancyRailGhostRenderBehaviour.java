package io.github.xfacthd.framedblocks.client.data.ghost;

import io.github.xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public final class FancyRailGhostRenderBehaviour implements GhostRenderBehaviour {
    @Override
    public boolean mayRender(ItemStack stack, @Nullable ItemStack proxiedStack) {
        return RailSlopeGhostRenderBehaviour.INSTANCE.mayRender(stack, proxiedStack) ||
                GhostRenderBehaviour.super.mayRender(stack, proxiedStack);
    }

    @Override
    public @Nullable BlockState getRenderState(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            int renderPass
    ) {
        BlockState state = RailSlopeGhostRenderBehaviour.INSTANCE.getRenderState(stack, proxiedStack, hit, ctx, hitState, renderPass);
        if (state != null)
        {
            return state;
        }
        return GhostRenderBehaviour.super.getRenderState(stack, proxiedStack, hit, ctx, hitState, renderPass);
    }

    @Override
    public BlockPos getRenderPos(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            BlockPos defaultPos,
            int renderPass
    ) {
        if (hitState.getBlock() == FBContent.BLOCK_FRAMED_SLOPE.value()) {
            return RailSlopeGhostRenderBehaviour.INSTANCE.getRenderPos(stack, proxiedStack, hit, ctx, hitState, defaultPos, renderPass);
        }
        return GhostRenderBehaviour.super.getRenderPos(stack, proxiedStack, hit, ctx, hitState, defaultPos, renderPass);
    }

    @Override
    public boolean canRenderAt(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            BlockState renderState,
            BlockPos renderPos
    ) {
        if (renderPos.equals(hit.getBlockPos())) {
            return RailSlopeGhostRenderBehaviour.INSTANCE.canRenderAt(stack, proxiedStack, hit, ctx, hitState, renderState, renderPos);
        }
        return GhostRenderBehaviour.super.canRenderAt(stack, proxiedStack, hit, ctx, hitState, renderState, renderPos);
    }
}
