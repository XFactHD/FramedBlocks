package io.github.xfacthd.framedblocks.client.model.unbaked;

import io.github.xfacthd.framedblocks.api.model.AbstractFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.AbstractUnbakedFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import io.github.xfacthd.framedblocks.client.model.baked.FramedDoubleBlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;

public final class UnbakedFramedDoubleBlockStateModel extends AbstractUnbakedFramedBlockStateModel {
    public UnbakedFramedDoubleBlockStateModel(ModelFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected AbstractFramedBlockStateModel bakeCached(GeometryFactory.Context context, ModelBaker baker) {
        return new FramedDoubleBlockStateModel(context);
    }
}
