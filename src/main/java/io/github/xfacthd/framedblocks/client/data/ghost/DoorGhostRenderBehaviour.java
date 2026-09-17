package io.github.xfacthd.framedblocks.client.data.ghost;

import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.ghost.SimpleGhostRenderBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public final class DoorGhostRenderBehaviour implements SimpleGhostRenderBehaviour {
    @Override
    public int getPassCount(ItemStack stack, Unit context) {
        return 2;
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
        BlockState state = SimpleGhostRenderBehaviour.super.getRenderState(stack, context, hit, ctx, hitState, renderPass);
        if (state != null && renderPass == 1) {
            state = state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
        }
        return state;
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
        return defaultPos.relative(Direction.UP, renderPass);
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
        return SimpleGhostRenderBehaviour.super.canRenderAt(stack, context, hit, ctx, hitState, renderState, renderPos) &&
               SimpleGhostRenderBehaviour.super.canRenderAt(stack, context, hit, ctx, hitState, renderState, renderPos.above());
    }

    @Override
    public CamoList postProcessCamo(
            ItemStack stack,
            Unit context,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            CamoList camo
    ) {
        return camo.subList(renderPass, renderPass + 1);
    }
}
