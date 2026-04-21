package io.github.xfacthd.framedblocks.client.model.unbaked;

import io.github.xfacthd.framedblocks.api.model.AbstractUnbakedFramedBlockStateModel;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import io.github.xfacthd.framedblocks.client.model.ReinforcementModel;
import io.github.xfacthd.framedblocks.client.model.baked.FramedBlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;

public final class UnbakedFramedBlockStateModel extends AbstractUnbakedFramedBlockStateModel {
    private final GeometryFactory geometryFactory;
    private final boolean standaloneWithCt;

    public UnbakedFramedBlockStateModel(ModelFactory.Context ctx, GeometryFactory geometryFactory, boolean standaloneWithCt) {
        super(ctx);
        this.geometryFactory = geometryFactory;
        this.standaloneWithCt = standaloneWithCt;
    }

    @Override
    protected BlockStateModel bakeCached(GeometryFactory.Context context, ModelBaker baker) {
        ReinforcementModel reinforcement = ReinforcementModel.getOrCreate(baker);
        return new FramedBlockStateModel(context, geometryFactory.create(context), reinforcement, standaloneWithCt);
    }

    @Override
    protected void resolveSpecialDependencies(Resolver resolver) {
        resolver.markDependency(ReinforcementModel.MODEL_ID);
    }
}
