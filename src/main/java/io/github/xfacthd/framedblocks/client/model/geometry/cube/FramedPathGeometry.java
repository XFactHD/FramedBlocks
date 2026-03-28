package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedPathGeometry extends Geometry {
    public FramedPathGeometry(@SuppressWarnings("unused") GeometryFactory.Context ctx) {}

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        Direction quadDir = quad.direction();
        if (quadDir == Direction.UP) {
            QuadModifier.of(quad)
                    .apply(Modifiers.setPosition(15F/16F))
                    .export(quadMap, null);
        } else if (quadDir != Direction.DOWN) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(Direction.UP, 15F/16F))
                    .export(quadMap, quadDir);
        }
    }
}
