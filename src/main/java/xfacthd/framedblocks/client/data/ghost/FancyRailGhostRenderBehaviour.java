package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.common.FBContent;

public final class FancyRailGhostRenderBehaviour implements GhostRenderBehaviour
{
    private final RailSlopeGhostRenderBehaviour railSlopeBehaviour = new RailSlopeGhostRenderBehaviour();

    @Override
    public boolean mayRender(ItemStack stack, @Nullable ItemStack proxiedStack)
    {
        return railSlopeBehaviour.mayRender(stack, proxiedStack) || GhostRenderBehaviour.super.mayRender(stack, proxiedStack);
    }

    @Override
    public @Nullable BlockState getRenderState(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            int renderPass
    )
    {
        BlockState state = railSlopeBehaviour.getRenderState(stack, proxiedStack, hit, ctx, hitState, renderPass);
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
    )
    {
        if (hitState.getBlock() == FBContent.BLOCK_FRAMED_SLOPE.get())
        {
            return railSlopeBehaviour.getRenderPos(stack, proxiedStack, hit, ctx, hitState, defaultPos, renderPass);
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
    )
    {
        if (renderPos.equals(hit.getBlockPos()))
        {
            return railSlopeBehaviour.canRenderAt(stack, proxiedStack, hit, ctx, hitState, renderState, renderPos);
        }
        return GhostRenderBehaviour.super.canRenderAt(stack, proxiedStack, hit, ctx, hitState, renderState, renderPos);
    }
}
