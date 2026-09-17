package io.github.xfacthd.framedblocks.client.data.ghost;

import io.github.xfacthd.framedblocks.api.ghost.SimpleGhostRenderBehaviour;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public final class BoardGhostRenderBehaviour implements SimpleGhostRenderBehaviour {
    @Override
    public @Nullable BlockState getRenderState(
            ItemStack stack,
            Unit context,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            int renderPass
    ) {
        BlockState renderState = SimpleGhostRenderBehaviour.super.getRenderState(stack, context, hit, ctx, hitState, renderPass);
        BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (renderState != null && state.getBlock() == FBContent.BLOCK_FRAMED_BOARD.value()) {
            int faces = renderState.getValue(PropertyHolder.FACES);
            faces &= ~state.getValue(PropertyHolder.FACES);
            return faces == 0 ? null : renderState.setValue(PropertyHolder.FACES, faces);
        }
        return renderState;
    }
}
