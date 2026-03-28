package io.github.xfacthd.framedblocks.client.model.unbaked;

import io.github.xfacthd.framedblocks.api.model.AbstractUnbakedFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelInfo;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import io.github.xfacthd.framedblocks.client.model.baked.FramedDoubleBlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;

public final class UnbakedFramedDoubleBlockStateModel extends AbstractUnbakedFramedBlockStateModel {
    private final ItemModelInfo itemModelInfo;

    public UnbakedFramedDoubleBlockStateModel(ModelFactory.Context ctx, ItemModelInfo itemModelInfo) {
        super(ctx);
        this.itemModelInfo = itemModelInfo;
    }

    @Override
    protected BlockStateModel bakeCached(GeometryFactory.Context context, ModelBaker baker) {
        return new FramedDoubleBlockStateModel(context, itemModelInfo);
    }
}
