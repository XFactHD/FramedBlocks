package xfacthd.framedblocks.client.model.slopepanel;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

import java.util.List;
import java.util.Map;

public class FramedFlatInnerSlopePanelCornerModel extends FramedBlockModel
{
    private final Direction facing;
    private final HorizontalRotation rotation;
    private final HorizontalRotation rotRotation;
    private final Direction orientation;
    private final Direction rotOrientation;
    private final boolean front;

    public FramedFlatInnerSlopePanelCornerModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.rotation = state.getValue(PropertyHolder.ROTATION);
        this.rotRotation = rotation.rotate(Rotation.COUNTERCLOCKWISE_90);
        this.orientation = rotation.withFacing(facing);
        this.rotOrientation = rotRotation.withFacing(facing);
        this.front = state.getValue(PropertyHolder.FRONT);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        if (face == orientation)
        {
            FramedFlatSlopePanelCornerModel.createSideTriangle(quadMap, quad, facing, rotRotation, front, false);
        }
        else if (face == rotOrientation)
        {
            FramedFlatSlopePanelCornerModel.createSideTriangle(quadMap, quad, facing, rotation, front, false);
        }
        else if (face == orientation.getOpposite() || face == rotOrientation.getOpposite())
        {
            Direction cutDir = front ? facing : facing.getOpposite();
            if (Utils.isY(face))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(cutDir, .5F))
                        .export(quadMap.get(face));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(cutDir, .5F))
                        .export(quadMap.get(face));
            }
        }
        else if (face == facing.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(FramedFlatSlopePanelCornerModel.createSlopeTriangle(facing, rotOrientation, true))
                    .apply(FramedSlopePanelModel.createSlope(facing, orientation))
                    .applyIf(Modifiers.offset(facing, .5F), !front)
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(FramedFlatSlopePanelCornerModel.createSlopeTriangle(facing, orientation, false))
                    .apply(FramedSlopePanelModel.createSlope(facing, rotOrientation))
                    .applyIf(Modifiers.offset(facing, .5F), !front)
                    .export(quadMap.get(null));
        }
        else if (face == facing && front)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
    }

    @Override
    protected void applyInHandTransformation(PoseStack poseStack, ItemTransforms.TransformType type)
    {
        poseStack.translate(0, .5, 0);
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedFlatInnerSlopePanelCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.ROTATION, HorizontalRotation.RIGHT);
    }
}
