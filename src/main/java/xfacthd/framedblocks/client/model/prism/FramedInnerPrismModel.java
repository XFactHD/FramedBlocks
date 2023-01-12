package xfacthd.framedblocks.client.model.prism;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.DirectionAxis;

import java.util.List;
import java.util.Map;

public class FramedInnerPrismModel extends FramedBlockModel
{
    private final Direction facing;
    private final Direction.Axis axis;

    public FramedInnerPrismModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        this.facing = dirAxis.direction();
        this.axis = dirAxis.axis();
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadFace = quad.getDirection();
        if (Utils.isY(facing) && quadFace.getAxis() != axis && quadFace.getAxis() != facing.getAxis()) // Slopes for Y facing
        {
            boolean up = facing == Direction.UP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(up, .5F))
                    .apply(Modifiers.makeVerticalSlope(up, 45))
                    .export(quadMap.get(null));
        }
        else if (!Utils.isY(facing) && axis == Direction.Axis.Y && quadFace.getAxis() != axis && quadFace.getAxis() != facing.getAxis()) //Slopes for horizontal facing and Y axis
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(facing.getOpposite(), .5F))
                    .apply(Modifiers.makeHorizontalSlope(quadFace == facing.getCounterClockWise(), 45))
                    .export(quadMap.get(null));
        }
        else if (!Utils.isY(facing) && axis != Direction.Axis.Y && quadFace == facing)
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
        else if (quadFace.getAxis() == axis)
        {
            if (axis == Direction.Axis.Y)
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
            else if (Utils.isY(facing))
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
            else //axis != Direction.Axis.Y && !Utils.isY(facing)
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

    @Override
    protected boolean transformAllQuads(BlockState state)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return Utils.isY(dirAxis.direction()) || dirAxis.axis() == Direction.Axis.Y;
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedInnerPrism.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_AXIS, DirectionAxis.UP_X);
    }
}
