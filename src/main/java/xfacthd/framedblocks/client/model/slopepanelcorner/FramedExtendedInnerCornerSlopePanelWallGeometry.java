package xfacthd.framedblocks.client.model.slopepanelcorner;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.model.slopepanel.FramedSlopePanelGeometry;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedExtendedInnerCornerSlopePanelWallGeometry extends Geometry
{
    private final Direction dir;
    private final Direction horRotDir;
    private final Direction vertRotDir;
    private final boolean ySlope;

    public FramedExtendedInnerCornerSlopePanelWallGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = ctx.state().getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(dir);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        this.horRotDir = Utils.isY(rotDir) ? perpRotDir : rotDir;
        this.vertRotDir = Utils.isY(rotDir) ? rotDir : perpRotDir;
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        boolean cw = horRotDir == dir.getClockWise();
        boolean up = vertRotDir == Direction.UP;
        if (quadDir == horRotDir)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(!up, cw ? .5F : 1F, cw ? 1F : .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(up, cw ? .5F : 0F, cw ? 0F : .5F))
                    .apply(Modifiers.makeHorizontalSlope(cw, FramedSlopePanelGeometry.SLOPE_ANGLE))
                    .export(quadMap.get(null));
        }
        else if (quadDir == dir.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(vertRotDir == Direction.DOWN, .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(vertRotDir == Direction.UP, .5F))
                    .apply(Modifiers.cutSideLeftRight(horRotDir, .5F))
                    .export(quadMap.get(quadDir));

            if (!ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(horRotDir.getOpposite(), up ? 0F : .5F, up ? .5F : 0F))
                        .apply(Modifiers.makeVerticalSlope(up, FramedSlopePanelGeometry.SLOPE_ANGLE_VERT))
                        .apply(Modifiers.offset(vertRotDir, .5F))
                        .export(quadMap.get(null));
            }
        }
        else if (quadDir == vertRotDir)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(horRotDir, cw ? .5F : 1F, cw ? 1F : .5F))
                    .export(quadMap.get(quadDir));

            if (ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(horRotDir.getOpposite(), cw ? 0F : .5F, cw ? .5F : 0F))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), FramedSlopePanelGeometry.SLOPE_ANGLE))
                        .export(quadMap.get(null));
            }
        }
    }
}
