package io.github.xfacthd.framedblocks.client.model.geometry.pane;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public final class FramedHalfBoardGeometry extends Geometry {
    private final Direction face;
    private final Direction dir;

    public FramedHalfBoardGeometry(GeometryFactory.Context ctx) {
        CompoundDirection cmpDir = ctx.state().getValue(PropertyHolder.FACING_DIR);
        this.face = cmpDir.direction();
        this.dir = cmpDir.orientation();
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        Direction quadDir = quad.direction();
        if (quadDir.getAxis() == face.getAxis()) {
            boolean front = quadDir == face.getOpposite();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), .5F))
                    .applyIf(Modifiers.setPosition(1F/16F), front)
                    .export(quadMap, front ? null : quadDir);
        } else if (quadDir.getAxis() == dir.getAxis()) {
            boolean inset = quadDir == dir.getOpposite();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(face.getOpposite(), 1F/16F))
                    .applyIf(Modifiers.setPosition(.5F), inset)
                    .export(quadMap, inset ? null : quadDir);
        } else {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(face.getOpposite(), 1F/16F))
                    .apply(Modifiers.cut(dir.getOpposite(), .5F))
                    .export(quadMap, quadDir);
        }
    }
}
