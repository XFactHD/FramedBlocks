package xfacthd.framedblocks.client.model.door;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

public class FramedDoorGeometry implements Geometry
{
    private final Direction dir;
    private final boolean hingeRight;
    private final boolean open;

    public FramedDoorGeometry(GeometryFactory.Context ctx)
    {
        dir = ctx.state().getValue(BlockStateProperties.HORIZONTAL_FACING);
        hingeRight = ctx.state().getValue(BlockStateProperties.DOOR_HINGE) == DoorHingeSide.RIGHT;
        open = ctx.state().getValue(BlockStateProperties.OPEN);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction faceDir = dir;
        if (open) { faceDir = hingeRight ? faceDir.getCounterClockWise() : faceDir.getClockWise(); }

        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(faceDir, 3F/16F))
                    .export(quadMap.get(quadDir));
        }
        else
        {
            if (quadDir == faceDir)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.setPosition(3F/16F))
                        .export(quadMap.get(null));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(faceDir, 3F/16F))
                        .export(quadMap.get(quadDir));
            }
        }
    }
}