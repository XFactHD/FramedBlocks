package xfacthd.framedblocks.api.ghost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.util.CamoList;
import xfacthd.framedblocks.api.util.Utils;

import java.util.Objects;

/**
 * Provide custom behaviours for ghost block rendering when the player is looking at another block while holding an
 * {@link ItemStack} in the main hand.
 * <p>
 * Must be registered via {@link FramedBlocksClientAPI#registerGhostRenderBehaviour(GhostRenderBehaviour, Item...)} or
 * {@link FramedBlocksClientAPI#registerGhostRenderBehaviour(GhostRenderBehaviour, Block...)} in {@link FMLClientSetupEvent}
 * </p>
 */
public interface GhostRenderBehaviour
{
    /**
     * If the {@link Item} this behaviour is registered for proxies another {@code Item}, then this method should be
     * used to return the actual {@code Item} whose block representation should be rendered, an example of this
     * being the Framed Blueprint.
     * The returned stack will be given to all other methods in this class
     *
     * @param stack The {@link ItemStack} in the players main hand
     * @return An {@link ItemStack} of the proxied item or null if not applicable
     */
    @Nullable
    default ItemStack getProxiedStack(ItemStack stack)
    {
        return null;
    }

    /**
     * Return true if the given {@link ItemStack} may render a block. Used as an early bail-out when
     * the given {@code ItemStack} won't be able to render anything. The checks should be as fast as possible.
     *
     * @param stack The {@link ItemStack} in the players main hand
     * @param proxiedStack The proxied {@code ItemStack} as returned from {@link GhostRenderBehaviour#getProxiedStack(ItemStack)}
     * @return True if the given {@code ItemStack} may render a block
     */
    default boolean mayRender(ItemStack stack, @Nullable ItemStack proxiedStack)
    {
        return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof IFramedBlock;
    }

    /**
     * Return true of the rendered block consist of two separate blocks (i.e. doors).
     *
     * @param stack The {@link ItemStack} in the players main hand
     * @param proxiedStack The proxied {@code ItemStack} as returned from {@link GhostRenderBehaviour#getProxiedStack(ItemStack)}
     * @return The amount of separate blocks to be rendered for the held item
     */
    default int getPassCount(ItemStack stack, @Nullable ItemStack proxiedStack)
    {
        return 1;
    }

    /**
     * Return the {@link BlockState} to render or null if no fitting {@code BlockState} can be determined for the given context
     *
     * @param stack The {@link ItemStack} in the players main hand
     * @param proxiedStack The proxied {@code ItemStack} as returned from {@link GhostRenderBehaviour#getProxiedStack(ItemStack)}
     * @param hit The {@link BlockHitResult} from {@link Minecraft#hitResult}
     * @param ctx The {@link BlockPlaceContext} to use for determining the resulting {@code BlockState} via
     *            {@link Block#getStateForPlacement(BlockPlaceContext)}
     * @param hitState The {@code BlockState} hit by the given {@code BlockHitResult}
     * @param renderPass The current render pass index
     * @return The {@code BlockState} to render or null when none could be determined
     */
    @Nullable
    default BlockState getRenderState(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            int renderPass
    )
    {
        Block block = ((BlockItem) stack.getItem()).getBlock();
        return block.getStateForPlacement(ctx);
    }

    /**
     * Return the {@link BlockPos} to render the block at.
     *
     * @param stack The {@link ItemStack} in the players main hand
     * @param proxiedStack The proxied {@code ItemStack} as returned from {@link GhostRenderBehaviour#getProxiedStack(ItemStack)}
     * @param hit The {@link BlockHitResult} from {@link Minecraft#hitResult}
     * @param ctx The {@link BlockPlaceContext} to use for determining the resulting
     * @param hitState The {@link BlockState} hit by the given {@code BlockHitResult}
     * @param defaultPos The {@code BlockPos} at which the block will be rendered and placed by default
     * @param renderPass The current render pass index
     * @return The {@code BlockPos} at which the block should be rendered
     */
    default BlockPos getRenderPos(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            BlockPos defaultPos,
            int renderPass
    )
    {
        return defaultPos;
    }

    /**
     * Determine whether the previously calculated {@link BlockState} can actually render at the given {@link BlockPos}.
     * If {@link GhostRenderBehaviour#getPassCount(ItemStack, ItemStack)} returns a value higher than one, this will
     * only be called for the first block and controls whether all or none of the blocks are rendered.
     *
     * @param stack The {@link ItemStack} in the players main hand
     * @param proxiedStack The proxied {@code ItemStack} as returned from {@link GhostRenderBehaviour#getProxiedStack(ItemStack)}
     * @param hit The {@link BlockHitResult} from {@link Minecraft#hitResult}
     * @param ctx The {@link BlockPlaceContext} to use for determining the resulting
     * @param hitState The {@code BlockState} hit by the given {@code BlockHitResult}
     * @param renderState The {@code BlockState} to render
     * @param renderPos The {@code BlockPos} the {@code BlockState} will be rendered at
     * @return True if the {@code BlockState} can actually render at the given {@link BlockPos}
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean canRenderAt(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            BlockState renderState,
            BlockPos renderPos
    )
    {
        Level level = ctx.getLevel();
        Player player = Objects.requireNonNull(ctx.getPlayer());

        return level.isUnobstructed(renderState, renderPos, CollisionContext.of(player)) && level.getBlockState(renderPos).canBeReplaced(ctx);
    }

    /**
     * Read and return the camo(s) stored in the given {@link ItemStack} or return {@link CamoList#EMPTY} if no camos are present.
     *
     * @param stack        The {@link ItemStack} in the players main hand
     * @param proxiedStack The proxied {@code ItemStack} as returned from {@link GhostRenderBehaviour#getProxiedStack(ItemStack)}
     * @param renderPass   The current render pass index
     * @return The camo(s) stored to apply to the rendered block
     */
    default CamoList readCamo(ItemStack stack, @Nullable ItemStack proxiedStack, int renderPass)
    {
        return stack.getOrDefault(Utils.DC_TYPE_CAMO_LIST, CamoList.EMPTY);
    }

    /**
     * Post-process the {@link CamoList} that was previously read from the {@link ItemStack} with the given context.
     * Separated from {@link GhostRenderBehaviour#readCamo(ItemStack, ItemStack, int)} to allow the camo to be read
     * by a proxying item while allowing the proxied item to manipulate it according to the context.
     *
     * @param stack        The {@link ItemStack} in the players main hand
     * @param proxiedStack The proxied {@code ItemStack} as returned from {@link GhostRenderBehaviour#getProxiedStack(ItemStack)}
     * @param ctx          The {@link BlockPlaceContext} to use for determining the resulting
     * @param renderState  The {@code BlockState} to render
     * @param renderPass   The current render pass index
     * @param camo         The {@code CamoPair} previously read by {@link GhostRenderBehaviour#readCamo(ItemStack, ItemStack, int)}
     * @return The {@code CamoPair} with any necessary modifications applied to it
     */
    default CamoList postProcessCamo(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            CamoList camo
    )
    {
        return camo;
    }

    /**
     * Build the {@link ModelData} to render the placement preview with. Allows full control over the model data creation,
     * for example to allow custom double blocks with two camos
     *
     * @param stack The {@link ItemStack} in the players main hand
     * @param proxiedStack The proxied {@code ItemStack} as returned from {@link GhostRenderBehaviour#getProxiedStack(ItemStack)}
     * @param ctx The {@link BlockPlaceContext} to use for determining the resulting
     * @param renderState The {@code BlockState} to render
     * @param renderPass The current render pass index
     * @param camo The prepared {@code ModelData} to be given to the {@link BakedModel} that is to be rendered
     * @return The {@code ModelData} with any necessary modifications applied to it
     */
    default ModelData buildModelData(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            CamoList camo
    )
    {
        return ModelData.builder().with(FramedBlockData.PROPERTY, new FramedBlockData(camo.getCamo(0).getContent(), false)).build();
    }

    /**
     * Append any additional data apart from the camos to the given {@link ModelData}.
     *
     * @param stack The {@link ItemStack} in the players main hand
     * @param proxiedStack The proxied {@code ItemStack} as returned from {@link GhostRenderBehaviour#getProxiedStack(ItemStack)}
     * @param ctx The {@link BlockPlaceContext} to use for determining the resulting
     * @param renderState The {@code BlockState} to render
     * @param renderPass The current render pass index
     * @param data The prepared {@code ModelData} to be given to the {@link BakedModel} that is to be rendered
     * @return The {@code ModelData} with any necessary modifications applied to it
     */
    default ModelData appendModelData(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            int renderPass,
            ModelData data
    )
    {
        return data;
    }
}
