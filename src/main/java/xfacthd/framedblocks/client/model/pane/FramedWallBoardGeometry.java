package xfacthd.framedblocks.client.model.pane;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.ItemModelInfo;
import xfacthd.framedblocks.api.model.wrapping.itemmodel.TranslatedItemModelInfo;
import xfacthd.framedblocks.api.util.Utils;

public class FramedWallBoardGeometry extends Geometry
{
    private static final TranslatedItemModelInfo ITEM_MODEL_INFO = TranslatedItemModelInfo.handOrGui(0F, 0F, -.5F);
    private static final float DEPTH = 1F/16F;

    private final Direction dir;

    public FramedWallBoardGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir == dir.getOpposite())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.setPosition(DEPTH))
                    .export(quadMap.get(null));

        }
        else if (Utils.isY(quadDir))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), DEPTH))
                    .export(quadMap.get(quadDir));
        }
        else if (quadDir != dir)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), DEPTH))
                    .export(quadMap.get(quadDir));
        }
    }

    @Override
    public ItemModelInfo getItemModelInfo()
    {
        return ITEM_MODEL_INFO;
    }
}
