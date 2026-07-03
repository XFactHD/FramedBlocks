package io.github.xfacthd.framedblocks.api.model.item;

import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedDoubleBlockData;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

/// Base item model data provider implementation for framed blocks with two camos.
public class DoubleBlockItemModelDataProvider implements ItemModelDataProvider {
    @Override
    public final ModelData buildItemModelData(BlockState state, CamoList camos, @Nullable Holder<BlockOverlay> overlay) {
        AbstractFramedBlockData fbData = new FramedDoubleBlockData(
                ((IFramedDoubleBlock) state.getBlock()).getCache(state).getParts(),
                new FramedBlockData(state, camos.getCamo(0), false, overlay),
                new FramedBlockData(state, camos.getCamo(1), true, overlay)
        );

        ModelData.Builder builder = ModelData.builder().with(AbstractFramedBlockData.PROPERTY, fbData);
        appendItemModelData(builder, state);
        return builder.build();
    }

    /// Append additional non-camo data to the model data used for querying the blockstate model.
    ///
    /// @param builder The model data builder to append to
    /// @param state   The blockstate used for resolving the blockstate model backing the item model
    protected void appendItemModelData(ModelData.Builder builder, BlockState state) { }
}
