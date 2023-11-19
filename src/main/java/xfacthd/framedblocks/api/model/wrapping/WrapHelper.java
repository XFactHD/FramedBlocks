package xfacthd.framedblocks.api.model.wrapping;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.internal.InternalClientAPI;
import xfacthd.framedblocks.api.util.Utils;

import java.util.ArrayList;
import java.util.List;

public final class WrapHelper
{
    private static final Logger LOGGER = LogUtils.getLogger();
    /** List of properties which are always present and always need to be ignored */
    public static final List<Property<?>> IGNORE_ALWAYS = List.of(FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);
    /** {@link WrapHelper#IGNORE_ALWAYS} + waterlogged */
    public static final List<Property<?>> IGNORE_WATERLOGGED = Utils.concat(List.of(BlockStateProperties.WATERLOGGED), IGNORE_ALWAYS);
    /** {@link WrapHelper#IGNORE_ALWAYS} + waterlogged + state-lock */
    public static final List<Property<?>> IGNORE_WATERLOGGED_LOCK = Utils.concat(List.of(FramedProperties.STATE_LOCKED), IGNORE_WATERLOGGED);
    /** {@link WrapHelper#IGNORE_ALWAYS} + solid */
    public static final List<Property<?>> IGNORE_SOLID = Utils.concat(List.of(FramedProperties.SOLID), IGNORE_ALWAYS);
    /** {@link WrapHelper#IGNORE_ALWAYS} + solid + waterlogged */
    public static final List<Property<?>> IGNORE_DEFAULT = Utils.concat(List.of(BlockStateProperties.WATERLOGGED), IGNORE_SOLID);
    /** {@link WrapHelper#IGNORE_ALWAYS} + solid + waterlogged + state-lock */
    public static final List<Property<?>> IGNORE_DEFAULT_LOCK = Utils.concat(List.of(FramedProperties.STATE_LOCKED), IGNORE_DEFAULT);
    /** Ignore all properties -> use the same model instance for every state */
    public static final StateMerger IGNORE_ALL = state -> state.getBlock().defaultBlockState();

    public static void wrap(
            Holder<Block> block,
            GeometryFactory blockGeometryFactory,
            List<Property<?>> ignoredProps
    )
    {
        wrap(block, blockGeometryFactory, null, ignoredProps);
    }

    public static void wrap(
            Holder<Block> block,
            GeometryFactory blockGeometryFactory,
            @Nullable BlockState itemModelSource,
            List<Property<?>> ignoredProps
    )
    {
        wrap(block, blockGeometryFactory, itemModelSource, ignoreProps(ignoredProps));
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
            List<Property<?>> ignoredProps
    )
    {
        copy(block, srcBlock, null, ignoredProps);
    }

    public static void copy(
            Holder<Block> block,
            Holder<Block> srcBlock,
            @Nullable BlockState itemModelSource,
            List<Property<?>> ignoredProps
    )
    {
        InternalClientAPI.INSTANCE.registerCopyingModelWrapper(block, srcBlock, itemModelSource, ignoredProps);
    }



    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static StateMerger ignoreProps(List<Property<?>> ignoredProps)
    {
        List<Property<?>> props = new ArrayList<>(ignoredProps);
        props.add(FramedProperties.GLOWING);

        return state ->
        {
            BlockState defaultState = state.getBlock().defaultBlockState();
            for (Property prop : props)
            {
                if (!state.hasProperty(prop))
                {
                    LOGGER.warn("Found invalid ignored property {} for block {}!", prop, state.getBlock());
                    continue;
                }
                state = state.setValue(prop, defaultState.getValue(prop));
            }
            return state;
        };
    }



    private WrapHelper() { }
}
