package io.github.xfacthd.framedblocks.client.model.wrapping;

import io.github.xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.client.model.block.FramedBlockModel;
import net.minecraft.client.renderer.block.BuiltInBlockModels;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.RegisterBlockModelsEvent;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public sealed class ModelWrappingHandler permits StandaloneModelWrappingHandler
{
    private final Map<BlockState, BlockStateModel.UnbakedRoot> visitedStates = new IdentityHashMap<>();
    private final Holder<Block> block;
    private final ModelFactory blockModelFactory;
    private final StateMerger stateMerger;

    public ModelWrappingHandler(Holder<Block> block, ModelFactory blockModelFactory, StateMerger stateMerger)
    {
        this.block = block;
        this.blockModelFactory = blockModelFactory;
        this.stateMerger = stateMerger;
    }

    public final void wrapAllBlockStateModels(Map<BlockState, BlockStateModel.UnbakedRoot> models, Map<String, SingleVariant.Unbaked> auxModels)
    {
        visitedStates.clear();
        blockModelFactory.reset();
        models.replaceAll((state, model) -> wrapBlockStateModel(state, model, auxModels));
    }

    private BlockStateModel.UnbakedRoot wrapBlockStateModel(BlockState state, BlockStateModel.UnbakedRoot srcModel, Map<String, SingleVariant.Unbaked> auxModels)
    {
        BlockState mergedState = stateMerger.apply(state);
        return visitedStates.computeIfAbsent(mergedState, keyState ->
                blockModelFactory.create(new ModelFactory.Context(keyState, srcModel, auxModels))
        );
    }

    final void registerBlockModelFactory(RegisterBlockModelsEvent event)
    {
        Map<BlockState, FramedBlockModel.Unbaked> handled = new IdentityHashMap<>();
        BuiltInBlockModels.ModelFactory factory = (_, state) -> handled.computeIfAbsent(
                stateMerger.apply(state),
                key -> new FramedBlockModel.Unbaked(key, Optional.empty())
        );
        event.register(factory, block.value());
    }

    public final Block getBlock()
    {
        return block.value();
    }

    public final StateMerger getStateMerger()
    {
        return stateMerger;
    }

    public final int getVisitedStateCount()
    {
        return visitedStates.size();
    }
}
