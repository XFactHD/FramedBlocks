package io.github.xfacthd.framedblocks.api.model.wrapping;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelInfo;
import io.github.xfacthd.framedblocks.api.model.standalone.CachingModel;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneModelFactory;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Set;

@SuppressWarnings("unused")
public final class WrapHelper {
    /// Set of properties that all blocks should ignore when wrapping models
    public static final Set<Property<?>> IGNORED_PROPS = Utils.concat(BlockUtils.REQUIRED_STATE_PROPERTIES, Set.of(
            FramedProperties.SOLID,
            FramedProperties.PROPAGATES_SKYLIGHT,
            FramedProperties.GLOWING,
            BlockStateProperties.WATERLOGGED,
            FramedProperties.STATE_LOCKED
    ));
    /// Default [StateMerger] to use for all blocks which only need to ignore the above properties
    public static final StateMerger DEFAULT_MERGER = StateMerger.ignoring(IGNORED_PROPS);
    /// [StateMerger] for blocks which need to ignore the default properties and [BlockStateProperties#POWERED]
    public static final StateMerger POWERED_MERGER = StateMerger.ignoring(Utils.concat(IGNORED_PROPS, Set.of(BlockStateProperties.POWERED)));

    /**
     * Wrap the models of all states of the given block with models generated from {@link Geometry}s created by
     * the given {@link GeometryFactory}.
     * <p>
     * States which match an already wrapped state after resetting the given ignored properties to default values
     * will re-use the existing wrapped model.
     *
     * @param block                The block whose models to wrap (must implement {@link IFramedBlock})
     * @param blockGeometryFactory The {@link GeometryFactory} to generate the wrapping models with
     * @param ignoredProps         The state properties to ignore during wrapping
     */
    public static void wrap(Holder<Block> block, GeometryFactory blockGeometryFactory, Set<Property<?>> ignoredProps) {
        wrap(block, blockGeometryFactory, StateMerger.ignoring(ignoredProps));
    }

    /**
     * Wrap the models of all states of the given block with models generated from {@link Geometry}s created by
     * the given {@link GeometryFactory}.
     * <p>
     * States which match an already wrapped state after applying the given {@link StateMerger} will re-use the
     * existing wrapped model.
     *
     * @param block                The block whose models to wrap (must implement {@link IFramedBlock})
     * @param blockGeometryFactory The {@link GeometryFactory} to generate the wrapping models with
     * @param stateMerger          The {@link StateMerger} to use for merging visually redundant states during wrapping
     */
    public static void wrap(Holder<Block> block, GeometryFactory blockGeometryFactory, StateMerger stateMerger) {
        InternalClientAPI.INSTANCE.registerModelWrapper(block, blockGeometryFactory, stateMerger);
    }

    /**
     * Wrap the models of all states of the given block with double block models using the {@link DoubleBlockParts}
     * retrieved from the given block for the respective state.
     * <p>
     * States which match an already wrapped state after resetting the given ignored properties to default values
     * will re-use the existing wrapped model.
     *
     * @param block             The block whose models to wrap (must implement {@link IFramedDoubleBlock})
     * @param itemModelInfo     The {@link ItemModelInfo} to use for controlling item model geometry caching
     * @param ignoredProps      The state properties to ignore during wrapping
     */
    public static void wrapDouble(Holder<Block> block, ItemModelInfo itemModelInfo, Set<Property<?>> ignoredProps) {
        wrapDouble(block, itemModelInfo, StateMerger.ignoring(ignoredProps));
    }

    /**
     * Wrap the models of all states of the given block with double block models using the {@link DoubleBlockParts}
     * retrieved from the given block for the respective state.
     * <p>
     * States which match an already wrapped state after applying the given {@link StateMerger} will re-use the
     * existing wrapped model.
     *
     * @param block             The block whose models to wrap (must implement {@link IFramedDoubleBlock})
     * @param itemModelInfo     The {@link ItemModelInfo} to use for controlling item model geometry caching
     * @param stateMerger       The {@link StateMerger} to use for merging visually redundant states during wrapping
     */
    public static void wrapDouble(Holder<Block> block, ItemModelInfo itemModelInfo, StateMerger stateMerger) {
        InternalClientAPI.INSTANCE.registerDoubleModelWrapper(block, itemModelInfo, stateMerger);
    }

    /**
     * Wrap the models of all states of the given block with models created by the given {@link ModelFactory}.
     * <p>
     * States which match an already wrapped state after resetting the given ignored properties to default values
     * will re-use the existing wrapped model.
     *
     * @param block        The block whose models to wrap
     * @param modelFactory The {@link ModelFactory} to generate the wrapping models with
     * @param ignoredProps The state properties to ignore during wrapping
     */
    public static void wrapSpecial(Holder<Block> block, ModelFactory modelFactory, Set<Property<?>> ignoredProps) {
        wrapSpecial(block, modelFactory, StateMerger.ignoring(ignoredProps));
    }

    /**
     * Wrap the models of all states of the given block with models created by the given {@link ModelFactory}.
     * <p>
     * States which match an already wrapped state after applying the given {@link StateMerger} will re-use the
     * existing wrapped model.
     *
     * @param block        The block whose models to wrap
     * @param modelFactory The {@link ModelFactory} to generate the wrapping models with
     * @param stateMerger  The {@link StateMerger} to use for merging visually redundant states during wrapping
     */
    public static void wrapSpecial(Holder<Block> block, ModelFactory modelFactory, StateMerger stateMerger) {
        InternalClientAPI.INSTANCE.registerSpecialModelWrapper(block, modelFactory, stateMerger);
    }

    /**
     * Re-use the wrapped models from the given source block for the given block.
     * <p>
     * States which match an already handled state after resetting the given ignored properties on the target block
     * to default values will re-use the previously retrieved model.
     *
     * @param block        The block whose models to replace
     * @param srcBlock     The block whose models to re-use
     * @param ignoredProps The state properties to ignore during copying before applying the target block's properties
     *                     to the source block for retrieving the wrapped model
     */
    public static void copy(Holder<Block> block, Holder<Block> srcBlock, Set<Property<?>> ignoredProps) {
        copy(block, srcBlock, StateMerger.ignoring(ignoredProps));
    }

    /**
     * Re-use the wrapped models from the given source block for the given block.
     * <p>
     * States which match an already handled state after applying the given {@link StateMerger} will re-use the
     * previously retrieved model.
     *
     * @param block       The block whose models to replace
     * @param srcBlock    The block whose models to re-use
     * @param stateMerger The {@link StateMerger} to use for merging visually redundant during copying before applying
     *                    the target block's properties to the source block for retrieving the wrapped model
     */
    public static void copy(Holder<Block> block, Holder<Block> srcBlock, StateMerger stateMerger) {
        InternalClientAPI.INSTANCE.registerCopyingModelWrapper(block, srcBlock, stateMerger);
    }

    public static <T extends CachingModel> void wrapStandalone(
            StandaloneWrapperKey<T> wrapperKey,
            GeometryFactory blockGeometryFactory,
            StandaloneModelFactory<T> modelFactory,
            Set<Property<?>> ignoredProps
    ) {
        wrapStandalone(wrapperKey, blockGeometryFactory, modelFactory, StateMerger.ignoring(ignoredProps));
    }

    public static <T extends CachingModel> void wrapStandalone(
            StandaloneWrapperKey<T> wrapperKey,
            GeometryFactory blockGeometryFactory,
            StandaloneModelFactory<T> modelFactory,
            StateMerger stateMerger
    ) {
        InternalClientAPI.INSTANCE.registerStandaloneModelWrapper(wrapperKey, blockGeometryFactory, modelFactory, stateMerger);
    }

    private WrapHelper() { }
}
