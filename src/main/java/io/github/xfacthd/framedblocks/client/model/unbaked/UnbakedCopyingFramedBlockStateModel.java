package io.github.xfacthd.framedblocks.client.model.unbaked;

import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.AbstractUnbakedFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import io.github.xfacthd.framedblocks.client.model.baked.CopyingFramedBlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class UnbakedCopyingFramedBlockStateModel extends AbstractUnbakedFramedBlockStateModel {
    private final Block srcBlock;

    public UnbakedCopyingFramedBlockStateModel(ModelFactory.Context ctx, Block srcBlock) {
        super(ctx);
        this.srcBlock = srcBlock;
    }

    @Override
    protected AbstractFramedBlockStateModel bakeCached(GeometryFactory.Context ctx, ModelBaker baker) {
        BlockState srcState = srcBlock.withPropertiesOf(ctx.state());
        return new CopyingFramedBlockStateModel(ctx.baseModel(), srcState);
    }
}
