package xfacthd.framedblocks.client.modelwrapping;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.model.wrapping.*;

import java.util.*;

public final class ModelWrappingHandler
{
    private final Map<BlockState, BakedModel> visitedStates = new IdentityHashMap<>();
    private final RegistryObject<Block> block;
    private final ModelFactory blockModelFactory;
    @Nullable
    private final BlockState itemModelSource;
    private final StateMerger stateMerger;

    public ModelWrappingHandler(
            RegistryObject<Block> block,
            ModelFactory blockModelFactory,
            @Nullable BlockState itemModelSource,
            StateMerger stateMerger
    )
    {
        this.block = block;
        this.blockModelFactory = blockModelFactory;
        this.itemModelSource = itemModelSource;
        this.stateMerger = stateMerger;
    }

    public synchronized BakedModel wrapBlockModel(
            BakedModel srcModel, BlockState state, ModelAccessor modelAccessor, @Nullable ModelCounter counter
    )
    {
        BlockState mergedState = stateMerger.apply(state);
        if (counter != null)
        {
            counter.increment(mergedState == state);
        }
        return visitedStates.computeIfAbsent(mergedState, keyState ->
                blockModelFactory.create(new GeometryFactory.Context(keyState, srcModel, modelAccessor))
        );
    }

    public synchronized BakedModel replaceItemModel(ModelAccessor modelAccessor, @Nullable ModelCounter counter)
    {
        if (itemModelSource == null)
        {
            throw new IllegalStateException(
                    "ModelWrappingHandler for block '" + block.getId() + "' does not support item model wrapping"
            );
        }

        BakedModel model = visitedStates.get(itemModelSource);
        if (model == null)
        {
            BakedModel srcModel = modelAccessor.get(StateLocationCache.getLocationFromState(itemModelSource, null));
            model = wrapBlockModel(srcModel, itemModelSource, modelAccessor, null);
        }
        if (counter != null)
        {
            counter.increment(true);
        }
        return model;
    }

    public Block getBlock()
    {
        return block.get();
    }

    public void reset()
    {
        visitedStates.clear();
    }

    public boolean handlesItemModel()
    {
        return itemModelSource != null;
    }
}
