package io.github.xfacthd.framedblocks.api.model.item;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

/// Specifies how to compute the model data and tint values for rendering a framed block's block item
/// via its blockstate model, optionally with camo(s) and a block overlay applied.
public interface ItemModelDataProvider {
    /// Default data provider for single-camo blocks without additional visual behavior.
    ItemModelDataProvider DEFAULT = new ItemModelDataProvider() {};
    /// Default data provider for double-camo blocks without additional visual behavior.
    ItemModelDataProvider DOUBLE_BLOCK = new DoubleBlockItemModelDataProvider();

    /// {@return the {@link ModelData} containing the camos from the item data in the format required for the associated item's model}
    ///
    /// @param state   The blockstate used for resolving the blockstate model backing the item model
    /// @param camos   The camos stored on the item stack to display on the model
    /// @param overlay The block overlay stored on the item stack to display on the model
    default ModelData buildItemModelData(BlockState state, CamoList camos, @Nullable Holder<BlockOverlay> overlay) {
        return ModelData.of(AbstractFramedBlockData.PROPERTY, new FramedBlockData(state, camos.getCamo(0), false, overlay));
    }

    /// {@return additional data needed to correctly cache item model geometry}
    ///
    /// @param stack The item stack being rendered
    default @Nullable Object computeCacheKey(ItemStack stack) {
        return null;
    }

    /// Append additional tint values for quads unrelated to the block's camo(s).
    ///
    /// @param stack The item stack being rendered
    /// @param tints The list to append the tint values to
    default void appendTintValues(ItemStack stack, IntList tints) { }
}
