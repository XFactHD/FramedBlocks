package xfacthd.framedblocks.client.model.slopepanelcorner;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.TranslatedItemModelInfo;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.ItemModelInfo;
import xfacthd.framedblocks.client.model.slopepanel.FramedSlopePanelGeometry;

public class FramedSmallCornerSlopePanelGeometry extends Geometry
{
    private static final TranslatedItemModelInfo ITEM_MODEL_INFO = TranslatedItemModelInfo.hand(0F, .5F, -.5F);
    private static final Vector3f ORIGIN_BOTTOM = new Vector3f(.5F, 0, .5F);
    private static final Vector3f ORIGIN_TOP = new Vector3f(.5F, 1, .5F);

    private final Direction dir;
    private final boolean top;
    private final boolean ySlope;

    public FramedSmallCornerSlopePanelGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.top = ctx.state().getValue(FramedProperties.TOP);
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir == dir || quadDir == dir.getCounterClockWise())
        {
            Direction cutDir = quadDir == dir ? dir.getClockWise() : dir.getOpposite();
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, top ? .5F : 0F, top ? 0F : .5F))
                    .export(quadMap.get(quadDir));
        }
        else if (!ySlope && (quadDir == dir.getOpposite() || quadDir == dir.getClockWise()))
        {
            Direction cutDir = quadDir == dir.getOpposite() ? dir.getClockWise() : dir.getOpposite();
            float angle = top ? FramedSlopePanelGeometry.SLOPE_ANGLE : -FramedSlopePanelGeometry.SLOPE_ANGLE;
            if (quadDir == Direction.NORTH || quadDir == Direction.EAST)
            {
                angle *= -1F;
            }

            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, top ? .5F : 0F, top ? 0F : .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .apply(Modifiers.rotate(cutDir.getAxis(), top ? ORIGIN_TOP : ORIGIN_BOTTOM, angle, true))
                    .export(quadMap.get(null));
        }
        else if (ySlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), 0, .5F))
                    .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), FramedSlopePanelGeometry.SLOPE_ANGLE_VERT))
                    .export(quadMap.get(null));

            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F, 0))
                    .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), FramedSlopePanelGeometry.SLOPE_ANGLE_VERT))
                    .export(quadMap.get(null));
        }
        else if ((!top && quadDir == Direction.DOWN) || (top && quadDir == Direction.UP))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .export(quadMap.get(quadDir));
        }
    }

    @Override
    public ItemModelInfo getItemModelInfo()
    {
        return ITEM_MODEL_INFO;
    }
}
