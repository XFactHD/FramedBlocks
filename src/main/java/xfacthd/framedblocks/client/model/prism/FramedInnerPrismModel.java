package xfacthd.framedblocks.client.model.prism;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;
import java.util.Map;

public class FramedInnerPrismModel extends FramedBlockModel
{
    private final Direction facing;
    private final Direction.Axis axis;
    private final boolean ySlope;

    public FramedInnerPrismModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(BlockStateProperties.FACING);
        this.axis = state.getValue(BlockStateProperties.AXIS);
        this.ySlope = state.getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadFace = quad.getDirection();
        if (isStateInvalid())
        {
            quadMap.get(quadFace).add(quad);
            return;
        }

        boolean yFacing = Utils.isY(facing);
        boolean yAxis = axis == Direction.Axis.Y;
        boolean quadOnFacingAxis = quadFace.getAxis() == facing.getAxis();
        boolean quadOnAxis = quadFace.getAxis() == axis;

        if (!ySlope && yFacing && !quadOnAxis && !quadOnFacingAxis) // Slopes for Y facing without Y_SLOPE
        {
            boolean up = facing == Direction.UP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(up, .5F))
                    .apply(Modifiers.makeVerticalSlope(up, 45))
                    .export(quadMap.get(null));
        }
        else if (ySlope && yFacing && quadFace == facing) // Slopes for Y facing with Y_SLOPE
        {
            Direction onAxis = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);

            Direction offAxisCW = onAxis.getClockWise();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(offAxisCW, .5F))
                    .apply(Modifiers.makeVerticalSlope(offAxisCW, 45))
                    .export(quadMap.get(null));

            Direction offAxisCCW = onAxis.getCounterClockWise();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(offAxisCCW, .5F))
                    .apply(Modifiers.makeVerticalSlope(offAxisCCW, 45))
                    .export(quadMap.get(null));
        }
        else if (!yFacing && yAxis && !quadOnAxis && !quadOnFacingAxis) // Slopes for horizontal facing and Y axis without Y_SLOPE
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(facing.getOpposite(), .5F))
                    .apply(Modifiers.makeHorizontalSlope(quadFace == facing.getCounterClockWise(), 45))
                    .export(quadMap.get(null));
        }
        else if (!ySlope && !yFacing && !yAxis && quadFace == facing)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(false, .5F))
                    .apply(Modifiers.makeVerticalSlope(true, 45))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(true, .5F))
                    .apply(Modifiers.makeVerticalSlope(false, 45))
                    .export(quadMap.get(null));
        }
        else if (ySlope && !yFacing && !yAxis && Utils.isY(quadFace)) // Slopes for horizontal facing and Y axis with Y_SLOPE
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(facing.getOpposite(), .5F))
                    .apply(Modifiers.makeVerticalSlope(facing, 45))
                    .export(quadMap.get(null));
        }
        else if (quadOnAxis)
        {
            if (yAxis)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getClockWise(), .5F))
                        .apply(Modifiers.cutTopBottom(facing, 0F, 1F))
                        .export(quadMap.get(quadFace));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutTopBottom(facing, 1F, 0F))
                        .export(quadMap.get(quadFace));
            }
            else if (yFacing)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(quadFace.getClockWise(), .5F))
                        .apply(Modifiers.cutSideUpDown(facing == Direction.DOWN, 0F, 1F))
                        .export(quadMap.get(quadFace));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(quadFace.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutSideUpDown(facing == Direction.DOWN, 1F, 0F))
                        .export(quadMap.get(quadFace));
            }
            else //!yAxis && !yFacing
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(true, .5F))
                        .apply(Modifiers.cutSideLeftRight(facing, 1F, 0F))
                        .export(quadMap.get(quadFace));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(false, .5F))
                        .apply(Modifiers.cutSideLeftRight(facing, 0F, 1F))
                        .export(quadMap.get(quadFace));
            }
        }
    }

    private boolean isStateInvalid() { return axis == facing.getAxis(); }

    @Override
    protected boolean transformAllQuads(BlockState state)
    {
        if (state.getValue(FramedProperties.Y_SLOPE))
        {
            return true;
        }
        Direction facing = state.getValue(BlockStateProperties.FACING);
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        return Utils.isY(facing) || axis == Direction.Axis.Y;
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedInnerPrism.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.UP);
    }
}
