package xfacthd.framedblocks.api.model.wrapping;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.internal.InternalClientAPI;
import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import xfacthd.framedblocks.api.util.Utils;

import java.util.Set;

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
    /** {@link WrapHelper#IGNORE_ALWAYS} + solid + waterlogged */
    public static final Set<Property<?>> IGNORE_DEFAULT = Utils.concat(Set.of(BlockStateProperties.WATERLOGGED), IGNORE_SOLID);
    /** {@link WrapHelper#IGNORE_ALWAYS} + solid + waterlogged + state-lock */
    public static final Set<Property<?>> IGNORE_DEFAULT_LOCK = Utils.concat(Set.of(FramedProperties.STATE_LOCKED), IGNORE_DEFAULT);

    public static void wrap(
            Holder<Block> block,
            GeometryFactory blockGeometryFactory,
            Set<Property<?>> ignoredProps
    )
    {
        wrap(block, blockGeometryFactory, null, ignoredProps);
    }

    public static void wrap(
            Holder<Block> block,
            GeometryFactory blockGeometryFactory,
            @Nullable BlockState itemModelSource,
            Set<Property<?>> ignoredProps
    )
    {
        wrap(block, blockGeometryFactory, itemModelSource, StateMerger.ignoring(ignoredProps));
    }

    public static void wrap(
            Holder<Block> block,
            GeometryFactory blockGeometryFactory,
            StateMerger stateMerger
    )
    {
        wrap(block, blockGeometryFactory, null, stateMerger);
    }

    public static void wrap(
            Holder<Block> block,
            GeometryFactory blockGeometryFactory,
            @Nullable BlockState itemModelSource,
            StateMerger stateMerger
    )
    {
        InternalClientAPI.INSTANCE.registerModelWrapper(block, blockGeometryFactory, itemModelSource, stateMerger);
    }



    public static void wrapSpecial(
            Holder<Block> block,
            ModelFactory modelFactory,
            @Nullable BlockState itemModelSource,
            StateMerger stateMerger
    )
    {
        InternalClientAPI.INSTANCE.registerSpecialModelWrapper(block, modelFactory, itemModelSource, stateMerger);
    }



    public static void copy(
            Holder<Block> block,
            Holder<Block> srcBlock,
            Set<Property<?>> ignoredProps
    )
    {
        copy(block, srcBlock, null, ignoredProps);
    }

    public static void copy(
            Holder<Block> block,
            Holder<Block> srcBlock,
            @Nullable BlockState itemModelSource,
            Set<Property<?>> ignoredProps
    )
    {
        copy(block, srcBlock, itemModelSource, StateMerger.ignoring(ignoredProps));
    }

    public static void copy(
            Holder<Block> block,
            Holder<Block> srcBlock,
            StateMerger stateMerger
    )
    {
        copy(block, srcBlock, null, stateMerger);
    }

    public static void copy(
            Holder<Block> block,
            Holder<Block> srcBlock,
            @Nullable BlockState itemModelSource,
            StateMerger stateMerger
    )
    {
        InternalClientAPI.INSTANCE.registerCopyingModelWrapper(block, srcBlock, itemModelSource, stateMerger);
    }



    private WrapHelper() { }
}
