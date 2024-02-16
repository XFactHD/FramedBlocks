package xfacthd.framedblocks.api.model.wrapping;

import net.minecraft.client.resources.model.BakedModel;

public interface ModelFactory
{
    BakedModel create(GeometryFactory.Context ctx);

    default void reset() { }
}
