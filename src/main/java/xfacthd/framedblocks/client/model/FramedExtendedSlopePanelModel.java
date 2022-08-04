package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

import java.util.List;
import java.util.Map;

public class FramedExtendedSlopePanelModel extends FramedBlockModel
{
    private final Direction facing;
    private final HorizontalRotation rotation;
    private final Direction orientation;

    public FramedExtendedSlopePanelModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.rotation = state.getValue(PropertyHolder.ROTATION);
        this.orientation = rotation.withFacing(facing);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        boolean yAxis = Utils.isY(orientation);
        if (face == orientation)
        {
            if (Utils.isY(orientation))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getOpposite(), .5F))
                        .export(quadMap.get(face));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(facing.getOpposite(), .5F))
                        .export(quadMap.get(face));
            }
        }
        else if (face == facing.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(FramedSlopePanelModel.createSlope(facing, orientation))
                    .export(quadMap.get(null));
        }
        else if (face.getAxis() != facing.getAxis() && face.getAxis() != orientation.getAxis())
        {
            if (yAxis)
            {
                boolean up = orientation == Direction.UP;
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(facing.getOpposite(), up ? .5F :  1F, up ?  1F : .5F))
                        .export(quadMap.get(face));
            }
            else
            {
                boolean rightRot = rotation == HorizontalRotation.RIGHT;
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getOpposite(), rightRot ?  1F : .5F, rightRot ? .5F :  1F))
                        .export(quadMap.get(face));
            }
        }
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedExtendedSlopePanel.get().defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
