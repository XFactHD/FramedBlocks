package xfacthd.framedblocks.client.model.slopeslab;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedSlopeSlabGeometry extends Geometry
{
    public static final float SLOPE_ANGLE = (float) (90D - Math.toDegrees(Math.atan(.5)));
    public static final float SLOPE_ANGLE_VERT = (float) Math.toDegrees(Math.atan(.5));
    private final Direction facing;
    private final boolean top;
    private final boolean topHalf;
    private final boolean ySlope;

    public FramedSlopeSlabGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.topHalf = ctx.state().getValue(PropertyHolder.TOP_HALF);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        boolean offset = top != topHalf;

        if (!ySlope && face == facing.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.makeVerticalSlope(!top, SLOPE_ANGLE))
                    .applyIf(Modifiers.offset(top ? Direction.DOWN : Direction.UP, .5F), offset)
                    .export(quadMap.get(null));
        }
        else if (ySlope && ((!top && face == Direction.UP) || (top && face == Direction.DOWN)))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.makeVerticalSlope(facing.getOpposite(), SLOPE_ANGLE_VERT))
                    .applyIf(Modifiers.offset(top ? Direction.UP : Direction.DOWN, .5F), !offset)
                    .export(quadMap.get(null));
        }
        else if (face == facing)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(topHalf, .5F))
                    .export(quadMap.get(face));
        }
        else if (face == facing.getClockWise() || face == facing.getCounterClockWise())
        {
            boolean rightFace = face == facing.getClockWise();
            float right = rightFace ? (offset ? .5F : 0) : (offset ? 1 : .5F);
            float left =  rightFace ? (offset ? 1 : .5F) : (offset ? .5F : 0);

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(top, right, left))
                    .applyIf(Modifiers.cutSideUpDown(!top, .5F), offset)
                    .export(quadMap.get(face));
        }
        else if ((top && !topHalf && face == Direction.UP) || (!top && topHalf && face == Direction.DOWN))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
    }

    @Override
    public void applyInHandTransformation(PoseStack poseStack, ItemDisplayContext ctx)
    {
        poseStack.translate(0, .5, 0);
    }
}
