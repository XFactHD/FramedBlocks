package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

public class FramedMiniCubeGeometry implements Geometry
{
    private static final Vector3f ORIGIN = new Vector3f(.5F, 0, .5F);

    private final float rotAngle;

    public FramedMiniCubeGeometry(GeometryFactory.Context ctx)
    {
        int rot = ctx.state().getValue(BlockStateProperties.ROTATION_16);
        this.rotAngle = (4 - (rot % 4)) * 22.5F;
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();


        QuadModifier.geometry(quad)
                .apply(Modifiers.scaleFace(.5F, ORIGIN))
                .applyIf(Modifiers.setPosition(.5F), quadDir == Direction.UP)
                .applyIf(Modifiers.setPosition(.75F), !Utils.isY(quadDir))
                .apply(Modifiers.rotate(Direction.Axis.Y, ORIGIN, rotAngle, false))
                .export(quadMap.get(quadDir == Direction.DOWN ? quadDir : null));
    }
}
