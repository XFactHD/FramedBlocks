package xfacthd.framedblocks.client.apiimpl;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.internal.InternalClientAPI;
import xfacthd.framedblocks.api.model.wrapping.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.model.FluidModel;
import xfacthd.framedblocks.client.model.FramedBlockModel;
import xfacthd.framedblocks.client.modelwrapping.*;
import xfacthd.framedblocks.client.util.ClientTaskQueue;

import java.util.List;

public final class InternalClientApiImpl implements InternalClientAPI
{
    @Override
    public void registerModelWrapper(
            Holder<Block> block,
            GeometryFactory geometryFactory,
            @Nullable BlockState itemModelSource,
            StateMerger stateMerger
    )
    {
        registerSpecialModelWrapper(
                block,
                ctx -> new FramedBlockModel(ctx, geometryFactory.create(ctx)),
                itemModelSource,
                stateMerger
        );
    }

    @Override
    public void registerSpecialModelWrapper(
            Holder<Block> block,
            ModelFactory modelFactory,
            @Nullable BlockState itemModelSource,
            StateMerger stateMerger
    )
    {
        ModelWrappingManager.register(block, new ModelWrappingHandler(
                block, modelFactory, itemModelSource, stateMerger
        ));
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void registerCopyingModelWrapper(
            Holder<Block> block,
            Holder<Block> srcBlock,
            @Nullable BlockState itemModelSource,
            @Nullable List<Property<?>> ignoredProps
    )
    {
        ModelWrappingManager.register(block, new ModelWrappingHandler(
                block,
                new ModelFactory()
                {
                    private final Lazy<ModelWrappingHandler> sourceWrapper = Lazy.of(() ->
                            ModelWrappingManager.getHandler(srcBlock.value())
                    );

                    @Override
                    public BakedModel create(GeometryFactory.Context ctx)
                    {
                        ResourceLocation baseLoc = StateLocationCache.getLocationFromState(
                                ctx.state(), Utils.getKeyOrThrow(srcBlock).location()
                        );
                        BakedModel baseModel = ctx.modelAccessor().get(baseLoc);
                        return sourceWrapper.get().wrapBlockModel(
                                baseModel, ctx.state(), ctx.modelAccessor(), null
                        );
                    }
                },
                itemModelSource,
                state ->
                {
                    BlockState sourceState = srcBlock.value().defaultBlockState();
                    for (Property prop : state.getProperties())
                    {
                        if (sourceState.hasProperty(prop))
                        {
                            sourceState = sourceState.setValue(prop, state.getValue(prop));
                        }
                        else if (ignoredProps != null && !ignoredProps.contains(prop))
                        {
                            FramedBlocks.LOGGER.warn(
                                    "Found un-ignored property {} which is invalid for source block {}!",
                                    prop, sourceState.getBlock()
                            );
                        }
                    }
                    return sourceState;
                }
        ));
    }

    @Override
    public void enqueueClientTask(long delay, Runnable task)
    {
        ClientTaskQueue.enqueueClientTask(delay, task);
    }

    @Override
    public BakedModel createFluidModel(Fluid fluid)
    {
        return FluidModel.create(fluid);
    }
}
