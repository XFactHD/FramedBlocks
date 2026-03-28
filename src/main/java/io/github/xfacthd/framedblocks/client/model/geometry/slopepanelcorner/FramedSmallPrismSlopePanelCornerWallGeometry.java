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

public class FramedSmallPrismSlopePanelCornerWallGeometry extends Geometry {
    static final float PRISM_ANGLE_HOR = FramedSmallPrismSlopePanelCornerGeometry.PRISM_ANGLE_VERT;
    static final float PRISM_ANGLE_VERT = FramedSmallPrismSlopePanelCornerGeometry.PRISM_ANGLE_HOR;
    private static final Vector3f[] TILT_ORIGNS = Util.make(() -> {
        Vector3f[] origins = new Vector3f[32];
        DirUtils.forHorizontalDirections(dir -> {
            for (HorizontalRotation rot : HorizontalRotation.values()) {
                Direction rotDir = rot.withFacing(dir);
                int idx = dir.get2DDataValue() << 3 | rot.ordinal() << 1;
                Direction oppDir = dir.getOpposite();
                float x = .5F + (rotDir.getStepX() * .5F) + (oppDir.getStepX() * .5F);
                float y = .5F + (rotDir.getStepY() * .5F) + (oppDir.getStepY() * .5F);
                float z = .5F + (rotDir.getStepZ() * .5F) + (oppDir.getStepZ() * .5F);
                origins[idx] = new Vector3f(x, y, z);
                x = .5F + (rotDir.getStepX() * .25F) + (dir.getStepX() * .5F);
                y = .5F + (rotDir.getStepY() * .25F) + (dir.getStepY() * .5F);
                z = .5F + (rotDir.getStepZ() * .25F) + (dir.getStepZ() * .5F);
                origins[idx | 1] = new Vector3f(x, y, z);
            }
        });
        return origins;
    });
    private static final Vector3f[] DIR_AXIS_ROT_ORIGINS = Util.make(() -> {
        Vector3f[] origins = new Vector3f[3];
        for (Direction.Axis axis : Direction.Axis.values()) {
            origins[axis.ordinal()] = new Vector3f(
                    (float) (.5F - axis.choose(.5F, 0, 0)),
                    (float) (.5F - axis.choose(0, .5F, 0)),
                    (float) (.5F - axis.choose(0, 0, .5F))
            );
        }
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

    public FramedSmallPrismSlopePanelCornerWallGeometry(GeometryFactory.Context ctx) {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = ctx.state().getValue(PropertyHolder.ROTATION);
        this.rotDirOne = rot.withFacing(dir);
        this.rotDirTwo = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
        this.offset = ctx.state().getValue(FramedProperties.OFFSET);
        this.flipSideTris = rot == HorizontalRotation.DOWN || rot == HorizontalRotation.LEFT;
        this.flipPrismTri = rot == HorizontalRotation.LEFT || rot == (altSlope ? HorizontalRotation.DOWN : HorizontalRotation.UP);
        this.flipPrismTriOpp = rot == HorizontalRotation.RIGHT || rot == HorizontalRotation.DOWN;
        this.tiltOrigin = getTiltOrigin(dir, rot, altSlope);
        this.invAngle = invertTiltAngle(dir, rot) == altSlope;
        this.dirAxisRotOrigin = getDirAxisRotOrigin(dir);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        Direction quadDir = quad.direction();
        if (quadDir == dir) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirOne.getOpposite(), .5F))
                    .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipSideTris ? -.5F : .5F, flipSideTris ? .5F : -.5F))
                    .export(quadMap, quadDir);
        } else if (quadDir == rotDirOne) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipSideTris ? .5F : 0, flipSideTris ? 0 : .5F))
                    .export(quadMap, quadDir);
        } else if (quadDir == rotDirTwo) {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(rotDirOne.getOpposite(), flipSideTris ? 0 : .5F, flipSideTris ? .5F : 0))
                    .export(quadMap, quadDir);
        } else if (altSlope && quadDir == rotDirOne.getOpposite()) {
            makePrismSlope(quadMap, quad, this::makePrismSlopeVertical);
        } else if (!altSlope && quadDir == dir.getOpposite()) {
            makePrismSlope(quadMap, quad, this::makePrismSlopeHorizontal);
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
        modifier.apply(Modifiers.cut(rotDirTwo, flipPrismTri ? .5F : .75F, flipPrismTri ? .75F : .5F))
                .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipPrismTriOpp ? .75F : .5F, flipPrismTriOpp ? .5F : .75F))
                .apply(Modifiers.rotate(rotDirTwo.getAxis(), tiltOrigin, tiltAngle, true))
                .apply(Modifiers.rotate(dir.getAxis(), dirAxisRotOrigin, rotAngle, true))
                .export(quadMap, null);
    }

    private void makePrismSlopeVertical(QuadMapBuilder quadMap, QuadModifier modifier) {
        float tiltAngle = invAngle ? -PRISM_ANGLE_VERT : PRISM_ANGLE_VERT;
        float rotAngle = DirUtils.isPositive(dir) ? -45F : 45F;
        modifier.apply(Modifiers.cut(rotDirTwo, flipPrismTri ? .5F : .75F, flipPrismTri ? .75F : .5F))
                .apply(Modifiers.cut(rotDirTwo.getOpposite(), flipPrismTriOpp ? .75F : .5F, flipPrismTriOpp ? .5F : .75F))
                .apply(Modifiers.setPosition(.25F))
                .apply(Modifiers.rotate(rotDirTwo.getAxis(), tiltOrigin, tiltAngle, true))
                .apply(Modifiers.rotate(dir.getAxis(), dirAxisRotOrigin, rotAngle, true))
                .export(quadMap, null);
    }

    static boolean invertTiltAngle(Direction dir, HorizontalRotation rot) {
        HorizontalRotation invAngleRot = DirUtils.isPositive(dir.getClockWise()) ? rot : rot.rotate(Rotation.CLOCKWISE_90);
        return invAngleRot == HorizontalRotation.UP || invAngleRot == HorizontalRotation.LEFT;
    }

    static Vector3f getTiltOrigin(Direction dir, HorizontalRotation rot, boolean altSlope) {
        return TILT_ORIGNS[dir.get2DDataValue() << 3 | rot.ordinal() << 1 | (altSlope ? 1 : 0)];
    }

    static Vector3f getDirAxisRotOrigin(Direction dir) {
        return DIR_AXIS_ROT_ORIGINS[dir.getAxis().ordinal()];
    }
}
