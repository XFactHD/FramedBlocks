package xfacthd.framedblocks.client.modelwrapping;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.model.wrapping.*;
import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.model.FramedBlockItemModel;

import java.util.*;

public final class ModelWrappingHandler
{
    private final Map<BlockState, BakedModel> visitedStates = new IdentityHashMap<>();
    private final Holder<Block> block;
    private final ModelFactory blockModelFactory;
    private final StateMerger stateMerger;
    @Nullable
    private BlockState itemModelSource;

    public ModelWrappingHandler(Holder<Block> block, ModelFactory blockModelFactory, StateMerger stateMerger)
    {
        this.block = block;
        this.blockModelFactory = blockModelFactory;
        this.stateMerger = stateMerger;
        updateItemModelSource();
    }

    public synchronized BakedModel wrapBlockModel(
            BakedModel srcModel, BlockState state, ModelLookup modelLookup, TextureLookup textureLookup, @Nullable ModelCounter counter
    )
    {
        BlockState mergedState = stateMerger.apply(state);
        if (counter != null)
        {
            counter.increment(mergedState == state);
        }
        return visitedStates.computeIfAbsent(mergedState, keyState ->
                blockModelFactory.create(new GeometryFactory.Context(keyState, srcModel, modelLookup, textureLookup))
        );
    }

    public synchronized BakedModel replaceItemModel(BakedModel originalModel, ModelLookup modelLookup, TextureLookup textureLookup, @Nullable ModelCounter counter)
    {
        if (itemModelSource == null)
        {
            return new FramedBlockItemModel(originalModel);
        }

        BakedModel model = visitedStates.get(itemModelSource);
        if (model == null)
        {
            BakedModel srcModel = modelLookup.get(StateLocationCache.getLocationFromState(itemModelSource, null));
            model = wrapBlockModel(srcModel, itemModelSource, modelLookup, textureLookup, null);
        }
        if (counter != null)
        {
            counter.incrementItem();
        }
        return new FramedBlockItemModel(model);
    }

    private void updateItemModelSource()
    {
        BlockState itemSource = null;
        if (block.value() instanceof IFramedBlock framedBlock)
        {
            itemSource = framedBlock.getItemModelSource();
            if (itemSource != null && !itemSource.is(block))
            {
                throw new IllegalArgumentException(
                        "Item model source '" + itemSource + "' is invalid for block '" + block.value() + "'"
                );
            }
        }
        itemModelSource = itemSource;
    }

    public Block getBlock()
    {
        return block.value();
    }

    public synchronized void reset()
    {
        visitedStates.clear();
        blockModelFactory.reset();
    }

    public boolean handlesItemModel()
    {
        return itemModelSource != null;
    }

    public StateMerger getStateMerger()
    {
        return stateMerger;
    }

    public int getVisitedStateCount()
    {
        return visitedStates.size();
    }
}
