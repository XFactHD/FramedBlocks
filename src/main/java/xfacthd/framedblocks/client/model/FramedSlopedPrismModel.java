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
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedSlopedPrismModel extends FramedBlockModel
{
    private final Direction facing;
    private final Direction orientation;

    public FramedSlopedPrismModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(BlockStateProperties.FACING);
        this.orientation = state.getValue(PropertyHolder.ORIENTATION);
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

        if (quadFace == orientation.getOpposite() && !Utils.isY(orientation))
        {
            if (Utils.isY(facing))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSmallTriangle(facing))
                        .apply(Modifiers.makeVerticalSlope(facing == Direction.UP, 45))
                        .export(quadMap.get(null));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSmallTriangle(facing))
                        .apply(Modifiers.makeHorizontalSlope(orientation == facing.getClockWise(), 45))
                        .export(quadMap.get(null));
            }
        }
        else if (quadFace == facing && Utils.isY(orientation))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSmallTriangle(orientation))
                    .apply(Modifiers.makeVerticalSlope(orientation == Direction.DOWN, 45))
                    .export(quadMap.get(null));
        }
        else if (quadFace == orientation)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSmallTriangle(facing))
                    .export(quadMap.get(quadFace));
        }
        else if (Utils.isY(facing) && quadFace.getAxis() == orientation.getClockWise().getAxis())
        {
            QuadModifier mod = QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(facing == Direction.DOWN, .5F));

            mod.derive().apply(Modifiers.cutSideLeftRight(orientation, .5F))
                    .apply(Modifiers.cutSmallTriangle(facing))
                    .apply(Modifiers.makeVerticalSlope(facing == Direction.UP, 45))
                    .export(quadMap.get(null));

            mod.apply(Modifiers.cutSideLeftRight(orientation.getOpposite(), .5F))
                    .apply(Modifiers.makeVerticalSlope(facing == Direction.UP, 45))
                    .export(quadMap.get(null));
        }
        else if (Utils.isY(orientation) && quadFace.getAxis() == facing.getClockWise().getAxis())
        {
            QuadModifier mod = QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(facing, .5F));

            mod.derive().apply(Modifiers.cutSideUpDown(orientation == Direction.DOWN, .5F))
                    .apply(Modifiers.cutSmallTriangle(facing))
                    .apply(Modifiers.makeHorizontalSlope(quadFace == facing.getCounterClockWise(), 45))
                    .export(quadMap.get(null));

            mod.apply(Modifiers.cutSideUpDown(orientation == Direction.UP, .5F))
                    .apply(Modifiers.makeHorizontalSlope(quadFace == facing.getCounterClockWise(), 45))
                    .export(quadMap.get(null));
        }
        else if (!Utils.isY(orientation) && !Utils.isY(facing) && quadFace == facing)
        {
            QuadModifier mod = QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(false, .5F));

            mod.derive().apply(Modifiers.cutSideLeftRight(orientation, .5F))
                    .apply(Modifiers.cutSmallTriangle(Direction.UP))
                    .apply(Modifiers.makeVerticalSlope(false, 45))
                    .export(quadMap.get(null));

            mod.apply(Modifiers.cutSideLeftRight(orientation.getOpposite(), .5F))
                    .apply(Modifiers.makeVerticalSlope(false, 45))
                    .export(quadMap.get(null));

            mod = QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(true, .5F));

            mod.derive().apply(Modifiers.cutSideLeftRight(orientation, .5F))
                    .apply(Modifiers.cutSmallTriangle(Direction.DOWN))
                    .apply(Modifiers.makeVerticalSlope(true, 45))
                    .export(quadMap.get(null));

            mod.apply(Modifiers.cutSideLeftRight(orientation.getOpposite(), .5F))
                    .apply(Modifiers.makeVerticalSlope(true, 45))
                    .export(quadMap.get(null));
        }
    }

    private boolean isStateInvalid() { return orientation.getAxis() == facing.getAxis(); }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedSlopedPrism.get().defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.UP)
                .setValue(PropertyHolder.ORIENTATION, Direction.WEST);
    }
}
