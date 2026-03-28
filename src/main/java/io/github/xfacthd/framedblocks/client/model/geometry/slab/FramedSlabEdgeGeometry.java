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

public class FramedSlabEdgeGeometry extends Geometry {
    private final Direction dir;
    private final boolean top;

    public FramedSlabEdgeGeometry(GeometryFactory.Context ctx) {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) {
        Direction quadDir = quad.direction();
        if (DirUtils.isY(quadDir)) {
            boolean inset = (quadDir == Direction.DOWN) == top;

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), .5F))
                    .applyIf(Modifiers.setPosition(.5F), inset)
                    .export(quadMap, inset ? null : quadDir);
        } else {
            boolean inset = quadDir == dir.getOpposite();
            boolean side = quadDir.getAxis() != dir.getAxis();

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, .5F))
                    .applyIf(Modifiers.cut(dir.getOpposite(), .5F), side)
                    .applyIf(Modifiers.setPosition(.5F), inset)
                    .export(quadMap, inset ? null : quadDir);
        }
    }
}
