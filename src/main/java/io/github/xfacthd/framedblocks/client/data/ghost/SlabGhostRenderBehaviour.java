package io.github.xfacthd.framedblocks.client.data.ghost;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.ghost.SimpleGhostRenderBehaviour;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public final class SlabGhostRenderBehaviour implements SimpleGhostRenderBehaviour {
    @Override
    public @Nullable BlockState getRenderState(
            ItemStack stack,
            Unit context,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            int renderPass
    ) {
        BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (state.getBlock() == FBContent.BLOCK_FRAMED_SLAB.value()) {
            boolean top = state.getValue(FramedProperties.TOP);
            return state.setValue(FramedProperties.TOP, !top);
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
        BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (state.getBlock() == FBContent.BLOCK_FRAMED_SLAB.value()) {
            return ctx.getClickedPos();
        }
        return defaultPos;
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
            return true;
        }
        return SimpleGhostRenderBehaviour.super.canRenderAt(stack, context, hit, ctx, hitState, renderState, renderPos);
    }
}
