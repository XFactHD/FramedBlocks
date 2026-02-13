package io.github.xfacthd.framedblocks.client.model.geometry.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

public class FramedLargePrismSlopePanelCornerGeometry extends Geometry
{
    private static final float PRISM_ANGLE_HOR = FramedSmallPrismSlopePanelCornerGeometry.PRISM_ANGLE_HOR;
    private static final float PRISM_ANGLE_VERT = FramedSmallPrismSlopePanelCornerGeometry.PRISM_ANGLE_VERT;
    private static final Vector3f[] TILT_ORIGINS = Util.make(() ->
    {
        Vector3f[] origins = new Vector3f[8];
        Utils.forHorizontalDirections(dir ->
        {
            int idx = dir.get2DDataValue();
            float x = .5F - dir.getStepX() * .5F;
            float z = .5F - dir.getStepZ() * .5F;
            origins[idx] = new Vector3f(x, 0F, z);
            origins[idx + 4] = new Vector3f(x, 1F, z);
        });
        return origins;
    });
    private static final Vector3f[] Y_ROT_ORIGINS = Util.make(() ->
    {
        Vector3f[] origins = new Vector3f[4];
        origins[Direction.NORTH.get2DDataValue()] = new Vector3f(0, 0, 1);
        origins[Direction.SOUTH.get2DDataValue()] = new Vector3f(1, 0, 0);
        origins[Direction.WEST.get2DDataValue()] = new Vector3f(1, 0, 1);
        origins[Direction.EAST.get2DDataValue()] = new Vector3f(0, 0, 0);
        return origins;
    });

    private final Direction dir;
    private final boolean top;
    private final Direction upDir;
    private final boolean ySlope;
    private final boolean offset;
    private final Vector3f tiltOrigin;
    private final boolean invAngle;
    private final Vector3f yRotOrigin;

    public FramedLargePrismSlopePanelCornerGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.upDir = top ? Direction.DOWN : Direction.UP;
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
        this.offset = ctx.state().getValue(FramedProperties.OFFSET);
        this.tiltOrigin = getTiltOrigin(dir, top, false);
        this.invAngle = Utils.isPositive(dir.getClockWise()) ^ top ^ ySlope;
        this.yRotOrigin = getYRotOrigin(dir);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        Direction quadDir = quad.direction();
        if (quadDir == dir || quadDir == dir.getCounterClockWise())
        {
            Direction cutDir = quadDir == dir ? dir.getClockWise() : dir.getOpposite();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(cutDir, top ? 1F : .5F, top ? .5F : 1F))
                    .export(quadMap, quadDir);
        }
        else if (!ySlope && quadDir == dir.getOpposite())
        {
            makePrismSlope(quadMap, quad, this::makePrismSlopeHorizontal);
        }
        else if (quadDir == upDir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getClockWise(), .5F))
                    .apply(Modifiers.cut(dir.getOpposite(), .5F, -.5F))
                    .export(quadMap, quadDir);

            if (ySlope)
            {
                makePrismSlope(quadMap, quad, this::makePrismSlopeVertical);
            }
        }
        else if (quadDir == upDir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getOpposite(), 1F, 0F))
                    .export(quadMap, quadDir);
        }
    }

    private void makePrismSlope(QuadMap quadMap, BakedQuad quad, BiConsumer<QuadMap, QuadModifier> slopeMaker)
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

    private void makePrismSlopeHorizontal(QuadMap quadMap, QuadModifier modifier)
    {
        float tiltAngle = invAngle ? -PRISM_ANGLE_HOR : PRISM_ANGLE_HOR;
        modifier.apply(Modifiers.cut(dir.getClockWise(), top ? 1F : .75F, top ? .75F : 1F))
                .apply(Modifiers.cut(dir.getCounterClockWise(), top ? 1F : .75F, top ? .75F : 1F))
                .apply(Modifiers.rotate(dir.getClockWise().getAxis(), tiltOrigin, tiltAngle, true))
                .apply(Modifiers.rotate(Direction.Axis.Y, yRotOrigin, 45F, true))
                .export(quadMap, null);
    }

    private void makePrismSlopeVertical(QuadMap quadMap, QuadModifier modifier)
    {
        float tiltAngle = invAngle ? -PRISM_ANGLE_VERT : PRISM_ANGLE_VERT;
        modifier.apply(Modifiers.cut(dir.getClockWise(), 1F, .75F))
                .apply(Modifiers.cut(dir.getCounterClockWise(), .75F, 1F))
                .apply(Modifiers.setPosition(0F))
                .apply(Modifiers.rotate(dir.getClockWise().getAxis(), tiltOrigin, tiltAngle, true))
                .apply(Modifiers.rotate(Direction.Axis.Y, yRotOrigin, 45F, true))
                .export(quadMap, null);
    }

    static Vector3f getTiltOrigin(Direction dir, boolean top, boolean ySlope)
    {
        return TILT_ORIGINS[dir.get2DDataValue() + (top ^ ySlope ? 4 : 0)];
    }

    static Vector3f getYRotOrigin(Direction dir)
    {
        return Y_ROT_ORIGINS[dir.get2DDataValue()];
    }
}
