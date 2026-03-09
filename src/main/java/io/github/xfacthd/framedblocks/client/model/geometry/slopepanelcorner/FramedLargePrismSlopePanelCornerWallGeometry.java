package io.github.xfacthd.framedblocks.client.model.geometry.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Rotation;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

public class FramedLargePrismSlopePanelCornerWallGeometry extends Geometry
{
    private static final float PRISM_ANGLE_HOR = FramedSmallPrismSlopePanelCornerWallGeometry.PRISM_ANGLE_HOR;
    private static final float PRISM_ANGLE_VERT = FramedSmallPrismSlopePanelCornerWallGeometry.PRISM_ANGLE_VERT;
    private static final Vector3f[] ROT_TILT_ORIGINS = Util.make(() ->
    {
        Vector3f[] origins = new Vector3f[16];
        DirUtils.forHorizontalDirections(dir ->
        {
            for (HorizontalRotation rot : HorizontalRotation.values())
            {
                Direction rotDirOne = rot.withFacing(dir).getOpposite();
                Direction rotDirTwo = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
                int x = Math.max(0, dir.getStepX()) + Math.max(0, rotDirOne.getStepX()) + Math.max(0, rotDirTwo.getStepX());
                int y = Math.max(0, rotDirOne.getStepY()) + Math.max(0, rotDirTwo.getStepY());
                int z = Math.max(0, dir.getStepZ()) + Math.max(0, rotDirOne.getStepZ()) + Math.max(0, rotDirTwo.getStepZ());
                origins[dir.get2DDataValue() << 2 | rot.ordinal()] = new Vector3f(x, y, z);
            }
        });
        return origins;
    });

    private final Direction dir;
    private final Direction rotDirOne;
    private final Direction rotDirTwo;
    private final boolean altSlope;
    private final boolean offset;
    private final boolean flipSideTris;
    private final boolean flipPrismTri;
    private final boolean flipPrismTriOpp;
    private final Vector3f rotTiltOrigin;
    private final boolean invAngle;

    public FramedLargePrismSlopePanelCornerWallGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = ctx.state().getValue(PropertyHolder.ROTATION);
        this.rotDirOne = rot.withFacing(dir);
        this.rotDirTwo = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
        this.offset = ctx.state().getValue(FramedProperties.OFFSET);
        this.flipSideTris = rot == HorizontalRotation.DOWN || rot == HorizontalRotation.LEFT;
        this.flipPrismTri = rot == (altSlope ? HorizontalRotation.UP : HorizontalRotation.DOWN) || rot == HorizontalRotation.RIGHT;
        this.flipPrismTriOpp = rot == HorizontalRotation.DOWN || rot == HorizontalRotation.RIGHT;
        this.rotTiltOrigin = getRotTiltOrigin(dir, rot, altSlope);
        this.invAngle = FramedSmallPrismSlopePanelCornerWallGeometry.invertTiltAngle(dir, rot) == altSlope;
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        Direction quadDir = quad.direction();
        if (quadDir == dir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipSideTris ? 0 : 1, flipSideTris ? 1 : 0))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirTwo.getOpposite(), .5F))
                    .apply(Modifiers.cut(rotDirOne.getOpposite(), flipSideTris ? -.5F : .5F, flipSideTris ? .5F : -.5F))
                    .export(quadMap.get(quadDir));

            if (!altSlope)
            {
                makePrismSlope(quadMap, quad, this::makePrismSlopeHorizontal);
            }
        }
        else if (quadDir == rotDirOne)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipSideTris ? 1 : .5F, flipSideTris ? .5F : 1))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir == rotDirTwo)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirOne.getOpposite(), flipSideTris ? .5F : 1, flipSideTris ? 1 : .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (altSlope && quadDir == rotDirOne.getOpposite())
        {
            makePrismSlope(quadMap, quad, this::makePrismSlopeVertical);
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
        float rotAngle = DirUtils.isPositive(dir) ? -45F : 45F;
        modifier.apply(Modifiers.cut(rotDirTwo, flipPrismTri ? 1 : .75F, flipPrismTri ? .75F : 1))
                .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipPrismTriOpp ? 1 : .75F, flipPrismTriOpp ? .75F : 1))
                .apply(Modifiers.setPosition(0F))
                .apply(Modifiers.rotate(rotDirTwo.getAxis(), rotTiltOrigin, tiltAngle, true))
                .apply(Modifiers.rotate(dir.getAxis(), rotTiltOrigin, rotAngle, true))
                .export(quadMap, null);
    }

    private void makePrismSlopeVertical(QuadMap quadMap, QuadModifier modifier)
    {
        float tiltAngle = invAngle ? -PRISM_ANGLE_VERT : PRISM_ANGLE_VERT;
        float rotAngle = DirUtils.isPositive(dir) ? -45F : 45F;
        modifier.apply(Modifiers.cut(rotDirTwo, flipPrismTri ? 1 : .75F, flipPrismTri ? .75F : 1))
                .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipPrismTriOpp ? 1 : .75F, flipPrismTriOpp ? .75F : 1))
                .apply(Modifiers.rotate(rotDirTwo.getAxis(), rotTiltOrigin, tiltAngle, true))
                .apply(Modifiers.rotate(dir.getAxis(), rotTiltOrigin, rotAngle, true))
                .export(quadMap.get(null));
    }

    static Vector3f getRotTiltOrigin(Direction dir, HorizontalRotation rot, boolean altSlope)
    {
        if (altSlope)
        {
            dir = dir.getOpposite();
            if (!rot.isVertical())
            {
                rot = rot.getOpposite();
            }
        }
        return ROT_TILT_ORIGINS[dir.get2DDataValue() << 2 | rot.ordinal()];
    }
}
