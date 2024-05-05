package xfacthd.framedblocks.client.apiimpl;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.internal.InternalClientAPI;
import xfacthd.framedblocks.api.model.wrapping.*;
import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import xfacthd.framedblocks.api.render.debug.BlockDebugRenderer;
import xfacthd.framedblocks.api.util.TestProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.model.FramedBlockModel;
import xfacthd.framedblocks.client.modelwrapping.*;
import xfacthd.framedblocks.client.render.block.debug.ConnectionPredicateDebugRenderer;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class InternalClientApiImpl implements InternalClientAPI
{
    private static final Pattern DEBUG_FILTER_PATTERN = Util.make(() ->
    {
        if (TestProperties.STATE_MERGER_DEBUG_FILTER == null) return null;
        if (TestProperties.STATE_MERGER_DEBUG_FILTER.isEmpty()) return null;
        return Pattern.compile(TestProperties.STATE_MERGER_DEBUG_FILTER);
    });

    @Override
    public void registerModelWrapper(Holder<Block> block, GeometryFactory geometryFactory, StateMerger stateMerger)
    {
        registerSpecialModelWrapper(
                block,
                ctx -> new FramedBlockModel(ctx, geometryFactory.create(ctx)),
                stateMerger
        );
    }

    @Override
    public void registerSpecialModelWrapper(Holder<Block> block, ModelFactory modelFactory, StateMerger stateMerger)
    {
        debugStateMerger(block, stateMerger);

        ModelWrappingManager.register(block, new ModelWrappingHandler(block, modelFactory, stateMerger));
    }

    @Override
    public void registerCopyingModelWrapper(Holder<Block> block, Holder<Block> srcBlock, StateMerger stateMerger)
    {
        registerSpecialModelWrapper(block, new CopyingModelFactory(srcBlock), stateMerger);
    }

    @Override
    public BlockDebugRenderer<FramedBlockEntity> getConnectionDebugRenderer()
    {
        return ConnectionPredicateDebugRenderer.INSTANCE;
    }



    private static void debugStateMerger(Holder<Block> block, StateMerger stateMerger)
    {
        if (!TestProperties.ENABLE_STATE_MERGER_DEBUG_LOGGING) return;
        //noinspection ConstantConditions
        if (DEBUG_FILTER_PATTERN != null)
        {
            String key = Utils.getKeyOrThrow(block).location().toString();
            if (!DEBUG_FILTER_PATTERN.matcher(key).matches()) return;
        }

        Set<Property<?>> props = new HashSet<>(block.value().getStateDefinition().getProperties());
        Set<Property<?>> ignoredProps = stateMerger.getHandledProperties(block);

        props.removeAll(ignoredProps);

        FramedBlocks.LOGGER.info("%-70s | %-150s | %-150s".formatted(
                block.value(), propsToString(props), propsToString(ignoredProps)
        ));
    }

    private static String propsToString(Collection<Property<?>> properties)
    {
        return properties.stream()
                .map(Property::getName)
                .collect(Collectors.joining(", ", "[ ", " ]"));
    }
}
