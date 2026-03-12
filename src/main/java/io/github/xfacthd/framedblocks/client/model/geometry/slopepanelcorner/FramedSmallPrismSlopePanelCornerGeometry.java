package io.github.xfacthd.framedblocks.client.model.geometry.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

public class FramedSmallPrismSlopePanelCornerGeometry extends Geometry
{
    static final float PRISM_ANGLE_HOR = (float) Math.toDegrees(Math.atan(.25D));
    static final float PRISM_ANGLE_VERT = (float) Math.toDegrees(Math.atan(4D));
    static final Vector3f Y_ROT_ORIGIN = new Vector3f(.5F, 0, .5F);
    private static final Vector3f[] TILT_ORIGINS = Util.make(() ->
    {
        Vector3f[] origins = new Vector3f[16];
        DirUtils.forHorizontalDirections(dir ->
        {
            int idx = dir.get2DDataValue();
            float x = .5F + dir.getStepX() * .25F;
            float z = .5F + dir.getStepZ() * .25F;
            origins[idx] = new Vector3f(x, 0, z);
            origins[idx + 4] = new Vector3f(x, 1, z);
            x = .5F + dir.getStepX() * .5F;
            z = .5F + dir.getStepZ() * .5F;
            origins[idx + 8] = new Vector3f(x, 1, z);
            origins[idx + 12] = new Vector3f(x, 0, z);
        });
        return origins;
    });

    private final Direction dir;
    private final boolean top;
    private final Direction upDir;
    private final boolean altSlope;
    private final boolean offset;
    private final Vector3f tiltOrigin;
    private final boolean invAngle;

    public FramedSmallPrismSlopePanelCornerGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.upDir = top ? Direction.DOWN : Direction.UP;
        this.altSlope = ctx.state().getValue(FramedProperties.ALT_SLOPE);
        this.offset = ctx.state().getValue(FramedProperties.OFFSET);
        this.invAngle = DirUtils.isPositive(dir.getClockWise()) ^ top ^ altSlope;
        this.tiltOrigin = getTiltOrigin(dir, top, altSlope);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        Direction quadDir = quad.direction();
        if (quadDir == dir || quadDir == dir.getCounterClockWise())
        {
            Direction cutDir = quadDir == dir ? dir.getClockWise() : dir.getOpposite();
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(cutDir, top ? .5F : 0F, top ? 0F : .5F))
                    .export(quadMap, quadDir);
        }
        else if (!altSlope && quadDir == dir.getOpposite())
        {
            makePrismSlope(quadMap, quad, this::makePrismSlopeHorizontal);
        }
        else if (altSlope && quadDir == upDir)
        {
            makePrismSlope(quadMap, quad, this::makePrismSlopeVertical);
        }
        else if (quadDir == upDir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(dir.getClockWise(), .5F))
                    .apply(Modifiers.cut(dir.getOpposite(), .5F, -.5F))
                    .export(quadMap, quadDir);
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
        modifier.apply(Modifiers.cut(dir.getClockWise(), top ? .75F : .5F, top ? .5F : .75F))
                .apply(Modifiers.cut(dir.getCounterClockWise(), top ? .75F : .5F, top ? .5F : .75F))
                .apply(Modifiers.setPosition(.25F))
                .apply(Modifiers.rotate(dir.getClockWise().getAxis(), tiltOrigin, tiltAngle, true))
                .apply(Modifiers.rotate(Direction.Axis.Y, Y_ROT_ORIGIN, 45F, true))
                .export(quadMap, null);
    }

    private void makePrismSlopeVertical(QuadMapBuilder quadMap, QuadModifier modifier)
    {
        float tiltAngle = invAngle ? -PRISM_ANGLE_VERT : PRISM_ANGLE_VERT;
        modifier.apply(Modifiers.cut(dir.getClockWise(), .75F, .5F))
                .apply(Modifiers.cut(dir.getCounterClockWise(), .5F, .75F))
                .apply(Modifiers.rotate(dir.getClockWise().getAxis(), tiltOrigin, tiltAngle, true))
                .apply(Modifiers.rotate(Direction.Axis.Y, Y_ROT_ORIGIN, 45F, true))
                .export(quadMap, null);
    }

    static Vector3f getTiltOrigin(Direction dir, boolean top, boolean altSlope)
    {
        return TILT_ORIGINS[dir.get2DDataValue() + (top ? 4 : 0) + (altSlope ? 8 : 0)];
    }
}
