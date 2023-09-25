package xfacthd.framedblocks.client.model.prism;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.DirectionAxis;

public class FramedPrismModel extends FramedBlockModel
{
    private final Direction facing;
    private final Direction.Axis axis;
    private final boolean ySlope;

    public FramedPrismModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        this.facing = dirAxis.direction();
        this.axis = dirAxis.axis();
        this.ySlope = state.getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    protected void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        boolean yFacing = Utils.isY(facing);
        boolean yAxis = axis == Direction.Axis.Y;
        Direction quadFace = quad.getDirection();
        boolean quadOnAxis = quadFace.getAxis() == axis;
        boolean quadOnFacingAxis = quadFace.getAxis() == facing.getAxis();

        if (!ySlope && yFacing && !quadOnAxis && !quadOnFacingAxis) // Slopes for Y facing without Y_SLOPE
        {
            boolean up = facing == Direction.UP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(!up, .5F))
                    .apply(Modifiers.makeVerticalSlope(up, 45))
                    .export(quadMap.get(null));
        }
        else if (ySlope && yFacing && Utils.isY(quadFace)) // Slopes for Y facing with Y_SLOPE
        {
            Direction onAxis = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
            Direction offAxisCW = onAxis.getClockWise();
            Direction offAxisCCW = onAxis.getCounterClockWise();

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(offAxisCW, .5F))
                    .apply(Modifiers.makeVerticalSlope(offAxisCCW, 45))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(offAxisCCW, .5F))
                    .apply(Modifiers.makeVerticalSlope(offAxisCW, 45))
                    .export(quadMap.get(null));
        }
        else if (!yFacing && yAxis && !quadOnAxis && !quadOnFacingAxis) // Slopes for horizontal facing and vertical axis
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(facing, .5F))
                    .apply(Modifiers.makeHorizontalSlope(quadFace == facing.getCounterClockWise(), 45))
                    .export(quadMap.get(null));
        }
        else if (!ySlope && !yFacing && !yAxis && quadFace == facing) // Slopes for horizontal facing and horizontal axis without Y_SLOPE
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(false, .5F))
                    .apply(Modifiers.makeVerticalSlope(false, 45))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(true, .5F))
                    .apply(Modifiers.makeVerticalSlope(true, 45))
                    .export(quadMap.get(null));
        }
        else if (ySlope && !yFacing && !yAxis && Utils.isY(quadFace)) // Slopes for horizontal facing and horizontal axis with Y_SLOPE
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(facing, .5F))
                    .apply(Modifiers.makeVerticalSlope(facing, 45))
                    .export(quadMap.get(null));
        }
        else if (quadFace.getAxis() == axis) // Triangles
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSmallTriangle(facing))
                    .export(quadMap.get(quadFace));
        }
    }

    @Override
    protected void applyInHandTransformation(PoseStack poseStack, ItemDisplayContext ctx)
    {
        poseStack.translate(0, .5, 0);
    }



    public static BlockState itemSource()
    {
        return FBContent.BLOCK_FRAMED_PRISM.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.UP_X);
    }
}
