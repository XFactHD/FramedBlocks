package xfacthd.framedblocks.api.ghost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.camo.CamoContainer;

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
     * @return True if the block to be rendered consists of two separate blocks
     */
    default boolean hasSecondBlock(ItemStack stack, @Nullable ItemStack proxiedStack)
    {
        return false;
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
     * @param secondPass True if {@link GhostRenderBehaviour#hasSecondBlock(ItemStack, ItemStack)} returns true and the
     *                   second block is being rendered, otherwise false
     * @return The {@code BlockState} to render or null when none could be determined
     */
    @Nullable
    default BlockState getRenderState(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            boolean secondPass
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
     * @param secondPass True if {@link GhostRenderBehaviour#hasSecondBlock(ItemStack, ItemStack)} returns true and
     *                   the second block is being rendered, otherwise false
     * @return The {@code BlockPos} at which the block should be rendered
     */
    default BlockPos getRenderPos(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockHitResult hit,
            BlockPlaceContext ctx,
            BlockState hitState,
            BlockPos defaultPos,
            boolean secondPass
    )
    {
        return defaultPos;
    }

    /**
     * Determine whether the previously calculated {@link BlockState} can actually render at the given {@link BlockPos}.
     * If {@link GhostRenderBehaviour#hasSecondBlock(ItemStack, ItemStack)} returns true, this will only be called for the first block
     * and controls whether both or none of the blocks are rendered.
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
     * Read and return the camo(s) stored in the given {@link ItemStack} or return {@link CamoPair#EMPTY} if no camos are present.
     *
     * @param stack The {@link ItemStack} in the players main hand
     * @param proxiedStack The proxied {@code ItemStack} as returned from {@link GhostRenderBehaviour#getProxiedStack(ItemStack)}
     * @param secondPass True if {@link GhostRenderBehaviour#hasSecondBlock(ItemStack, ItemStack)} returns true and
     *                   the second block is being rendered, otherwise false
     * @return The camo(s) stored to apply to the rendered block
     */
    default CamoPair readCamo(ItemStack stack, @Nullable ItemStack proxiedStack, boolean secondPass)
    {
        //noinspection ConstantConditions
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag"))
        {
            CompoundTag tag = stack.getTag().getCompound("BlockEntityTag").getCompound("camo");
            CamoContainer camo = CamoContainer.load(tag);
            return new CamoPair(camo.getState(), Blocks.AIR.defaultBlockState());
        }
        return CamoPair.EMPTY;
    }

    /**
     * Post-process the {@link CamoPair} that was previously read from the {@link ItemStack} with the given context.
     * Separated from {@link GhostRenderBehaviour#readCamo(ItemStack, ItemStack, boolean)} to allow the camo to be read
     * by a proxying item while allowing the proxied item to manipulate it according to the context.
     *
     * @param stack The {@link ItemStack} in the players main hand
     * @param proxiedStack The proxied {@code ItemStack} as returned from {@link GhostRenderBehaviour#getProxiedStack(ItemStack)}
     * @param ctx The {@link BlockPlaceContext} to use for determining the resulting
     * @param renderState The {@code BlockState} to render
     * @param secondPass True if {@link GhostRenderBehaviour#hasSecondBlock(ItemStack, ItemStack)} returns true and
     *                   the second block is being rendered, otherwise false
     * @param camo The {@code CamoPair} previously read by {@link GhostRenderBehaviour#readCamo(ItemStack, ItemStack, boolean)}
     * @return The {@code CamoPair} with any necessary modifications applied to it
     */
    default CamoPair postProcessCamo(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            boolean secondPass,
            CamoPair camo
    )
    {
        return camo;
    }

    /**
     * Append any additional data apart from the camos to the given {@link ModelData}.
     *
     * @param stack The {@link ItemStack} in the players main hand
     * @param proxiedStack The proxied {@code ItemStack} as returned from {@link GhostRenderBehaviour#getProxiedStack(ItemStack)}
     * @param ctx The {@link BlockPlaceContext} to use for determining the resulting
     * @param renderState The {@code BlockState} to render
     * @param secondPass True if {@link GhostRenderBehaviour#hasSecondBlock(ItemStack, ItemStack)} returns true and
     *                   the second block is being rendered, otherwise false
     * @param data The prepared {@code ModelData} to be given to the {@link BakedModel} that is to be rendered
     * @return The {@code ModelData} with any necessary modifications applied to it
     */
    default ModelData appendModelData(
            ItemStack stack,
            @Nullable ItemStack proxiedStack,
            BlockPlaceContext ctx,
            BlockState renderState,
            boolean secondPass,
            ModelData data
    )
    {
        return data;
    }
}
