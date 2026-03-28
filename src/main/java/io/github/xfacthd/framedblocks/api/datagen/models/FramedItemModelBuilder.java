package io.github.xfacthd.framedblocks.api.datagen.models;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import io.github.xfacthd.framedblocks.api.model.item.block.BlockItemModelProvider;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
public final class FramedItemModelBuilder {
    private final Holder<Block> block;
    private BlockItemModelProvider modelProvider = BlockItemModelProvider.DEFAULT;
    private Identifier itemBaseModel = AbstractFramedBlockModelProvider.FRAMED_CUBE_MODEL;

    FramedItemModelBuilder(Holder<Block> block) {
        Preconditions.checkArgument(
                block.value() instanceof IFramedBlock,
                "Expected IFramedBlock, got %s", block.value()
        );
        Preconditions.checkArgument(
                ((IFramedBlock) block.value()).getItemModelSource() != null,
                "Framed block %s does not provide an item model source state", block.value()
        );
        this.block = block;
    }

    /**
     * Specify the {@link BlockItemModelProvider} to use for retrieving the {@link BlockStateModel} which the
     * item model will be based on.
     * <p>
     * Allows using dedicated block models with camo awareness when the item model looks different to all variants
     * of the actual block model.
     */
    public FramedItemModelBuilder modelProvider(BlockItemModelProvider modelProvider) {
        this.modelProvider = modelProvider;
        return this;
    }

    /**
     * Specify the model from which the {@link ItemTransforms} should be pulled
     */
    public FramedItemModelBuilder itemBaseModel(Identifier itemBaseModel) {
        this.itemBaseModel = itemBaseModel;
        return this;
    }

    public ItemModel.Unbaked build() {
        return InternalClientAPI.INSTANCE.createFramedBlockItemModel(block.value(), modelProvider, itemBaseModel);
    }
}
