package io.github.xfacthd.framedblocks.client.data.ghost;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintBlockPlaceContext;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import io.github.xfacthd.framedblocks.client.render.special.GhostBlockRenderer;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.item.FramedBlueprintItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public final class BlueprintGhostRenderBehaviour implements GhostRenderBehaviour<BlueprintGhostRenderBehaviour.Context> {
    @Override
    public @Nullable Context getRenderContext(ItemStack stack) {
        BlueprintData blueprintData = stack.getOrDefault(FBContent.DC_TYPE_BLUEPRINT_DATA, BlueprintData.EMPTY);
        if (!blueprintData.isEmpty()) {
            ItemStack proxied = new ItemStack(blueprintData.block());
            FramedBlueprintItem.getBehaviour(blueprintData.block()).attachDataToDummyRenderStack(proxied, blueprintData);
            return Context.create(proxied, blueprintData);
        }
        return null;
    }

    @Override
    public boolean mayRender(ItemStack stack, Context context) {
        return context.proxiedBehaviour.mayRender(context.stack, context.proxyContext);
    }

    @Override
    public int getPassCount(ItemStack stack, Context context) {
        return context.proxiedBehaviour.getPassCount(context.stack, context.proxyContext);
    }

    @Override
    public BlockPlaceContext buildPlaceContext(Player player, ItemStack stack, Context context, BlockHitResult hit) {
        BlockPlaceContext placeContext = GhostRenderBehaviour.super.buildPlaceContext(player, stack, context, hit);
        return new BlueprintBlockPlaceContext(placeContext, context.stack, context.blueprintData);
    }

    @Override
    public @Nullable BlockState getRenderState(
            ItemStack stack,
            Context context,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            int renderPass
    ) {
        BlockState state = context.proxiedBehaviour.getRenderState(context.stack, context.proxyContext, hit, ctx, hitState, renderPass);
        BlockItemStateProperties stateProps = context.blueprintData.blockState();
        if (state != null && !stateProps.isEmpty()) {
            state = stateProps.apply(state);
        }
        return state;
    }

    @Override
    public BlockPos getRenderPos(
            ItemStack stack,
            Context context,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            BlockPos defaultPos,
            int renderPass
    ) {
        return context.proxiedBehaviour.getRenderPos(context.stack, context.proxyContext, hit, ctx, hitState, defaultPos, renderPass);
    }

    @Override
    public boolean canRenderAt(
            ItemStack stack,
            Context context,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            BlockState renderState,
            BlockPos renderPos
    ) {
        return context.proxiedBehaviour.canRenderAt(context.stack, context.proxyContext, hit, ctx, hitState, renderState, renderPos);
    }

    @Override
    public CamoList readCamo(ItemStack stack, Context context, int renderPass) {
        return FramedBlueprintItem.getCamoContainers(context.blueprintData);
    }

    @Override
    public CamoList postProcessCamo(
            ItemStack stack,
            Context context,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            CamoList camo
    ) {
        return context.proxiedBehaviour.postProcessCamo(context.stack, context.proxyContext, ctx, renderState, renderPass, camo);
    }

    @Override
    public @Nullable Holder<BlockOverlay> readBlockOverlay(ItemStack stack, Context context, int renderPass) {
        return context.blueprintData.overlay().orElse(null);
    }

    @Override
    public ModelData buildModelData(
            ItemStack stack,
            Context context,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            CamoList camo,
            @Nullable Holder<BlockOverlay> overlay
    ) {
        return context.proxiedBehaviour.buildModelData(context.stack, context.proxyContext, ctx, renderState, renderPass, camo, overlay);
    }

    @Override
    public ModelData appendModelData(
            ItemStack stack,
            Context context,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            ModelData data
    ) {
        return context.proxiedBehaviour.appendModelData(context.stack, context.proxyContext, ctx, renderState, renderPass, data);
    }

    @Override
    public Vector3fc getRenderOffset(
            ItemStack stack,
            Context context,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            ModelData data
    ) {
        return context.proxiedBehaviour.getRenderOffset(context.stack, context.proxyContext, ctx, renderState, renderPass, data);
    }

    public record Context(ItemStack stack, BlueprintData blueprintData, GhostRenderBehaviour<Object> proxiedBehaviour, Object proxyContext) {
        private static @Nullable Context create(ItemStack stack, BlueprintData blueprintData) {
            GhostRenderBehaviour<Object> behaviour = GhostBlockRenderer.getBehaviour(stack.getItem());
            Object context = behaviour.getRenderContext(stack);
            return context != null ? new Context(stack, blueprintData, behaviour, context) : null;
        }
    }
}
