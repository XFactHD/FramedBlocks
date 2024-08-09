package xfacthd.framedblocks.api.camo.block;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.ConfigView;

/**
 * Basic block camo container factory for simple camos based on only a {@link BlockState} which only need minimal
 * context to be applied and removed. Camos using this factory must be trivially droppable (i.e. they must not require
 * consumption of an item during removal or any player or level context for dropping)
 */
public abstract class SimpleBlockCamoContainerFactory extends AbstractBlockCamoContainerFactory<SimpleBlockCamoContainer>
{
    private final MapCodec<SimpleBlockCamoContainer> codec = BlockState.CODEC
            .xmap(state -> new SimpleBlockCamoContainer(state, this), SimpleBlockCamoContainer::getState).fieldOf("state");
    private final StreamCodec<ByteBuf, SimpleBlockCamoContainer> streamCodec = ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)
            .map(state -> new SimpleBlockCamoContainer(state, this), SimpleBlockCamoContainer::getState);

    @Override
    public ItemStack dropCamo(SimpleBlockCamoContainer container)
    {
        return new ItemStack(container.getState().getBlock());
    }

    @Override
    public boolean canApplyInCraftingRecipe(ItemStack stack)
    {
        if (stack.getItem() instanceof BlockItem item)
        {
            BlockState state = item.getBlock().defaultBlockState();
            return isValidBlock(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, null);
        }
        return false;
    }

    @Override
    public SimpleBlockCamoContainer applyCamoInCraftingRecipe(ItemStack stack)
    {
        if (stack.getItem() instanceof BlockItem item)
        {
            BlockState state = item.getBlock().defaultBlockState();
            if (isValidBlock(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, null))
            {
                return new SimpleBlockCamoContainer(item.getBlock().defaultBlockState(), this);
            }
        }
        throw new IllegalStateException("applyCamoInCraftingRecipe() called without canApplyInCraftingRecipe() check");
    }

    @Override
    public ItemStack getCraftingRemainder(ItemStack stack)
    {
        if (!ConfigView.Server.INSTANCE.shouldConsumeCamoItem())
        {
            return stack.copy();
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected final SimpleBlockCamoContainer createContainer(BlockState camoState, Level level, BlockPos pos, Player player, ItemStack stack)
    {
        return new SimpleBlockCamoContainer(camoState, this);
    }

    @Override
    protected final SimpleBlockCamoContainer copyContainerWithState(SimpleBlockCamoContainer original, BlockState newCamoState)
    {
        return new SimpleBlockCamoContainer(newCamoState, this);
    }

    @Override
    protected final ItemStack createItemStack(Level level, BlockPos pos, Player player, ItemStack stack, SimpleBlockCamoContainer container)
    {
        return dropCamo(container);
    }

    @Override
    public final boolean canTriviallyConvertToItemStack()
    {
        return true;
    }

    @Override
    protected final void writeToNetwork(CompoundTag tag, SimpleBlockCamoContainer container)
    {
        tag.putInt("state", Block.getId(container.getState()));
    }

    @Override
    protected final SimpleBlockCamoContainer readFromNetwork(CompoundTag tag)
    {
        return new SimpleBlockCamoContainer(Block.stateById(tag.getInt("state")), this);
    }

    @Override
    public final MapCodec<SimpleBlockCamoContainer> codec()
    {
        return codec;
    }

    @Override
    public final StreamCodec<? super RegistryFriendlyByteBuf, SimpleBlockCamoContainer> streamCodec()
    {
        return streamCodec;
    }
}
