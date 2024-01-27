package xfacthd.framedblocks.api.model.wrapping;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.geometry.Geometry;

public interface GeometryFactory
{
    Geometry create(Context ctx);



    record Context(BlockState state, BakedModel baseModel, ModelAccessor modelAccessor, TextureLookup textureLookup) { }
}
