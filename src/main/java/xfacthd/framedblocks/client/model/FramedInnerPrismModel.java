package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;
import java.util.Map;

public class FramedInnerPrismModel extends FramedBlockModel
{
    private final Direction facing;
    private final Direction.Axis axis;

    public FramedInnerPrismModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(BlockStateProperties.FACING);
        this.axis = state.getValue(BlockStateProperties.AXIS);
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

    private boolean isStateInvalid() { return axis == facing.getAxis(); }

    @Override
    protected boolean transformAllQuads(BlockState state)
    {
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
