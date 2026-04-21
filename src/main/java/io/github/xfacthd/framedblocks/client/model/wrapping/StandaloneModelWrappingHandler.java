package io.github.xfacthd.framedblocks.client.model.wrapping;

import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneModelFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

public final class StandaloneModelWrappingHandler<T> extends ModelWrappingHandler {
    private final StandaloneModelFactory<T> modelFactory;

    public StandaloneModelWrappingHandler(
            Holder<Block> block,
            ModelFactory blockModelFactory,
            StateMerger stateMerger,
            StandaloneModelFactory<T> modelFactory
    ) {
        super(block, blockModelFactory, stateMerger);
        this.modelFactory = modelFactory;
    }

    public StandaloneModelFactory<T> getModelFactory() {
        return modelFactory;
    }
}
