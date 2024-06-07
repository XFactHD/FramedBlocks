package xfacthd.framedblocks.api.model.wrapping;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.internal.InternalClientAPI;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import xfacthd.framedblocks.api.util.Utils;

import java.util.Set;

@SuppressWarnings("unused")
public final class WrapHelper
{
    /** List of properties which are always present and always need to be ignored */
    public static final Set<Property<?>> IGNORE_ALWAYS = Set.of(FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);
    /** {@link WrapHelper#IGNORE_ALWAYS} + waterlogged */
    public static final Set<Property<?>> IGNORE_WATERLOGGED = Utils.concat(Set.of(BlockStateProperties.WATERLOGGED), IGNORE_ALWAYS);
    /** {@link WrapHelper#IGNORE_ALWAYS} + waterlogged + state-lock */
    public static final Set<Property<?>> IGNORE_WATERLOGGED_LOCK = Utils.concat(Set.of(FramedProperties.STATE_LOCKED), IGNORE_WATERLOGGED);
    /** {@link WrapHelper#IGNORE_ALWAYS} + solid */
    public static final Set<Property<?>> IGNORE_SOLID = Utils.concat(Set.of(FramedProperties.SOLID), IGNORE_ALWAYS);
    /** {@link WrapHelper#IGNORE_ALWAYS} + solid + state-lock */
    public static final Set<Property<?>> IGNORE_SOLID_LOCK = Utils.concat(Set.of(FramedProperties.STATE_LOCKED), IGNORE_SOLID);
    /** {@link WrapHelper#IGNORE_ALWAYS} + solid + waterlogged */
    public static final Set<Property<?>> IGNORE_DEFAULT = Utils.concat(Set.of(BlockStateProperties.WATERLOGGED), IGNORE_SOLID);
    /** {@link WrapHelper#IGNORE_ALWAYS} + solid + waterlogged + state-lock */
    public static final Set<Property<?>> IGNORE_DEFAULT_LOCK = Utils.concat(Set.of(FramedProperties.STATE_LOCKED), IGNORE_DEFAULT);

    /**
     * Wrap the models of all states of the given block with models generated from {@link Geometry}s created by
     * the given {@link GeometryFactory}.
     * <p>
     * States which match an already wrapped state after resetting the given ignored properties to default values
     * will re-use the existing wrapped model
     *
     * @param block The block whose models to wrap
     * @param blockGeometryFactory The {@link GeometryFactory} to generate the wrapping models with
     * @param ignoredProps The state properties to ignore during wrapping
     */
    public static void wrap(Holder<Block> block, GeometryFactory blockGeometryFactory, Set<Property<?>> ignoredProps)
    {
        wrap(block, blockGeometryFactory, StateMerger.ignoring(ignoredProps));
    }

    /**
     * Wrap the models of all states of the given block with models generated from {@link Geometry}s created by
     * the given {@link GeometryFactory}.
     * <p>
     * States which match an already wrapped state after applying the given {@link StateMerger} will re-use the
     * existing wrapped model
     *
     * @param block The block whose models to wrap
     * @param blockGeometryFactory The {@link GeometryFactory} to generate the wrapping models with
     * @param stateMerger The {@link StateMerger} to use for merging visually redundant states during wrapping
     */
    public static void wrap(Holder<Block> block, GeometryFactory blockGeometryFactory, StateMerger stateMerger)
    {
        InternalClientAPI.INSTANCE.registerModelWrapper(block, blockGeometryFactory, stateMerger);
    }

    /**
     * Wrap the models of all states of the given block with models generated from {@link Geometry}s created by
     * the given {@link GeometryFactory}.
     * <p>
     * States which match an already wrapped state after resetting the given ignored properties to default values
     * will re-use the existing wrapped model
     *
     * @param block The block whose models to wrap
     * @param modelFactory The {@link ModelFactory} to generate the wrapping models with
     * @param ignoredProps The state properties to ignore during wrapping
     */
    public static void wrapSpecial(Holder<Block> block, ModelFactory modelFactory, Set<Property<?>> ignoredProps)
    {
        wrapSpecial(block, modelFactory, StateMerger.ignoring(ignoredProps));
    }

    /**
     * Wrap the models of all states of the given block with models generated from {@link Geometry}s created by
     * the given {@link GeometryFactory}.
     * <p>
     * States which match an already wrapped state after applying the given {@link StateMerger} will re-use the
     * existing wrapped model
     *
     * @param block The block whose models to wrap
     * @param modelFactory The {@link ModelFactory} to generate the wrapping models with
     * @param stateMerger The {@link StateMerger} to use for merging visually redundant states during wrapping
     */
    public static void wrapSpecial(Holder<Block> block, ModelFactory modelFactory, StateMerger stateMerger)
    {
        InternalClientAPI.INSTANCE.registerSpecialModelWrapper(block, modelFactory, stateMerger);
    }

    /**
     * Re-use the wrapped models from the given source block for the given block.
     * <p>
     * States which match an already handled state after resetting the given ignored properties on the target block
     * to default values will re-use the previously retrieved model
     *
     * @param block The block whose models to replace
     * @param srcBlock The block whose models to re-use
     * @param ignoredProps The state properties to ignore during copying before applying the target block's properties
     *                     to the source block for retrieving the wrapped model
     */
    public static void copy(Holder<Block> block, Holder<Block> srcBlock, Set<Property<?>> ignoredProps)
    {
        copy(block, srcBlock, StateMerger.ignoring(ignoredProps));
    }

    /**
     * Re-use the wrapped models from the given source block for the given block.
     * <p>
     * States which match an already handled state after applying the given {@link StateMerger} will re-use the
     * previously retrieved model
     *
     * @param block The block whose models to replace
     * @param srcBlock The block whose models to re-use
     * @param stateMerger The {@link StateMerger} to use for merging visually redundant during copying before applying
     *                    the target block's properties to the source block for retrieving the wrapped model
     */
    public static void copy(Holder<Block> block, Holder<Block> srcBlock, StateMerger stateMerger)
    {
        InternalClientAPI.INSTANCE.registerCopyingModelWrapper(block, srcBlock, stateMerger);
    }



    private WrapHelper() { }
}
