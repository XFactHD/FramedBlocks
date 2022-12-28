package xfacthd.framedblocks.client.model.slopepanel;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
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

public class FramedFlatExtendedSlopePanelCornerModel extends FramedBlockModel
{
    private final Direction facing;
    private final HorizontalRotation rotation;
    private final HorizontalRotation rotRotation;
    private final Direction orientation;
    private final Direction rotOrientation;

    public FramedFlatExtendedSlopePanelCornerModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.rotation = state.getValue(PropertyHolder.ROTATION);
        this.rotRotation = rotation.rotate(Rotation.COUNTERCLOCKWISE_90);
        this.orientation = rotation.withFacing(facing);
        this.rotOrientation = rotRotation.withFacing(facing);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        if (face == orientation.getOpposite())
        {
            FramedFlatSlopePanelCornerModel.createSideTriangle(quadMap, quad, facing, rotRotation, true, true);
        }
        else if (face == rotOrientation.getOpposite())
        {
            FramedFlatSlopePanelCornerModel.createSideTriangle(quadMap, quad, facing, rotation, true, true);
        }
        else if (face == orientation || face == rotOrientation)
        {
            if (Utils.isY(face))
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
                    .apply(FramedFlatSlopePanelCornerModel.createSlopeTriangle(facing, orientation, false))
                    .apply(FramedSlopePanelModel.createSlope(facing, orientation))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(FramedFlatSlopePanelCornerModel.createSlopeTriangle(facing, rotOrientation, true))
                    .apply(FramedSlopePanelModel.createSlope(facing, rotOrientation))
                    .export(quadMap.get(null));
        }
        else if (face == facing)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedFlatExtendedSlopePanelCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.ROTATION, HorizontalRotation.RIGHT);
    }
}
