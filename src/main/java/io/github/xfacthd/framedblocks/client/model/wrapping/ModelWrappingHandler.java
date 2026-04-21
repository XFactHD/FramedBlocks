package io.github.xfacthd.framedblocks.client.model.wrapping;

import io.github.xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.client.model.block.FramedBlockModel;
import net.minecraft.client.renderer.block.BuiltInBlockModels;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.RegisterBlockModelsEvent;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public sealed class ModelWrappingHandler permits StandaloneModelWrappingHandler {
    private static final Function<BlockState, BlockModel.Unbaked> DEFAULT_BLOCK_MODEL_FACTORY =
            state -> new FramedBlockModel.Unbaked(state, Optional.empty());

    private final Map<BlockState, BlockStateModel.UnbakedRoot> visitedStates = new IdentityHashMap<>();
    private final Holder<Block> block;
    private final ModelFactory blockStateModelFactory;
    private final StateMerger stateMerger;
    private Function<BlockState, BlockModel.Unbaked> blockModelFactory = DEFAULT_BLOCK_MODEL_FACTORY;

    public ModelWrappingHandler(Holder<Block> block, ModelFactory blockStateModelFactory, StateMerger stateMerger) {
        this.block = block;
        this.blockStateModelFactory = blockStateModelFactory;
        this.stateMerger = stateMerger;
    }

    public final void wrapAllBlockStateModels(Map<BlockState, BlockStateModel.UnbakedRoot> models, Map<String, SingleVariant.Unbaked> auxModels) {
        visitedStates.clear();
        blockStateModelFactory.reset();
        models.replaceAll((state, model) -> wrapBlockStateModel(state, model, auxModels));
    }

    private BlockStateModel.UnbakedRoot wrapBlockStateModel(BlockState state, BlockStateModel.UnbakedRoot srcModel, Map<String, SingleVariant.Unbaked> auxModels) {
        BlockState mergedState = stateMerger.apply(state);
        return visitedStates.computeIfAbsent(mergedState, keyState ->
                blockStateModelFactory.create(new ModelFactory.Context(keyState, srcModel, auxModels))
        );
    }

    final void registerBlockModelFactory(RegisterBlockModelsEvent event) {
        Map<BlockState, BlockModel.Unbaked> handled = new IdentityHashMap<>();
        BuiltInBlockModels.ModelFactory factory = (_, state) -> handled.computeIfAbsent(stateMerger.apply(state), blockModelFactory);
        event.register(factory, block.value());
    }

    public final void overrideBlockModelFactory(Function<BlockState, BlockModel.Unbaked> blockModelFactory) {
        this.blockModelFactory = blockModelFactory;
    }

    public final Block getBlock() {
        return block.value();
    }

    public final StateMerger getStateMerger() {
        return stateMerger;
    }

    public final int getVisitedStateCount() {
        return visitedStates.size();
    }
}
