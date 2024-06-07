package xfacthd.framedblocks.api.camo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.util.ConfigView;
import xfacthd.framedblocks.api.util.Utils;

public abstract class AbstractBlockCamoContainerFactory<T extends AbstractBlockCamoContainer<T>> extends CamoContainerFactory<T>
{
    @Override
    public final T applyCamo(Level level, BlockPos pos, Player player, ItemStack stack)
    {
        if (stack.getItem() instanceof BlockItem item)
        {
            BlockState state = item.getBlock().defaultBlockState();
            if (!(state.getBlock() instanceof IFramedBlock) && isValidBlock(state, level, pos, player))
            {
                T container = createContainer(state, level, pos, player, stack);
                if (!level.isClientSide() && !player.isCreative() && ConfigView.Server.INSTANCE.shouldConsumeCamoItem())
                {
                    stack.shrink(1);
                    player.getInventory().setChanged();
                }
                return container;
            }
        }
        return null;
    }

    @Override
    public final boolean removeCamo(Level level, BlockPos pos, Player player, ItemStack stack, T container)
    {
        if (!level.isClientSide())
        {
            ItemStack result = createItemStack(level, pos, player, stack, container);
            Utils.giveToPlayer(player, result, ConfigView.Server.INSTANCE.shouldConsumeCamoItem());
        }
        return true;
    }

    @Override
    public final boolean validateCamo(T container)
    {
        if (container.getState().getBlock() instanceof IFramedBlock) return false;
        return isValidBlock(container.getState(), EmptyBlockGetter.INSTANCE, BlockPos.ZERO, null);
    }

    /**
     * {@return a new camo container from the given {@linkplain BlockState camo state} and context}
     */
    protected abstract T createContainer(BlockState camoState, Level level, BlockPos pos, Player player, ItemStack stack);

    /**
     * {@return a copy of the given camo container with the given new {@linkplain BlockState camo state}}
     */
    protected abstract T copyContainerWithState(T original, BlockState newCamoState);

    /**
     * {@return a new {@link ItemStack} to be given to the player when removing the camo with the given stack in hand}
     */
    protected abstract ItemStack createItemStack(Level level, BlockPos pos, Player player, ItemStack stack, T container);

    /**
     * Validate that the given {@linkplain BlockState camo state} is a valid camo
     * @return true to keep the camo, false to discard it
     */
    protected abstract boolean isValidBlock(BlockState camoState, BlockGetter level, BlockPos pos, @Nullable Player player);
}
