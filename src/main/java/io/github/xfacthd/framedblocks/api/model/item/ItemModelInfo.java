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

public interface ItemModelInfo {
    ItemModelInfo DEFAULT = new ItemModelInfo() {};

    /**
     * {@return whether the associated item model requires data even when no camos are present}
     */
    default boolean isDataRequired() {
        return false;
    }

    /**
     * {@return the {@link ModelData} containing the camos from the item data in the format required for the associated item's model}
     */
    default ModelData buildItemModelData(BlockState state, CamoList camos, @Nullable Holder<BlockOverlay> overlay) {
        return ModelData.of(AbstractFramedBlockData.PROPERTY, new FramedBlockData(state, camos.getCamo(0), false, overlay));
    }

    /**
     * {@return additional data needed to correctly cache item model geometry}
     */
    default @Nullable Object computeCacheKey(ItemStack stack) {
        return null;
    }

    /// Append additional tint values for quads unrelated to the block's camo(s)
    default void appendTintValues(ItemStack stack, IntList tints) { }
}
