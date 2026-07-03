package io.github.xfacthd.framedblocks.api.model.wrapping;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneModelFactory;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Set;
import java.util.function.Function;

/// Provides methods for registering model wrappers during [RegisterModelWrappersEvent].
@SuppressWarnings("unused")
public final class WrapHelper {
    /// Set of properties that all blocks should ignore when wrapping models.
    public static final Set<Property<?>> IGNORED_PROPS = Utils.concat(BlockUtils.REQUIRED_STATE_PROPERTIES, Set.of(
            FramedProperties.SOLID,
            FramedProperties.PROPAGATES_SKYLIGHT,
            FramedProperties.GLOWING,
            BlockStateProperties.WATERLOGGED,
            FramedProperties.STATE_LOCKED
    ));
    /// Default state merger to use for all blocks which only need to ignore the above properties.
    public static final StateMerger DEFAULT_MERGER = StateMerger.ignoring(IGNORED_PROPS);
    /// State merger for blocks which need to ignore the default properties and [BlockStateProperties#POWERED].
    public static final StateMerger POWERED_MERGER = StateMerger.ignoring(Utils.concat(IGNORED_PROPS, Set.of(BlockStateProperties.POWERED)));

    /// Wrap the models of all states of the given block with models generated from [Geometry]s created by
    /// the given [GeometryFactory].
    ///
    /// States which match an already wrapped state after resetting the given ignored properties to default values
    /// will re-use the existing wrapped model.
    ///
    /// @param block           The block whose models to wrap (must implement [IFramedBlock])
    /// @param geometryFactory The [GeometryFactory] to generate the wrapping models with
    /// @param ignoredProps    The state properties to ignore during wrapping
    public static void wrap(Holder<Block> block, GeometryFactory geometryFactory, Set<Property<?>> ignoredProps) {
        wrap(block, geometryFactory, StateMerger.ignoring(ignoredProps));
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
    public static void wrap(Holder<Block> block, GeometryFactory geometryFactory, StateMerger stateMerger) {
        InternalClientAPI.INSTANCE.registerModelWrapper(block, geometryFactory, stateMerger);
    }

    /// Wrap the models of all states of the given block with double block models using the [DoubleBlockParts]
    /// retrieved from the given block for the respective state.
    ///
    /// States which match an already wrapped state after resetting the given ignored properties to default values
    /// will re-use the existing wrapped model.
    ///
    /// @param block        The block whose models to wrap (must implement [IFramedDoubleBlock])
    /// @param ignoredProps The state properties to ignore during wrapping
    public static void wrapDouble(Holder<Block> block, Set<Property<?>> ignoredProps) {
        wrapDouble(block, StateMerger.ignoring(ignoredProps));
    }

    /// Wrap the models of all states of the given block with double block models using the [DoubleBlockParts]
    /// retrieved from the given block for the respective state.
    ///
    /// States which match an already wrapped state after applying the given [StateMerger] will re-use the
    /// existing wrapped model.
    ///
    /// @param block       The block whose models to wrap (must implement [IFramedDoubleBlock])
    /// @param stateMerger The [StateMerger] to use for merging visually redundant states during wrapping
    public static void wrapDouble(Holder<Block> block, StateMerger stateMerger) {
        InternalClientAPI.INSTANCE.registerDoubleModelWrapper(block, stateMerger);
    }

    /// Wrap the models of all states of the given block with models created by the given [ModelFactory].
    ///
    /// States which match an already wrapped state after resetting the given ignored properties to default values
    /// will re-use the existing wrapped model.
    ///
    /// @param block        The block whose models to wrap
    /// @param modelFactory The [ModelFactory] to generate the wrapping models with
    /// @param ignoredProps The state properties to ignore during wrapping
    public static void wrapSpecial(Holder<Block> block, ModelFactory modelFactory, Set<Property<?>> ignoredProps) {
        wrapSpecial(block, modelFactory, StateMerger.ignoring(ignoredProps));
    }

    /// Wrap the models of all states of the given block with models created by the given [ModelFactory].
    ///
    /// States which match an already wrapped state after applying the given [StateMerger] will re-use the
    /// existing wrapped model.
    ///
    /// @param block        The block whose models to wrap
    /// @param modelFactory The [ModelFactory] to generate the wrapping models with
    /// @param stateMerger  The [StateMerger] to use for merging visually redundant states during wrapping
    public static void wrapSpecial(Holder<Block> block, ModelFactory modelFactory, StateMerger stateMerger) {
        InternalClientAPI.INSTANCE.registerSpecialModelWrapper(block, modelFactory, stateMerger);
    }

    /// Re-use the wrapped models from the given source block for the given block.
    ///
    /// States which match an already handled state after resetting the given ignored properties on the target block's
    /// state to default values will re-use the previously retrieved model.
    ///
    /// @param block        The block whose models to replace
    /// @param srcBlock     The block whose models to re-use
    /// @param ignoredProps The state properties to ignore during copying before applying the target block's properties
    ///                     to the source block for retrieving the wrapped model
    public static void copy(Holder<Block> block, Holder<Block> srcBlock, Set<Property<?>> ignoredProps) {
        copy(block, srcBlock, StateMerger.ignoring(ignoredProps));
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
    public static void copy(Holder<Block> block, Holder<Block> srcBlock, StateMerger stateMerger) {
        InternalClientAPI.INSTANCE.registerCopyingModelWrapper(block, srcBlock, stateMerger);
    }

    /// Wrap the model of the given block's default state in a model providing no parts and apply this model to all states.
    ///
    /// @param block The block whose models to replace
    public static void wrapEmpty(Holder<Block> block) {
        InternalClientAPI.INSTANCE.registerEmptyModelWrapper(block);
    }

    /// Wrap the models loaded from the definition file of the given wrapper key with models generated from [Geometry]s created by
    /// the given [GeometryFactory].
    ///
    /// States which match an already wrapped state after resetting the given ignored properties to default values
    /// will re-use the existing wrapped model.
    ///
    /// @param wrapperKey      The wrapper key whose models to wrap
    /// @param geometryFactory The [GeometryFactory] to generate the wrapping models with
    /// @param modelFactory    The model factory to use for constructing the standalone model from the wrapped blockstate models
    /// @param ignoredProps    The state properties to ignore during wrapping
    public static <T> void wrapStandalone(
            StandaloneWrapperKey<T> wrapperKey,
            GeometryFactory geometryFactory,
            StandaloneModelFactory<T> modelFactory,
            Set<Property<?>> ignoredProps
    ) {
        wrapStandalone(wrapperKey, geometryFactory, modelFactory, StateMerger.ignoring(ignoredProps));
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
    public static <T> void wrapStandalone(
            StandaloneWrapperKey<T> wrapperKey,
            GeometryFactory geometryFactory,
            StandaloneModelFactory<T> modelFactory,
            StateMerger stateMerger
    ) {
        InternalClientAPI.INSTANCE.registerStandaloneModelWrapper(wrapperKey, geometryFactory, modelFactory, stateMerger);
    }

    /// Replace the block model used for rendering the given block in dynamic contexts (i.e. BERs, Jade tooltip, etc.).
    ///
    /// @param block        The block to replace the model for
    /// @param modelFactory The factory to create the models with
    public static void overrideBlockModelFactory(Holder<Block> block, Function<BlockState, BlockModel.Unbaked> modelFactory) {
        InternalClientAPI.INSTANCE.overrideBlockModelFactory(block, modelFactory);
    }

    private WrapHelper() { }
}
