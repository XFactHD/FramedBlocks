package io.github.xfacthd.framedblocks.api.camo.block;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public abstract class AbstractBlockCamoContainerFactory<T extends AbstractBlockCamoContainer<T>> extends CamoContainerFactory<T> {
    @Override
    public final @Nullable T applyCamo(Level level, BlockPos pos, Player player, ItemAccess itemAccess) {
        BlockState state = getStateFromItemStack(level, pos, player, itemAccess);
        if (state != null && !(state.getBlock() instanceof IFramedBlock) && isValidBlock(state, level, pos, player)) {
            try (Transaction tx = Transaction.open(null)) {
                T container = createContainer(state, level, pos, player, itemAccess);
                if (!level.isClientSide() && !player.isCreative() && ConfigView.Server.INSTANCE.shouldConsumeCamoItem()) {
                    if (itemAccess.extract(itemAccess.getResource(), 1, tx) != 1) {
                        return null;
                    }
                    tx.commit();
                    player.getInventory().setChanged();
                }
                return container;
            }
        }
        return null;
    }

    @Override
    public final boolean removeCamo(Level level, BlockPos pos, Player player, ItemAccess itemAccess, T container) {
        if (!level.isClientSide() && (player.isCreative() || ConfigView.Server.INSTANCE.shouldConsumeCamoItem())) {
            ItemStack result = createItemStack(level, pos, player, itemAccess, container);
            try (Transaction tx = Transaction.open(null)) {
                if (itemAccess.insert(ItemResource.of(result), result.getCount(), tx) != result.getCount()) {
                    return false;
                }
                tx.commit();
            }
        }
        return true;
    }

    @Override
    public final boolean validateCamo(T container) {
        if (container.getState().getBlock() instanceof IFramedBlock) {
            return false;
        }
        return isValidBlock(container.getState(), EmptyBlockGetter.INSTANCE, BlockPos.ZERO, null);
    }

    /**
     * {@return the {@linkplain BlockState camo state} resulting from the stack in the given {@link ItemAccess} and context}
     */
    protected @Nullable BlockState getStateFromItemStack(Level level, BlockPos pos, Player player, ItemAccess itemAccess) {
        if (itemAccess.getResource().getItem() instanceof BlockItem item) {
            return item.getBlock().defaultBlockState();
        }
        return null;
    }

    /**
     * {@return a new camo container from the given {@linkplain BlockState camo state} and context}
     */
    protected abstract T createContainer(BlockState camoState, Level level, BlockPos pos, Player player, ItemAccess itemAccess);

    /**
     * {@return a copy of the given camo container with the given new {@linkplain BlockState camo state}}
     */
    protected abstract T copyContainerWithState(T original, BlockState newCamoState);

    /**
     * {@return a new {@link ItemStack} to be given to the player when removing the camo with the given stack in hand}
     */
    protected abstract ItemStack createItemStack(Level level, BlockPos pos, Player player, ItemAccess itemAccess, T container);

    /**
     * Validate that the given {@linkplain BlockState camo state} is a valid camo
     * @return true to keep the camo, false to discard it
     */
    protected abstract boolean isValidBlock(BlockState camoState, BlockGetter level, BlockPos pos, @Nullable Player player);

    @ApiStatus.Internal
    public final boolean isValidBlockInternal(BlockState camoState) {
        return isValidBlock(camoState, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, null);
    }
}
