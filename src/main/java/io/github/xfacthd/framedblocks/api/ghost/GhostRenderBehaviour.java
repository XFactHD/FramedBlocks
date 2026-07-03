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
/// Must be registered in [RegisterGhostRenderBehavioursEvent].
public interface GhostRenderBehaviour {
    Vector3fc OFFSET_ZERO = new Vector3f();

    /// If the item this behavior is registered for proxies another item, then this method should be
    /// used to return the actual item whose block representation should be rendered, an example of this
    /// being the Framed Blueprint.
    /// The returned stack will be given to all other methods in this class.
    ///
    /// @param stack The stack in the players main hand
    /// @return a stack of the proxied item or null if not applicable
    default @Nullable ItemStack getProxiedStack(ItemStack stack) {
        return null;
    }

    /// Return true if the given stack may render a block. Used as an early bail-out when
    /// the given stack won't be able to render anything. The checks should be as fast as possible.
    ///
    /// @param stack        The stack in the players main hand
    /// @param proxiedStack The proxied stack as returned from [#getProxiedStack(ItemStack)]
    /// @return true if the given stack may render a block
    default boolean mayRender(ItemStack stack, @Nullable ItemStack proxiedStack) {
        return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof IFramedBlock;
    }

    /// {@return how many separate blocks need to be rendered for the held item}
    ///
    /// @param stack        The stack in the players main hand
    /// @param proxiedStack The proxied stack as returned from [#getProxiedStack(ItemStack)]
    default int getPassCount(ItemStack stack, @Nullable ItemStack proxiedStack) {
        return 1;
    }

    /// {@return the blockstate to render or null if no fitting state can be determined for the given context}
    ///
    /// @param stack        The stack in the players main hand
    /// @param proxiedStack The proxied stack as returned from [#getProxiedStack(ItemStack)]
    /// @param hit          The [BlockHitResult] from [Minecraft#hitResult]
    /// @param ctx          The [BlockPlaceContext] to use for determining the blockstate to render
    /// @param hitState     The state of the block the player is looking at
    /// @param renderPass   The current render pass index
    default @Nullable BlockState getRenderState(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            int renderPass
    ) {
        return ((InvokerBlockItem) stack.getItem()).framedblocks$callGetPlacementState(ctx);
    }

    /// {@return the position to render the block at}
    ///
    /// @param stack        The stack in the players main hand
    /// @param proxiedStack The proxied stack as returned from [#getProxiedStack(ItemStack)]
    /// @param hit          The [BlockHitResult] from [Minecraft#hitResult]
    /// @param ctx          The [BlockPlaceContext] used for determining the blockstate to render
    /// @param hitState     The state of the block the player is looking at
    /// @param defaultPos   The position at which the block will be rendered and placed by default
    /// @param renderPass   The current render pass index
    default BlockPos getRenderPos(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            BlockPos defaultPos,
            int renderPass
    ) {
        return defaultPos;
    }

    /// Determine whether the previously calculated blockstate can actually render at the given position.
    /// This method is only called at most once, regardless of the return value of [#getPassCount(ItemStack, ItemStack)]
    /// and controls whether all or none of the blocks are rendered.
    ///
    /// @param stack        The stack in the players main hand
    /// @param proxiedStack The proxied stack as returned from [#getProxiedStack(ItemStack)]
    /// @param hit          The [BlockHitResult] from [Minecraft#hitResult]
    /// @param ctx          The [BlockPlaceContext] used for determining the blockstate to render
    /// @param hitState     The state of the block the player is looking at
    /// @param renderState  The state to render
    /// @param renderPos    The position to render the block at
    /// @return true if the `BlockState` can actually render at the given [BlockPos]
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean canRenderAt(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
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
    /// @param stack        The stack in the players main hand
    /// @param proxiedStack The proxied stack as returned from [#getProxiedStack(ItemStack)]
    /// @param renderPass   The current render pass index
    default CamoList readCamo(ItemStack stack, @Nullable ItemStack proxiedStack, int renderPass) {
        return stack.getOrDefault(FramedConstants.Objects.DC_TYPE_CAMO_LIST, CamoList.EMPTY);
    }

    /// Post-process the camos read from the stack with the given context.
    /// Separated from [#readCamo(ItemStack, ItemStack, int)] to allow the camo to be read
    /// by a proxying item while allowing the proxied item to manipulate it according to the context.
    ///
    /// @param stack        The stack in the players main hand
    /// @param proxiedStack The proxied stack as returned from [#getProxiedStack(ItemStack)]
    /// @param ctx          The [BlockPlaceContext] used for determining the blockstate to render
    /// @param renderState  The state to render
    /// @param renderPass   The current render pass index
    /// @param camo         The camo list previously read by [#readCamo(ItemStack, ItemStack, int)]
    /// @return the camo list with any necessary modifications applied to it
    default CamoList postProcessCamo(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            CamoList camo
    ) {
        return camo;
    }

    /// {@return the block overlay stored in the given stack or `null` if no overlay is present}
    ///
    /// @param stack        The stack in the players main hand
    /// @param proxiedStack The proxied stack as returned from [#getProxiedStack(ItemStack)]
    /// @param renderPass   The current render pass index
    default @Nullable Holder<BlockOverlay> readBlockOverlay(ItemStack stack, @Nullable ItemStack proxiedStack, int renderPass) {
        return stack.get(FramedConstants.Objects.DC_TYPE_BLOCK_OVERLAY);
    }

    /// Build the model data to render the placement preview with. Allows full control over the model data creation.
    ///
    /// @param stack        The stack in the players main hand
    /// @param proxiedStack The proxied stack as returned from [#getProxiedStack(ItemStack)]
    /// @param ctx          The [BlockPlaceContext] used for determining the state to render
    /// @param renderState  The state to render
    /// @param renderPass   The current render pass index
    /// @param camo         The camos applied to the block
    /// @param overlay      The overlay applied to the block
    /// @return the model data to render the model of the block with
    default ModelData buildModelData(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
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
    /// @param stack        The stack in the players main hand
    /// @param proxiedStack The proxied stack as returned from [#getProxiedStack(ItemStack)]
    /// @param ctx          The [BlockPlaceContext] used for determining the state to render
    /// @param renderState  The state to render
    /// @param renderPass   The current render pass index
    /// @param data         The model data holding the camos and block overlay
    /// @return the model data with any necessary modifications applied to it
    default ModelData appendModelData(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            ModelData data
    ) {
        return data;
    }

    /// {@return an additional offset to apply to the {@link PoseStack} before rendering}
    ///
    /// @param stack        The stack in the players main hand
    /// @param proxiedStack The proxied stack as returned from [#getProxiedStack(ItemStack)]
    /// @param ctx          The [BlockPlaceContext] used for determining the state to render
    /// @param renderState  The state to render
    /// @param renderPass   The current render pass index
    /// @param data         The model data the model of the block will be rendered with
    default Vector3fc getRenderOffset(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            ModelData data
    ) {
        return OFFSET_ZERO;
    }
}
