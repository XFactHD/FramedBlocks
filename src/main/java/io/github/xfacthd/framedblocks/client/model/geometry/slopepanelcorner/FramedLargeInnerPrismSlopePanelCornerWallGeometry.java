package io.github.xfacthd.framedblocks.client.model.geometry.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Rotation;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

public class FramedLargeInnerPrismSlopePanelCornerWallGeometry extends Geometry {
    private static final float PRISM_ANGLE_HOR = FramedSmallPrismSlopePanelCornerWallGeometry.PRISM_ANGLE_HOR;
    private static final float PRISM_ANGLE_VERT = FramedSmallPrismSlopePanelCornerWallGeometry.PRISM_ANGLE_VERT;
    private static final Vector3f[] ROT_ORIGINS = Util.make(() -> {
        Vector3f[] origins = new Vector3f[32];
        DirUtils.forHorizontalDirections(dir -> {
            for (HorizontalRotation rot : HorizontalRotation.values()) {
                int idx = dir.get2DDataValue() << 3 | rot.ordinal() << 1;

                Direction rotDirOne = rot.withFacing(dir).getOpposite();
                Direction rotDirTwo = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
                float x = Math.max(0, rotDirOne.getStepX()) + .5F * Math.abs(rotDirTwo.getStepX());
                float y = Math.max(0, rotDirOne.getStepY()) + .5F * Math.abs(rotDirTwo.getStepY());
                float z = Math.max(0, rotDirOne.getStepZ()) + .5F * Math.abs(rotDirTwo.getStepZ());
                origins[idx] = new Vector3f(x, y, z);

                origins[idx | 1] = FramedLargePrismSlopePanelCornerWallGeometry.getRotTiltOrigin(dir, rot, false);
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
    private final Vector3f tiltOrigin;
    private final boolean invAngle;
    private final Vector3f dirAxisRotOrigin;

    public FramedLargeInnerPrismSlopePanelCornerWallGeometry(GeometryFactory.Context ctx) {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = ctx.state().getValue(PropertyHolder.ROTATION);
        this.rotDirOne = rot.withFacing(dir);
        this.rotDirTwo = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
        this.offset = ctx.state().getValue(PropertyHolder.OFFSET);
        this.flipSideTris = rot == HorizontalRotation.DOWN || rot == HorizontalRotation.RIGHT;
        this.flipPrismTri = rot == (altSlope ? HorizontalRotation.UP : HorizontalRotation.DOWN) || rot == HorizontalRotation.RIGHT;
        this.flipPrismTriOpp = rot == HorizontalRotation.DOWN || rot == HorizontalRotation.RIGHT;
        HorizontalRotation tiltRot = rot.isVertical() ? rot : rot.getOpposite();
        this.tiltOrigin = FramedLargePrismSlopePanelCornerWallGeometry.getRotTiltOrigin(dir.getOpposite(), tiltRot, !altSlope);
        this.invAngle = FramedSmallPrismSlopePanelCornerWallGeometry.invertTiltAngle(dir, rot) == altSlope;
        this.dirAxisRotOrigin = getRotOrigin(dir, rot, altSlope);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        Direction quadDir = quad.direction();
        if (quadDir == dir) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirTwo.getOpposite(), .5F))
                    .export(quadMap, quadDir);
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirTwo, .5F))
                    .apply(Modifiers.cut(rotDirOne.getOpposite(), flipSideTris ? 1.5F : .5F, flipSideTris ? .5F : 1.5F))
                    .export(quadMap, quadDir);
        } else if (quadDir == dir.getOpposite()) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipSideTris ? 0 : 1, flipSideTris ? 1 : 0))
                    .export(quadMap, quadDir);

            if (!altSlope) {
                makePrismSlope(quadMap, quad, this::makePrismSlopeHorizontal);
            }
        } else if (quadDir == rotDirOne.getOpposite()) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipSideTris ? .5F : 0, flipSideTris ? 0 : .5F))
                    .export(quadMap, quadDir);

            if (altSlope) {
                makePrismSlope(quadMap, quad, this::makePrismSlopeVertical);
            }
        } else if (quadDir == rotDirTwo.getOpposite()) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirOne.getOpposite(), flipSideTris ? .5F : 0, flipSideTris ? 0 : .5F))
                    .export(quadMap, quadDir);
        }
    }

    private void makePrismSlope(QuadMapBuilder quadMap, BakedQuad quad, BiConsumer<QuadMapBuilder, QuadModifier> slopeMaker) {
        if (offset) {
            QuadModifier modOne = QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirTwo, .5F))
                    .apply(Modifiers.offset(rotDirTwo, .5F));
            QuadModifier modTwo = QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirTwo.getOpposite(), .5F))
                    .apply(Modifiers.offset(rotDirTwo.getOpposite(), .5F));
            slopeMaker.accept(quadMap, modTwo);
            slopeMaker.accept(quadMap, modOne);
        } else {
            slopeMaker.accept(quadMap, QuadModifier.of(quad));
        }
    }

    private void makePrismSlopeHorizontal(QuadMapBuilder quadMap, QuadModifier modifier) {
        float tiltAngle = invAngle ? -PRISM_ANGLE_HOR : PRISM_ANGLE_HOR;
        float rotAngle = DirUtils.isPositive(dir) ? -45F : 45F;
        modifier.apply(Modifiers.cut(rotDirTwo, flipPrismTri ? .75F : 1, flipPrismTri ? 1 : .75F))
                .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipPrismTriOpp ? .75F : 1, flipPrismTriOpp ? 1 : .75F))
                .apply(Modifiers.setPosition(0F))
                .apply(Modifiers.rotate(rotDirTwo.getAxis(), tiltOrigin, tiltAngle, true))
                .apply(Modifiers.offset(rotDirTwo.getOpposite(), .25F))
                .apply(Modifiers.rotate(dir.getAxis(), dirAxisRotOrigin, rotAngle, true))
                .export(quadMap, null);
    }

    private void makePrismSlopeVertical(QuadMapBuilder quadMap, QuadModifier modifier) {
        float tiltAngle = invAngle ? -PRISM_ANGLE_VERT : PRISM_ANGLE_VERT;
        float rotAngle = DirUtils.isPositive(dir) ? -45F : 45F;
        modifier.apply(Modifiers.cut(rotDirTwo, flipPrismTri ? .75F : 1, flipPrismTri ? 1 : .75F))
                .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipPrismTriOpp ? .75F : 1, flipPrismTriOpp ? 1 : .75F))
                .apply(Modifiers.rotate(rotDirTwo.getAxis(), tiltOrigin, tiltAngle, true))
                .apply(Modifiers.rotate(dir.getAxis(), dirAxisRotOrigin, rotAngle, true))
                .export(quadMap, null);
    }

    private static Vector3f getRotOrigin(Direction dir, HorizontalRotation rot, boolean altSlope) {
        return ROT_ORIGINS[dir.get2DDataValue() << 3 | rot.ordinal() << 1 | (altSlope ? 1 : 0)];
    }
}
