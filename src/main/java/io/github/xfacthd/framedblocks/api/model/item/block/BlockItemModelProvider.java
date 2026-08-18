package io.github.xfacthd.framedblocks.api.model.item.block;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/// Specifies how to get the blockstate model backing the item model based on the ["item model source"][IFramedBlock#getItemModelSource()]
/// state provided by the framed block.
public interface BlockItemModelProvider {
    /// Default provider using the model of the "item model source" state.
    BlockItemModelProvider DEFAULT = (state, _) -> ModelUtils.getModel(state);

    /// {@return the blockstate model to use for the item model}
    ///
    /// @param state The "item model source" state provided by the framed block
    /// @param baker The baker used to bake the item model
    BlockStateModel create(BlockState state, ModelBaker baker);

    /// {@return a blockitem model provider based on the given geometry factory}
    ///
    /// @param srcState The blockstate to use for looking up the "base" model or null to use the block's "item model source" state
    /// @param geometry The geometry backing the resulting model
    static BlockItemModelProvider forGeometry(@Nullable BlockState srcState, GeometryFactory geometry) {
        return InternalClientAPI.INSTANCE.createBlockItemModelProviderForGeometry(srcState, geometry);
    }
}
