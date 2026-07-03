package io.github.xfacthd.framedblocks.api.model.item.block;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

/// Specifies how to get the blockstate model backing the item model based on the ["item model source"][IFramedBlock#getItemModelSource()]
/// state provided by the framed block.
public interface BlockItemModelProvider {
    /// Default provider using the model of the "item model source" state.
    BlockItemModelProvider DEFAULT = (state, _) -> () -> ModelUtils.getModel(state);

    /// {@return a supplier providing the blockstate model to use for the item model}
    /// The returned supplier will be memoized internally.
    ///
    /// @param state The "item model source" state provided by the framed block
    /// @param baker The baker used to bake the item model
    Supplier<BlockStateModel> create(BlockState state, ModelBaker baker);

    /// {@return a supplier creating a blockstate model from the given geometry factory}
    ///
    /// @param state    The blockstate to use for instantiating the geometry
    /// @param srcState The blockstate to use for looking up the "base" model
    /// @param geometry The geometry backing the resulting model
    /// @param baker    The baker used to bake the item model
    static Supplier<BlockStateModel> forGeometry(BlockState state, BlockState srcState, GeometryFactory geometry, ModelBaker baker) {
        return InternalClientAPI.INSTANCE.createBlockItemModelProviderForGeometry(state, srcState, geometry, baker);
    }
}
