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
import xfacthd.framedblocks.common.data.property.CompoundDirection;

import java.util.List;
import java.util.Map;

public class FramedInnerSlopedPrismModel extends FramedBlockModel
{
    private final Direction facing;
    private final Direction orientation;

    public FramedInnerSlopedPrismModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        this.facing = cmpDir.direction();
        this.orientation = cmpDir.orientation();
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadFace = quad.getDirection();
        if (Utils.isY(facing) && quadFace.getAxis() != orientation.getAxis() && quadFace.getAxis() != facing.getAxis()) // Slopes for Y facing
        {
            boolean up = facing == Direction.UP;
            float top = up ? 1 : 0;
            float bottom = up ? 0 : 1;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(up, .5F))
                    .apply(Modifiers.cutSideLeftRight(orientation.getOpposite(), top, bottom))
                    .apply(Modifiers.makeVerticalSlope(up, 45))
                    .export(quadMap.get(null));
        }
        else if (!Utils.isY(facing) && Utils.isY(orientation) && quadFace.getAxis() != orientation.getAxis() && quadFace.getAxis() != facing.getAxis()) //Slopes for horizontal facing and Y axis
        {
            boolean down = orientation == Direction.UP;
            boolean cw = quadFace == facing.getClockWise();
            float right = cw ? 0F : 1F;
            float left = cw ? 1F : 0F;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(facing.getOpposite(), .5F))
                    .apply(Modifiers.cutSideUpDown(down, right, left))
                    .apply(Modifiers.makeHorizontalSlope(quadFace == facing.getCounterClockWise(), 45))
                    .export(quadMap.get(null));
        }
        else if (!Utils.isY(facing) && !Utils.isY(orientation) && quadFace == facing)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(false, .5F))
                    .apply(Modifiers.cutSideLeftRight(orientation.getOpposite(), 0F, 1F))
                    .apply(Modifiers.makeVerticalSlope(true, 45))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(true, .5F))
                    .apply(Modifiers.cutSideLeftRight(orientation.getOpposite(), 1F, 0F))
                    .apply(Modifiers.makeVerticalSlope(false, 45))
                    .export(quadMap.get(null));

            boolean right = orientation == facing.getClockWise();
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(orientation, .5F))
                    .apply(Modifiers.cutSmallTriangle(orientation))
                    .apply(Modifiers.makeHorizontalSlope(right, 45))
                    .export(quadMap.get(null));
        }
        else if (!Utils.isY(facing) && Utils.isY(orientation) && quadFace == facing)
        {
            //TODO: disable when y_slope is active
            boolean up = orientation == Direction.UP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSmallTriangle(orientation))
                    .apply(Modifiers.makeVerticalSlope(up, 45))
                    .export(quadMap.get(null));
        }
        else if (quadFace == orientation)
        {
            if (Utils.isY(orientation))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getClockWise(), .5F))
                        .apply(Modifiers.cutTopBottom(facing, 0F, 1F))
                        .export(quadMap.get(quadFace));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getCounterClockWise(), .5F))
                        .apply(Modifiers.cutTopBottom(facing, 1F, 0F))
                        .export(quadMap.get(quadFace));

                //TODO: use for y_slope impl
                //QuadModifier.geometry(quad)
                //        .apply(Modifiers.cutSmallTriangle(facing.getOpposite()))
                //        .apply(Modifiers.makeVerticalSlope(facing, 45))
                //        .export(quadMap.get(null));
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

                boolean up = facing == Direction.UP;
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSmallTriangle(facing.getOpposite()))
                        .apply(Modifiers.makeVerticalSlope(up, 45))
                        .export(quadMap.get(null));
            }
            else //!Utils.isY(orientation) && !Utils.isY(facing)
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
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return Utils.isY(cmpDir.direction()) || Utils.isY(cmpDir.orientation());
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedInnerSlopedPrism.get()
                .defaultBlockState()
                .setValue(PropertyHolder.FACING_DIR, CompoundDirection.UP_EAST);
    }
}
