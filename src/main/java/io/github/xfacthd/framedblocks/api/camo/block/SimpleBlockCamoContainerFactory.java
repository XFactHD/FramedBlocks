package io.github.xfacthd.framedblocks.api.camo.block;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.access.ItemAccess;

/// Basic camo container factory implementation for simple block-based camos which hold only a [BlockState] and only need minimal
/// context to be applied and removed. Camos using this factory must be trivially droppable (i.e. they must not require
/// consumption of an item during removal or any player or level context for dropping).
public abstract class SimpleBlockCamoContainerFactory extends AbstractBlockCamoContainerFactory<SimpleBlockCamoContainer> {
    public static final Component MSG_BLOCK_ENTITY = Utils.translate("msg", "camo.block_entity");
    public static final Component MSG_NON_SOLID = Utils.translate("msg", "camo.non_solid");

    private final MapCodec<SimpleBlockCamoContainer> codec = BlockState.CODEC
            .xmap(state -> new SimpleBlockCamoContainer(state, this), SimpleBlockCamoContainer::getState).fieldOf("state");
    private final StreamCodec<ByteBuf, SimpleBlockCamoContainer> streamCodec = ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)
            .map(state -> new SimpleBlockCamoContainer(state, this), SimpleBlockCamoContainer::getState);
    private final BlockCamoCraftingHandler craftingHandler = new BlockCamoCraftingHandler(this);

    @Override
    public ItemStack dropCamo(SimpleBlockCamoContainer container) {
        return new ItemStack(container.getState().getBlock());
    }

    @Override
    public BlockCamoCraftingHandler getCraftingHandler() {
        return craftingHandler;
    }

    @Override
    protected final SimpleBlockCamoContainer createContainer(BlockState camoState, Level level, BlockPos pos, Player player, ItemAccess itemAccess) {
        return new SimpleBlockCamoContainer(camoState, this);
    }

    @Override
    protected final SimpleBlockCamoContainer copyContainerWithState(SimpleBlockCamoContainer original, BlockState newCamoState) {
        return new SimpleBlockCamoContainer(newCamoState, this);
    }

    @Override
    protected final ItemStack createItemStack(Level level, BlockPos pos, Player player, ItemAccess itemAccess, SimpleBlockCamoContainer container) {
        return dropCamo(container);
    }

    @Override
    public final boolean canTriviallyConvertToItemStack() {
        return true;
    }

    @Override
    protected final void writeToNetwork(ValueOutput valueOutput, SimpleBlockCamoContainer container) {
        valueOutput.putInt("state", Block.getId(container.getState()));
    }

    @Override
    protected final SimpleBlockCamoContainer readFromNetwork(ValueInput valueInput) {
        return new SimpleBlockCamoContainer(Block.stateById(valueInput.getIntOr("state", -1)), this);
    }

    @Override
    public final MapCodec<SimpleBlockCamoContainer> codec() {
        return codec;
    }

    @Override
    public final StreamCodec<? super RegistryFriendlyByteBuf, SimpleBlockCamoContainer> streamCodec() {
        return streamCodec;
    }
}
