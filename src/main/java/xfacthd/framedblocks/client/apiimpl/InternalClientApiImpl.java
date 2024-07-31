package xfacthd.framedblocks.client.apiimpl;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.internal.InternalClientAPI;
import xfacthd.framedblocks.api.model.wrapping.*;
import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import xfacthd.framedblocks.api.render.debug.BlockDebugRenderer;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.model.FramedBlockModel;
import xfacthd.framedblocks.client.modelwrapping.*;
import xfacthd.framedblocks.client.render.block.debug.ConnectionPredicateDebugRenderer;
import xfacthd.framedblocks.client.render.block.debug.QuadWindingDebugRenderer;
import xfacthd.framedblocks.client.util.ClientTaskQueue;
import xfacthd.framedblocks.common.config.DevToolsConfig;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class InternalClientApiImpl implements InternalClientAPI
{
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

    @Override
    public BlockDebugRenderer<FramedBlockEntity> getQuadWindingDebugRenderer()
    {
        return QuadWindingDebugRenderer.INSTANCE;
    }

    @Override
    public void enqueueClientTask(int delay, Runnable task)
    {
        ClientTaskQueue.enqueueClientTask(delay, task);
    }



    private static void debugStateMerger(Holder<Block> block, StateMerger stateMerger)
    {
        if (!DevToolsConfig.VIEW.isStateMergerDebugLoggingEnabled()) return;

        Pattern debugFilterPattern = DevToolsConfig.VIEW.getStateMergerDebugFilter();
        if (debugFilterPattern != null)
        {
            String key = Utils.getKeyOrThrow(block).location().toString();
            if (!debugFilterPattern.matcher(key).matches()) return;
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
