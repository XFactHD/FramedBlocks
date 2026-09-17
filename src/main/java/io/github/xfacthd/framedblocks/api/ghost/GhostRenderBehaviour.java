package io.github.xfacthd.framedblocks.api.ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.mixin.InvokerBlockItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/// Provide custom behaviors for ghost block rendering when the player is looking at another block while holding an
/// applicable stack in the main hand.
///
/// Implementations which don't need additional context data should implement [SimpleGhostRenderBehaviour] instead.
///
/// Must be registered in [RegisterGhostRenderBehavioursEvent].
public interface GhostRenderBehaviour<C> {
    Vector3fc OFFSET_ZERO = new Vector3f();

    /// Return additional context data needed in subsequent stages, such as a proxied item and its
    /// behavior and context data.
    ///
    /// @param stack The stack in the player's main hand
    /// @return additional render context
    @Nullable C getRenderContext(ItemStack stack);

    /// Return true if the given stack may render a block. Used as an early bail-out when
    /// the given stack won't be able to render anything. The checks should be as fast as possible.
    ///
    /// @param stack   The stack in the players main hand
    /// @param context The render context as returned from [#getRenderContext(ItemStack)]
    /// @return true if the given stack may render a block
    default boolean mayRender(ItemStack stack, C context) {
        return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof IFramedBlock;
    }

    /// {@return how many separate blocks need to be rendered for the held item}
    ///
    /// @param stack   The stack in the players main hand
    /// @param context The render context as returned from [#getRenderContext(ItemStack)]
    default int getPassCount(ItemStack stack, C context) {
        return 1;
    }

    /// {@return the place context to use for determining the rendered ghost state}
    ///
    /// @param player  The player holding the item for which the ghost is being rendered
    /// @param stack   The stack in the players main hand
    /// @param context The render context as returned from [#getRenderContext(ItemStack)]
    /// @param hit     The [BlockHitResult] from [Minecraft#hitResult]
    default BlockPlaceContext buildPlaceContext(Player player, ItemStack stack, C context, BlockHitResult hit) {
        return new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, hit);
    }

    /// {@return the blockstate to render or null if no fitting state can be determined for the given context}
    ///
    /// @param stack      The stack in the players main hand
    /// @param context    The render context as returned from [#getRenderContext(ItemStack)]
    /// @param hit        The [BlockHitResult] from [Minecraft#hitResult]
    /// @param ctx        The [BlockPlaceContext] to use for determining the blockstate to render
    /// @param hitState   The state of the block the player is looking at
    /// @param renderPass The current render pass index
    default @Nullable BlockState getRenderState(
            ItemStack stack,
            C context,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            int renderPass
    ) {
        return ((InvokerBlockItem) stack.getItem()).framedblocks$callGetPlacementState(ctx);
    }

    /// {@return the position to render the block at}
    ///
    /// @param stack      The stack in the players main hand
    /// @param context    The render context as returned from [#getRenderContext(ItemStack)]
    /// @param hit        The [BlockHitResult] from [Minecraft#hitResult]
    /// @param ctx        The [BlockPlaceContext] used for determining the blockstate to render
    /// @param hitState   The state of the block the player is looking at
    /// @param defaultPos The position at which the block will be rendered and placed by default
    /// @param renderPass The current render pass index
    default BlockPos getRenderPos(
            ItemStack stack,
            C context,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            BlockPos defaultPos,
            int renderPass
    ) {
        return defaultPos;
    }

    /// Determine whether the previously calculated blockstate can actually render at the given position.
    /// This method is only called at most once, regardless of the return value of [#getPassCount(ItemStack, C)]
    /// and controls whether all or none of the blocks are rendered.
    ///
    /// @param stack       The stack in the players main hand
    /// @param context     The render context as returned from [#getRenderContext(ItemStack)]
    /// @param hit         The [BlockHitResult] from [Minecraft#hitResult]
    /// @param ctx         The [BlockPlaceContext] used for determining the blockstate to render
    /// @param hitState    The state of the block the player is looking at
    /// @param renderState The state to render
    /// @param renderPos   The position to render the block at
    /// @return true if the `BlockState` can actually render at the given [BlockPos]
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean canRenderAt(
            ItemStack stack,
            C context,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            BlockState renderState,
            BlockPos renderPos
    ) {
        Level level = ctx.getLevel();
        Player player = Objects.requireNonNull(ctx.getPlayer());

        return level.isUnobstructed(renderState, renderPos, CollisionContext.of(player)) && level.getBlockState(renderPos).canBeReplaced(ctx);
    }

    /// {@return the camo(s) stored in the given stack or the empty list if no camos are present}
    ///
    /// @param stack      The stack in the players main hand
    /// @param context    The render context as returned from [#getRenderContext(ItemStack)]
    /// @param renderPass The current render pass index
    default CamoList readCamo(ItemStack stack, C context, int renderPass) {
        return stack.getOrDefault(FramedConstants.Objects.DC_TYPE_CAMO_LIST, CamoList.EMPTY);
    }

    /// Post-process the camos read from the stack with the given context.
    /// Separated from [#readCamo(ItemStack, C, int)] to allow the camo to be read
    /// by a proxying item while allowing the proxied item to manipulate it according to the context.
    ///
    /// @param stack       The stack in the players main hand
    /// @param context     The render context as returned from [#getRenderContext(ItemStack)]
    /// @param ctx         The [BlockPlaceContext] used for determining the blockstate to render
    /// @param renderState The state to render
    /// @param renderPass  The current render pass index
    /// @param camo        The camo list previously read by [#readCamo(ItemStack, C, int)]
    /// @return the camo list with any necessary modifications applied to it
    default CamoList postProcessCamo(
            ItemStack stack,
            C context,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            CamoList camo
    ) {
        return camo;
    }

    /// {@return the block overlay stored in the given stack or `null` if no overlay is present}
    ///
    /// @param stack      The stack in the players main hand
    /// @param context    The render context as returned from [#getRenderContext(ItemStack)]
    /// @param renderPass The current render pass index
    default @Nullable Holder<BlockOverlay> readBlockOverlay(ItemStack stack, C context, int renderPass) {
        return stack.get(FramedConstants.Objects.DC_TYPE_BLOCK_OVERLAY);
    }

    /// Build the model data to render the placement preview with. Allows full control over the model data creation.
    ///
    /// @param stack       The stack in the players main hand
    /// @param context     The render context as returned from [#getRenderContext(ItemStack)]
    /// @param ctx         The [BlockPlaceContext] used for determining the state to render
    /// @param renderState The state to render
    /// @param renderPass  The current render pass index
    /// @param camo        The camos applied to the block
    /// @param overlay     The overlay applied to the block
    /// @return the model data to render the model of the block with
    default ModelData buildModelData(
            ItemStack stack,
            C context,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            CamoList camo,
            @Nullable Holder<BlockOverlay> overlay
    ) {
        return ModelData.of(AbstractFramedBlockData.PROPERTY, new FramedBlockData(renderState, camo.getCamo(0), false, overlay));
    }

    /// Append any additional data apart from the camos and overlay to the given model data.
    ///
    /// @param stack       The stack in the players main hand
    /// @param context     The render context as returned from [#getRenderContext(ItemStack)]
    /// @param ctx         The [BlockPlaceContext] used for determining the state to render
    /// @param renderState The state to render
    /// @param renderPass  The current render pass index
    /// @param data        The model data holding the camos and block overlay
    /// @return the model data with any necessary modifications applied to it
    default ModelData appendModelData(
            ItemStack stack,
            C context,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            ModelData data
    ) {
        return data;
    }

    /// {@return an additional offset to apply to the {@link PoseStack} before rendering}
    ///
    /// @param stack       The stack in the players main hand
    /// @param context     The render context as returned from [#getRenderContext(ItemStack)]
    /// @param ctx         The [BlockPlaceContext] used for determining the state to render
    /// @param renderState The state to render
    /// @param renderPass  The current render pass index
    /// @param data        The model data the model of the block will be rendered with
    default Vector3fc getRenderOffset(
            ItemStack stack,
            C context,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            ModelData data
    ) {
        return OFFSET_ZERO;
    }
}
