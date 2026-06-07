package io.github.xfacthd.framedblocks.api.datagen.models;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Either;
import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelDataProvider;
import io.github.xfacthd.framedblocks.api.model.item.block.BlockItemModelProvider;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
public final class FramedItemModelBuilder {
    private static final Identifier DEFAULT_BASE_MODEL = AbstractFramedBlockModelProvider.FRAMED_CUBE_MODEL;

    private final Holder<Block> block;
    private BlockItemModelProvider modelProvider = BlockItemModelProvider.DEFAULT;
    @Nullable
    private Identifier itemBaseModel;
    @Nullable
    private ItemTransforms transforms;
    private boolean requiresData;
    private Optional<ItemModelDataProvider> dataProvider = Optional.empty();

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
        Preconditions.checkState(this.itemBaseModel == null, "Item base model already specified");
        Preconditions.checkState(this.transforms == null, "Item base model cannot be combined with embedded transforms");
        this.itemBaseModel = itemBaseModel;
        return this;
    }

    /// Specify [ItemTransforms] to embed in the client item file
    public FramedItemModelBuilder transforms(UnaryOperator<ItemTransformsBuilder> builderOperator) {
        Preconditions.checkState(this.transforms == null, "Item transforms already specified");
        Preconditions.checkState(this.itemBaseModel == null, "Embedded transforms cannot be combined with an item base model");
        this.transforms = builderOperator.apply(new ItemTransformsBuilder()).build();
        return this;
    }

    /// Indicates that the block model used by this item model requires [ModelData] even if no camo is present.
    public FramedItemModelBuilder requiresData() {
        requiresData = true;
        return this;
    }

    /// Specify the [ItemModelDataProvider] to use for computing additional data relevant to caching of the item model geometry
    /// and for computing non-camo tint values.
    ///
    /// If unspecified, item models will use [ItemModelDataProvider#DOUBLE_BLOCK] if [IBlockType#isDoubleBlock()] returns `true`
    /// and [ItemModelDataProvider#DEFAULT] if it returns `false`.
    public FramedItemModelBuilder dataProvider(ItemModelDataProvider dataProvider) {
        this.dataProvider = Optional.of(dataProvider);
        return this;
    }

    public ItemModel.Unbaked build() {
        Either<Identifier, ItemTransforms> modelOrXform;
        if (transforms != null) {
            modelOrXform = Either.right(transforms);
        } else {
            modelOrXform = Either.left(Objects.requireNonNullElse(itemBaseModel, DEFAULT_BASE_MODEL));
        }
        return InternalClientAPI.INSTANCE.createFramedBlockItemModel(block.value(), modelProvider, modelOrXform, requiresData, dataProvider);
    }
}
