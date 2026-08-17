package io.github.xfacthd.framedblocks.api.model.wrapping;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.internal.ModelWrapperRegistrar;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneModelFactory;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.model.template.GeometryTemplateSpec;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/// Event for registering model wrappers for framed blocks.
///
/// Fired on the mod event bus only on the physical client.
public final class RegisterModelWrappersEvent extends Event implements IModBusEvent {
    private final ModelWrapperRegistrar registrar;

    @ApiStatus.Internal
    public RegisterModelWrappersEvent(ModelWrapperRegistrar registrar) {
        this.registrar = registrar;
    }

    /// Wrap the models of all states of the given block with models generated from [Geometry]s created by
    /// the given [GeometryFactory].
    ///
    /// States which match an already wrapped state after applying the given [StateMerger] will re-use the
    /// existing wrapped model.
    ///
    /// @param block           The block whose models to wrap (must implement [IFramedBlock])
    /// @param geometryFactory The [GeometryFactory] to generate the wrapping models with
    /// @param stateMerger     The [StateMerger] to use for merging visually redundant states during wrapping
    public void wrapSingle(Holder<Block> block, GeometryFactory geometryFactory, StateMerger stateMerger) {
        registrar.wrapSingle(block, geometryFactory, stateMerger);
    }

    /// Wrap the models of all states of the given block with models generated from [Geometry]s created from
    /// the given [GeometryTemplateSpec].
    ///
    /// States which match an already wrapped state after applying the given [StateMerger] will re-use the
    /// existing wrapped model.
    ///
    /// @param block        The block whose models to wrap (must implement [IFramedBlock])
    /// @param templateSpec The [GeometryTemplateSpec] to generate the wrapping models with
    /// @param stateMerger  The [StateMerger] to use for merging visually redundant states during wrapping
    public void wrapSingle(Holder<Block> block, GeometryTemplateSpec templateSpec, StateMerger stateMerger) {
        registrar.wrapSingle(block, templateSpec, stateMerger);
    }

    /// Wrap the models of all states of the given block with double block models using the [DoubleBlockParts]
    /// retrieved from the given block for the respective state.
    ///
    /// States which match an already wrapped state after applying the given [StateMerger] will re-use the
    /// existing wrapped model.
    ///
    /// @param block       The block whose models to wrap (must implement [IFramedDoubleBlock])
    /// @param stateMerger The [StateMerger] to use for merging visually redundant states during wrapping
    public void wrapDouble(Holder<Block> block, StateMerger stateMerger) {
        registrar.wrapDouble(block, stateMerger);
    }

    /// Wrap the models of all states of the given block with models created by the given [ModelFactory].
    ///
    /// States which match an already wrapped state after applying the given [StateMerger] will re-use the
    /// existing wrapped model.
    ///
    /// @param block        The block whose models to wrap
    /// @param modelFactory The [ModelFactory] to generate the wrapping models with
    /// @param stateMerger  The [StateMerger] to use for merging visually redundant states during wrapping
    public void wrapCustom(Holder<Block> block, ModelFactory modelFactory, StateMerger stateMerger) {
        registrar.wrapCustom(block, modelFactory, stateMerger);
    }

    /// Re-use the wrapped models from the given source block for the given block.
    ///
    /// States which match an already handled state after applying the given [StateMerger] to the target block's
    /// state will re-use the previously retrieved model.
    ///
    /// @param block       The block whose models to replace
    /// @param srcBlock    The block whose models to re-use
    /// @param stateMerger The [StateMerger] to use for merging visually redundant states during copying before applying
    ///                    the target block's properties to the source block for retrieving the wrapped model
    public void copyModels(Holder<Block> block, Holder<Block> srcBlock, StateMerger stateMerger) {
        registrar.copyModels(block, srcBlock, stateMerger);
    }

    /// Wrap the model of the given block's default state in a model providing no parts and apply this model to all states.
    ///
    /// @param block The block whose models to replace
    public void wrapEmpty(Holder<Block> block) {
        registrar.wrapEmpty(block);
    }

    /// Wrap the models loaded from the definition file of the given wrapper key with models generated from [Geometry]s created by
    /// the given [GeometryFactory].
    ///
    /// States which match an already wrapped state after applying the given [StateMerger] will re-use the
    /// existing wrapped model.
    ///
    /// @param wrapperKey      The wrapper key whose models to wrap
    /// @param geometryFactory The [GeometryFactory] to generate the wrapping models with
    /// @param modelFactory    The model factory to use for constructing the standalone model from the wrapped blockstate models
    /// @param stateMerger     The [StateMerger] to use for merging visually redundant states during wrapping
    public <T> void wrapStandalone(StandaloneWrapperKey<T> wrapperKey, GeometryFactory geometryFactory, StandaloneModelFactory<T> modelFactory, StateMerger stateMerger) {
        registrar.wrapStandalone(wrapperKey, geometryFactory, modelFactory, stateMerger);
    }

    /// Wrap the models loaded from the definition file of the given wrapper key with models generated from [Geometry]s created from
    /// the given [GeometryTemplateSpec].
    ///
    /// States which match an already wrapped state after applying the given [StateMerger] will re-use the
    /// existing wrapped model.
    ///
    /// @param wrapperKey   The wrapper key whose models to wrap
    /// @param templateSpec The [GeometryTemplateSpec] to generate the wrapping models with
    /// @param modelFactory The model factory to use for constructing the standalone model from the wrapped blockstate models
    /// @param stateMerger  The [StateMerger] to use for merging visually redundant states during wrapping
    public <T> void wrapStandalone(StandaloneWrapperKey<T> wrapperKey, GeometryTemplateSpec templateSpec, StandaloneModelFactory<T> modelFactory, StateMerger stateMerger) {
        registrar.wrapStandalone(wrapperKey, templateSpec, modelFactory, stateMerger);
    }

    /// Replace the block model used for rendering the given block in dynamic contexts (i.e. BERs, Jade tooltip, etc.).
    ///
    /// @param block        The block to replace the model for
    /// @param modelFactory The factory to create the models with
    public void overrideBlockModelFactory(Holder<Block> block, Function<BlockState, BlockModel.Unbaked> modelFactory) {
        registrar.overrideBlockModelFactory(block, modelFactory);
    }
}
