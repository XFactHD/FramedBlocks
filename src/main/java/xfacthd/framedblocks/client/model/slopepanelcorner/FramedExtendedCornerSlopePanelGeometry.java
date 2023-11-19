package xfacthd.framedblocks.client.model.slopepanelcorner;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.client.model.slopepanel.FramedSlopePanelGeometry;
import xfacthd.framedblocks.common.FBContent;

public class FramedExtendedCornerSlopePanelGeometry implements Geometry
{
    private final Direction dir;
    private final boolean top;
    private final boolean ySlope;

    public FramedExtendedCornerSlopePanelGeometry(GeometryFactory.Context ctx)
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

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, top ? 1F : .5F, top ? .5F : 1F))
                    .export(quadMap.get(quadDir));
        }
        else if (!ySlope && (quadDir == dir.getOpposite() || quadDir == dir.getClockWise()))
        {
            Direction cutDir = quadDir == dir.getOpposite() ? dir.getClockWise() : dir.getOpposite();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, top ? 1F : .5F, top ? .5F : 1F))
                    .apply(Modifiers.makeVerticalSlope(!top, FramedSlopePanelGeometry.SLOPE_ANGLE))
                    .export(quadMap.get(null));
        }
        else if ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN))
        {
            if (ySlope)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F, 1))
                        .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), FramedSlopePanelGeometry.SLOPE_ANGLE_VERT))
                        .apply(Modifiers.offset(dir.getClockWise(), .5F))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(dir.getClockWise(), 1, .5F))
                        .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), FramedSlopePanelGeometry.SLOPE_ANGLE_VERT))
                        .apply(Modifiers.offset(dir.getOpposite(), .5F))
                        .export(quadMap.get(null));
            }

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .export(quadMap.get(quadDir));
        }
    }



    public static BlockState itemModelSource()
    {
        return FBContent.BLOCK_FRAMED_EXTENDED_CORNER_SLOPE_PANEL.value()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.WEST);
    }
}
