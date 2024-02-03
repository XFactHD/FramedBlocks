package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

public class FramedPressurePlateGeometry extends Geometry
{
    private final boolean pressed;

    public FramedPressurePlateGeometry(GeometryFactory.Context ctx)
    {
        this(ctx.state().getValue(BlockStateProperties.POWERED));
    }

    protected FramedPressurePlateGeometry(boolean powered)
    {
        this.pressed = powered;
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        float height = pressed ? .5F / 16F : 1F / 16F;

        if (Utils.isY(quadDir))
        {
            boolean up = quadDir == Direction.UP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(1F/16F, 1F/16F, 15F/16F, 15F/16F))
                    .applyIf(Modifiers.setPosition(height), up)
                    .export(quadMap.get(up ? null : Direction.DOWN));
        }
        else
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(1F/16F, 0F, 15F/16F, height))
                    .apply(Modifiers.setPosition(15F/16F))
                    .export(quadMap.get(null));
        }
    }
}