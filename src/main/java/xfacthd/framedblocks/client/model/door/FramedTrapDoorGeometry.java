package xfacthd.framedblocks.client.model.door;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

public class FramedTrapDoorGeometry extends Geometry
{
    private static final float DEPTH = 3F/16F;

    private final Direction dir;
    private final boolean top;
    private final boolean open;

    public FramedTrapDoorGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.top = ctx.state().getValue(BlockStateProperties.HALF) == Half.TOP;
        this.open = ctx.state().getValue(BlockStateProperties.OPEN);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (open)
        {
            if (quadDir == dir)
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.setPosition(DEPTH))
                        .export(quadMap.get(null));
            }
            else if (Utils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(dir, DEPTH))
                        .export(quadMap.get(quadDir));
            }
            else
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideLeftRight(dir, DEPTH))
                        .export(quadMap.get(quadDir));
            }
        }
        else
        {
            if ((top && quad.getDirection() == Direction.DOWN) || (!top && quad.getDirection() == Direction.UP))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.setPosition(DEPTH))
                        .export(quadMap.get(null));
            }
            else if (!Utils.isY(quad.getDirection()))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSideUpDown(top, DEPTH))
                        .export(quadMap.get(quadDir));
            }
        }
    }
}