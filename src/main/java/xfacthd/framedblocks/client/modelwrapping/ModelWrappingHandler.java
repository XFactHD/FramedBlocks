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

import java.util.*;

public final class ModelWrappingHandler
{
    private final Map<BlockState, BakedModel> visitedStates = new IdentityHashMap<>();
    private final Holder<Block> block;
    private final ModelFactory blockModelFactory;
    @Nullable
    private final BlockState itemModelSource;
    private final StateMerger stateMerger;

    public ModelWrappingHandler(Holder<Block> block, ModelFactory blockModelFactory, StateMerger stateMerger)
    {
        this.block = block;
        this.blockModelFactory = blockModelFactory;
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
        this.itemModelSource = itemSource;
        this.stateMerger = stateMerger;
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

    public synchronized BakedModel replaceItemModel(ModelLookup modelLookup, TextureLookup textureLookup, @Nullable ModelCounter counter)
    {
        if (itemModelSource == null)
        {
            ResourceLocation key = Utils.getKeyOrThrow(block).location();
            throw new IllegalStateException(
                    "ModelWrappingHandler for block '" + key + "' does not support item model wrapping"
            );
        }

        BakedModel model = visitedStates.get(itemModelSource);
        if (model == null)
        {
            BakedModel srcModel = modelLookup.get(StateLocationCache.getLocationFromState(itemModelSource, null));
            model = wrapBlockModel(srcModel, itemModelSource, modelLookup, textureLookup, null);
        }
        if (counter != null)
        {
            counter.increment(true);
        }
        return model;
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
}
