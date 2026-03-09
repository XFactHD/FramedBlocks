package io.github.xfacthd.framedblocks.client.model.geometry.pane;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class FramedCornerStripGeometry extends Geometry
{
    private final Direction dir;
    private final SlopeType type;

    public FramedCornerStripGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(PropertyHolder.SLOPE_TYPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (type == SlopeType.HORIZONTAL)
        {
            if (DirUtils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), 1F/16F))
                        .apply(Modifiers.cut(dir.getClockWise(), 1F/16F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir.getAxis() == dir.getAxis())
            {
                boolean onFace = quadDir == dir;
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getClockWise(), 1F/16F))
                        .applyIf(Modifiers.setPosition(1F/16F), !onFace)
                        .export(quadMap.get(onFace ? quadDir : null));
            }
            else if (quadDir.getAxis() == dir.getClockWise().getAxis())
            {
                boolean onFace = quadDir == dir.getCounterClockWise();
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), 1F/16F))
                        .applyIf(Modifiers.setPosition(1F/16F), !onFace)
                        .export(quadMap.get(onFace ? quadDir : null));
            }
        }
        else
        {
            boolean top = type == SlopeType.TOP;
            if (quadDir.getAxis() == dir.getClockWise().getAxis())
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), 1F/16F))
                        .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, 1F/16F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir.getAxis() == dir.getAxis())
            {
                boolean onFace = quadDir == dir;
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(top ? Direction.DOWN : Direction.UP, 1F/16F))
                        .applyIf(Modifiers.setPosition(1F/16F), !onFace)
                        .export(quadMap.get(onFace ? quadDir : null));
            }
            else if (DirUtils.isY(quadDir))
            {
                boolean onFace = top ? quadDir == Direction.UP : quadDir == Direction.DOWN;
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir.getOpposite(), 1F/16F))
                        .applyIf(Modifiers.setPosition(1F/16F), !onFace)
                        .export(quadMap.get(onFace ? quadDir : null));
            }
        }
    }
}
