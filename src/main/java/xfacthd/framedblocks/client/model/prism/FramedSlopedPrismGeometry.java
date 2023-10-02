package xfacthd.framedblocks.client.model.prism;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CompoundDirection;

public class FramedSlopedPrismGeometry implements Geometry
{
    private final Direction facing;
    private final Direction orientation;
    private final boolean ySlope;

    public FramedSlopedPrismGeometry(GeometryFactory.Context ctx)
    {
        CompoundDirection cmpDir = ctx.state().getValue(PropertyHolder.FACING_DIR);
        this.facing = cmpDir.direction();
        this.orientation = cmpDir.orientation();
        this.ySlope = ctx.state().getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        boolean yFacing = Utils.isY(facing);
        boolean yOrient = Utils.isY(orientation);
        Direction orientOpp = orientation.getOpposite();
        Direction quadFace = quad.getDirection();

        if (quadFace == orientOpp && !yOrient)
        {
            if (!yFacing) // Triangle for horizontal facing and horizontal orientation
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSmallTriangle(facing))
                        .apply(Modifiers.makeHorizontalSlope(orientation == facing.getClockWise(), 45))
                        .export(quadMap.get(null));
            }
            else if (!ySlope)  // Triangle for horizontal facing and vertical orientation without Y_SLOPE
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSmallTriangle(facing))
                        .apply(Modifiers.makeVerticalSlope(facing == Direction.UP, 45))
                        .export(quadMap.get(null));
            }
        }
        else if (ySlope && yFacing && Utils.isY(quadFace)) // Triangle and slopes for vertical facing with Y_SLOPE
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSmallTriangle(orientation))
                    .apply(Modifiers.makeVerticalSlope(orientOpp, 45))
                    .export(quadMap.get(null));

            Direction offAxisCW = orientation.getClockWise();
            Direction offAxisCCW = orientation.getCounterClockWise();

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(offAxisCW, .5F))
                    .apply(Modifiers.cutTopBottom(orientOpp, 1, 0))
                    .apply(Modifiers.makeVerticalSlope(offAxisCCW, 45))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(offAxisCCW, .5F))
                    .apply(Modifiers.cutTopBottom(orientOpp, 0, 1))
                    .apply(Modifiers.makeVerticalSlope(offAxisCW, 45))
                    .export(quadMap.get(null));
        }
        else if (!ySlope && yOrient && quadFace == facing) // Tilted triangle for horizontal facing and vertical orientation without Y_SLOPE
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSmallTriangle(orientation))
                    .apply(Modifiers.makeVerticalSlope(orientation == Direction.DOWN, 45))
                    .export(quadMap.get(null));
        }
        else if (ySlope && yOrient && quadFace == orientOpp) // Tilted triangle for horizontal facing and vertical orientation with Y_SLOPE
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSmallTriangle(facing))
                    .apply(Modifiers.makeVerticalSlope(facing, 45))
                    .export(quadMap.get(null));
        }
        else if (quadFace == orientation) // Triangle
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSmallTriangle(facing))
                    .export(quadMap.get(quadFace));
        }
        else if (!ySlope && yFacing && quadFace.getAxis() == orientation.getClockWise().getAxis()) // Slopes for Y facing without Y_SLOPE
        {
            boolean up = facing == Direction.UP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(facing == Direction.DOWN, .5F))
                    .apply(Modifiers.cutSideLeftRight(orientation.getOpposite(), up ? 0 : 1, up ? 1 : 0))
                    .apply(Modifiers.makeVerticalSlope(up, 45))
                    .export(quadMap.get(null));
        }
        else if (yOrient && quadFace.getAxis() == facing.getClockWise().getAxis()) // Slopes for horizontal facing and vertical orientation
        {
            boolean right = quadFace == facing.getClockWise();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(facing, .5F))
                    .apply(Modifiers.cutSideUpDown(orientation == Direction.UP, right ? 1 : 0, right ? 0 : 1))
                    .apply(Modifiers.makeHorizontalSlope(quadFace == facing.getCounterClockWise(), 45))
                    .export(quadMap.get(null));
        }
        else if (!ySlope && !yOrient && !yFacing && quadFace == facing) // Slopes for horizontal facing and horizontal orientation without Y_SLOPE
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(false, .5F))
                    .apply(Modifiers.cutSideLeftRight(orientation.getOpposite(), 0, 1))
                    .apply(Modifiers.makeVerticalSlope(false, 45))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(true, .5F))
                    .apply(Modifiers.cutSideLeftRight(orientation.getOpposite(), 1, 0))
                    .apply(Modifiers.makeVerticalSlope(true, 45))
                    .export(quadMap.get(null));
        }
        else if (ySlope && !yOrient && !yFacing && Utils.isY(quadFace)) // Slopes for horizontal facing and horizontal orientation with Y_SLOPE
        {
            boolean right = orientation == facing.getClockWise();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(facing, .5F))
                    .apply(Modifiers.cutTopBottom(orientOpp, right ? 0 : 1, right ? 1 : 0))
                    .apply(Modifiers.makeVerticalSlope(facing, 45))
                    .export(quadMap.get(null));
        }
    }

    @Override
    public void applyInHandTransformation(PoseStack poseStack, ItemDisplayContext ctx)
    {
        poseStack.translate(0, .5, 0);
    }



    public static BlockState itemSource()
    {
        return FBContent.BLOCK_FRAMED_SLOPED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.UP_WEST);
    }
}
