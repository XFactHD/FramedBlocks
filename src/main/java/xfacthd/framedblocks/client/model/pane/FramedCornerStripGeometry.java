package xfacthd.framedblocks.client.model.pane;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

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
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (type == SlopeType.HORIZONTAL)
        {
            if (Utils.isY(quadDir))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), 1F/16F))
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), 1F/16F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir.getAxis() == dir.getAxis())
            {
                boolean onFace = quadDir == dir;
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), 1F/16F))
                        .applyIf(Modifiers.setPosition(1F/16F), !onFace)
                        .export(quadMap.get(onFace ? quadDir : null));
            }
            else if (quadDir.getAxis() == dir.getClockWise().getAxis())
            {
                boolean onFace = quadDir == dir.getCounterClockWise();
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), 1F/16F))
                        .applyIf(Modifiers.setPosition(1F/16F), !onFace)
                        .export(quadMap.get(onFace ? quadDir : null));
            }
        }
        else
        {
            boolean top = type == SlopeType.TOP;
            if (quadDir.getAxis() == dir.getClockWise().getAxis())
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), 1F/16F))
                        .apply(Modifiers.cutSideUpDown(top, 1F/16F))
                        .export(quadMap.get(quadDir));
            }
            else if (quadDir.getAxis() == dir.getAxis())
            {
                boolean onFace = quadDir == dir;
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(top, 1F/16F))
                        .applyIf(Modifiers.setPosition(1F/16F), !onFace)
                        .export(quadMap.get(onFace ? quadDir : null));
            }
            else if (Utils.isY(quadDir))
            {
                boolean onFace = top ? quadDir == Direction.UP : quadDir == Direction.DOWN;
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), 1F/16F))
                        .applyIf(Modifiers.setPosition(1F/16F), !onFace)
                        .export(quadMap.get(onFace ? quadDir : null));
            }
        }
    }

    @Override
    public void applyInHandTransformation(PoseStack poseStack, ItemDisplayContext ctx)
    {
        if (ctx.firstPerson())
        {
            poseStack.mulPose(Quaternions.YP_90);
        }
        poseStack.translate(0, .5, 0);
    }
}
