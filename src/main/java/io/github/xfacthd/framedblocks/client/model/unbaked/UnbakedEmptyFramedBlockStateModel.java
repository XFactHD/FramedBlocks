package io.github.xfacthd.framedblocks.client.model.unbaked;

import io.github.xfacthd.framedblocks.api.model.AbstractUnbakedFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import io.github.xfacthd.framedblocks.client.model.baked.EmptyFramedBlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;

public final class UnbakedEmptyFramedBlockStateModel extends AbstractUnbakedFramedBlockStateModel {
    public UnbakedEmptyFramedBlockStateModel(ModelFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected BlockStateModel bakeCached(GeometryFactory.Context context, ModelBaker baker) {
        return new EmptyFramedBlockStateModel(context.baseModel(), context.state());
    }
}
