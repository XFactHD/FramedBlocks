package io.github.xfacthd.framedblocks.client.model.geometry.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedCenteredPanelGeometry extends Geometry {
    private final Direction dir;

    public FramedCenteredPanelGeometry(GeometryFactory.Context ctx) {
        this.dir = ctx.state().getValue(FramedProperties.FACING_NE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction quadDir = quad.direction();
        if (quadDir.getAxis() == dir.getAxis()) {
            QuadModifier.of(quad)
                    .apply(Modifiers.setPosition(12F/16F))
                    .export(quadMap, null);
        } else if (DirUtils.isY(quadDir)) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getAxis(), 12F/16F))
                    .export(quadMap, quadDir);
        } else {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 12F/16F))
                    .export(quadMap, quadDir);
        }
    }
}
