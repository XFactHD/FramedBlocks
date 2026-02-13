package io.github.xfacthd.framedblocks.client.model.geometry.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

public class FramedSmallInnerPrismSlopePanelCornerWallGeometry extends Geometry
{
    private static final float PRISM_ANGLE_HOR = FramedSmallPrismSlopePanelCornerWallGeometry.PRISM_ANGLE_HOR;
    private static final float PRISM_ANGLE_VERT = FramedSmallPrismSlopePanelCornerWallGeometry.PRISM_ANGLE_VERT;

    private final Direction dir;
    private final Direction rotDirOne;
    private final Direction rotDirTwo;
    private final boolean ySlope;
    private final boolean offset;
    private final boolean flipSideTris;
    private final boolean flipPrismTri;
    private final boolean flipPrismTriOpp;
    private final Vector3f tiltOrigin;
    private final boolean invAngle;
    private final Vector3f dirAxisRotOrigin;

    public FramedSmallInnerPrismSlopePanelCornerWallGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = ctx.state().getValue(PropertyHolder.ROTATION);
        this.rotDirOne = rot.withFacing(dir);
        this.rotDirTwo = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
        this.offset = ctx.state().getValue(FramedProperties.OFFSET);
        this.flipSideTris = rot == HorizontalRotation.DOWN || rot == HorizontalRotation.RIGHT;
        this.flipPrismTri = rot == HorizontalRotation.LEFT || rot == (ySlope ? HorizontalRotation.DOWN : HorizontalRotation.UP);
        this.flipPrismTriOpp = rot == HorizontalRotation.RIGHT || rot == HorizontalRotation.DOWN;
        HorizontalRotation tiltRot = rot.isVertical() ? rot.getOpposite() : rot;
        this.tiltOrigin = FramedSmallPrismSlopePanelCornerWallGeometry.getTiltOrigin(dir.getOpposite(), tiltRot, ySlope);
        this.invAngle = FramedSmallPrismSlopePanelCornerWallGeometry.invertTiltAngle(dir, rot) == ySlope;
        this.dirAxisRotOrigin = FramedSmallPrismSlopePanelCornerWallGeometry.getDirAxisRotOrigin(dir);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        Direction quadDir = quad.direction();
        if (quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirOne.getOpposite(), .5F))
                    .export(quadMap.get(quadDir));
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirOne, .5F))
                    .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipSideTris ? .5F : 1.5F, flipSideTris ? 1.5F : .5F))
                    .export(quadMap.get(quadDir));

            if (!ySlope)
            {
                makePrismSlope(quadMap, quad, this::makePrismSlopeHorizontal);
            }
        }
        else if (quadDir == rotDirOne.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipSideTris ? 1 : .5F, flipSideTris ? .5F : 1))
                    .export(quadMap.get(quadDir));

            if (ySlope)
            {
                makePrismSlope(quadMap, quad, this::makePrismSlopeVertical);
            }
        }
        else if (quadDir == rotDirTwo.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirOne.getOpposite(), flipSideTris ? 1 : .5F, flipSideTris ? .5F : 1))
                    .export(quadMap.get(quadDir));
        }
    }

    private void makePrismSlope(QuadMap quadMap, BakedQuad quad, BiConsumer<QuadMap, QuadModifier> slopeMaker)
    {
        if (offset)
        {
            QuadModifier modOne = QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirTwo, .5F))
                    .apply(Modifiers.offset(rotDirTwo, .5F));
            QuadModifier modTwo = QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirTwo.getOpposite(), .5F))
                    .apply(Modifiers.offset(rotDirTwo.getOpposite(), .5F));
            slopeMaker.accept(quadMap, modTwo);
            slopeMaker.accept(quadMap, modOne);
        }
        else
        {
            slopeMaker.accept(quadMap, QuadModifier.of(quad));
        }
    }

    private void makePrismSlopeHorizontal(QuadMap quadMap, QuadModifier modifier)
    {
        float tiltAngle = invAngle ? -PRISM_ANGLE_HOR : PRISM_ANGLE_HOR;
        float rotAngle = Utils.isPositive(dir) ? -45F : 45F;
        modifier.apply(Modifiers.cut(rotDirTwo, flipPrismTri ? .75F : .5F, flipPrismTri ? .5F : .75F))
                .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipPrismTriOpp ? .5F : .75F, flipPrismTriOpp ? .75F : .5F))
                .apply(Modifiers.setPosition(0F))
                .apply(Modifiers.rotate(rotDirTwo.getAxis(), tiltOrigin, tiltAngle, true))
                .apply(Modifiers.rotate(dir.getAxis(), dirAxisRotOrigin, rotAngle, true))
                .export(quadMap, null);
    }

    private void makePrismSlopeVertical(QuadMap quadMap, QuadModifier modifier)
    {
        float tiltAngle = invAngle ? -PRISM_ANGLE_VERT : PRISM_ANGLE_VERT;
        float rotAngle = Utils.isPositive(dir) ? -45F : 45F;
        modifier.apply(Modifiers.cut(rotDirTwo, flipPrismTri ? .75F : .5F, flipPrismTri ? .5F : .75F))
                .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipPrismTriOpp ? .5F : .75F, flipPrismTriOpp ? .75F : .5F))
                .apply(Modifiers.setPosition(.75F))
                .apply(Modifiers.rotate(rotDirTwo.getAxis(), tiltOrigin, tiltAngle, true))
                .apply(Modifiers.rotate(dir.getAxis(), dirAxisRotOrigin, rotAngle, true))
                .export(quadMap.get(null));
    }
}
