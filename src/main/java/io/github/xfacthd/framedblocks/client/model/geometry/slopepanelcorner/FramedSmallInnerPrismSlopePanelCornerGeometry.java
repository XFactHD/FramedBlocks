package io.github.xfacthd.framedblocks.client.model.geometry.slopepanelcorner;

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
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

public class FramedSmallInnerPrismSlopePanelCornerGeometry extends Geometry
{
    private static final float PRISM_ANGLE_HOR = FramedSmallPrismSlopePanelCornerGeometry.PRISM_ANGLE_HOR;
    private static final float PRISM_ANGLE_VERT = FramedSmallPrismSlopePanelCornerGeometry.PRISM_ANGLE_VERT;

    private final Direction dir;
    private final boolean top;
    private final Direction upDir;
    private final boolean altSlope;
    private final boolean offset;
    private final Vector3f tiltOrigin;
    private final boolean invAngle;

    public FramedSmallInnerPrismSlopePanelCornerGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.upDir = top ? Direction.DOWN : Direction.UP;
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
        this.offset = ctx.state().getValue(FramedProperties.OFFSET);
        this.invAngle = DirUtils.isPositive(dir.getClockWise()) ^ top ^ altSlope;
        this.tiltOrigin = FramedSmallPrismSlopePanelCornerGeometry.getTiltOrigin(dir.getOpposite(), !top, altSlope);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        Direction quadDir = quad.direction();
        if (quadDir == dir.getClockWise())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), top ? 1F : .5F, top ? .5F : 1F))
                    .export(quadMap, quadDir);
        }
        else if (quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getClockWise(), top ? 1F : .5F, top ? .5F : 1F))
                    .export(quadMap, quadDir);

            if (!altSlope)
            {
                makePrismSlope(quadMap, quad, this::makePrismSlopeHorizontal);
            }
        }
        else if (quadDir == upDir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), .5F))
                    .export(quadMap, quadDir);

            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir, .5F))
                    .apply(Modifiers.cut(dir.getClockWise(), .5F, 1.5F))
                    .export(quadMap, quadDir);

            if (altSlope)
            {
                makePrismSlope(quadMap, quad, this::makePrismSlopeVertical);
            }
        }
    }

    private void makePrismSlope(QuadMapBuilder quadMap, BakedQuad quad, BiConsumer<QuadMapBuilder, QuadModifier> slopeMaker)
    {
        if (offset)
        {
            QuadModifier modOne = QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getClockWise(), .5F))
                    .apply(Modifiers.offset(dir.getClockWise(), .5F));
            QuadModifier modTwo = QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getCounterClockWise(), .5F))
                    .apply(Modifiers.offset(dir.getCounterClockWise(), .5F));
            slopeMaker.accept(quadMap, modTwo);
            slopeMaker.accept(quadMap, modOne);
        }
        else
        {
            slopeMaker.accept(quadMap, QuadModifier.of(quad));
        }
    }

    private void makePrismSlopeHorizontal(QuadMapBuilder quadMap, QuadModifier modifier)
    {
        float tiltAngle = invAngle ? -PRISM_ANGLE_HOR : PRISM_ANGLE_HOR;
        modifier.apply(Modifiers.cut(dir.getClockWise(), top ? .5F : .75F, top ? .75F : .5F))
                .apply(Modifiers.cut(dir.getCounterClockWise(), top ? .5F : .75F, top ? .75F : .5F))
                .apply(Modifiers.setPosition(.75F))
                .apply(Modifiers.rotate(dir.getClockWise().getAxis(), tiltOrigin, tiltAngle, true))
                .apply(Modifiers.rotate(Direction.Axis.Y, FramedSmallPrismSlopePanelCornerGeometry.Y_ROT_ORIGIN, 45F, true))
                .export(quadMap, null);
    }

    private void makePrismSlopeVertical(QuadMapBuilder quadMap, QuadModifier modifier)
    {
        float tiltAngle = invAngle ? -PRISM_ANGLE_VERT : PRISM_ANGLE_VERT;
        modifier.apply(Modifiers.cut(dir.getClockWise(), .5F, .75F))
                .apply(Modifiers.cut(dir.getCounterClockWise(), .75F, .5F))
                .apply(Modifiers.setPosition(0F))
                .apply(Modifiers.rotate(dir.getClockWise().getAxis(), tiltOrigin, tiltAngle, true))
                .apply(Modifiers.rotate(Direction.Axis.Y, FramedSmallPrismSlopePanelCornerGeometry.Y_ROT_ORIGIN, 45F, true))
                .export(quadMap, null);
    }
}
