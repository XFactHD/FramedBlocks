package xfacthd.framedblocks.client.model.slopepanel;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedFlatSlopePanelCornerModel extends FramedBlockModel
{
    private final Direction facing;
    private final HorizontalRotation rotation;
    private final HorizontalRotation rotRotation;
    private final Direction orientation;
    private final Direction rotOrientation;
    private final boolean front;
    private final boolean ySlope;

    public FramedFlatSlopePanelCornerModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.rotation = state.getValue(PropertyHolder.ROTATION);
        this.rotRotation = rotation.rotate(Rotation.COUNTERCLOCKWISE_90);
        this.orientation = rotation.withFacing(facing);
        this.rotOrientation = rotRotation.withFacing(facing);
        this.front = state.getValue(PropertyHolder.FRONT);
        this.ySlope = state.getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    protected void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        if (face == orientation.getOpposite())
        {
            createSideTriangle(quadMap, quad, facing, rotRotation, front, false);
        }
        else if (face == rotOrientation.getOpposite())
        {
            createSideTriangle(quadMap, quad, facing, rotation, front, false);
        }
        else if (face == facing.getOpposite())
        {
            if (!ySlope || !Utils.isY(orientation))
            {
                QuadModifier.geometry(quad)
                        .apply(createSlopeTriangle(facing, orientation, false))
                        .apply(FramedSlopePanelModel.createSlope(facing, orientation))
                        .applyIf(Modifiers.offset(facing, .5F), !front)
                        .export(quadMap.get(null));
            }

            if (!ySlope || !Utils.isY(rotOrientation))
            {
                QuadModifier.geometry(quad)
                        .apply(createSlopeTriangle(facing, rotOrientation, true))
                        .apply(FramedSlopePanelModel.createSlope(facing, rotOrientation))
                        .applyIf(Modifiers.offset(facing, .5F), !front)
                        .export(quadMap.get(null));
            }
        }
        else if (ySlope && Utils.isY(orientation) && face == orientation)
        {
            QuadModifier.geometry(quad)
                    .apply(createVerticalSlopeTriangle(facing, orientation, false))
                    .apply(FramedSlopePanelModel.createVerticalSlope(facing, orientation))
                    .applyIf(Modifiers.offset(facing.getOpposite(), .5F), front)
                    .export(quadMap.get(null));
        }
        else if (ySlope && Utils.isY(rotOrientation) && face == rotOrientation)
        {
            QuadModifier.geometry(quad)
                    .apply(createVerticalSlopeTriangle(facing, rotOrientation, true))
                    .apply(FramedSlopePanelModel.createVerticalSlope(facing, rotOrientation))
                    .applyIf(Modifiers.offset(facing.getOpposite(), .5F), front)
                    .export(quadMap.get(null));
        }
        else if (face == facing && front)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
    }

    public static QuadModifier.Modifier createSlopeTriangle(Direction facing, Direction orientation, boolean second)
    {
        if (Utils.isY(orientation))
        {
            boolean down = orientation == Direction.UP;
            float right = (down != second) ? 0 : 1;
            float left  = (down != second) ? 1 : 0;
            return Modifiers.cutSideUpDown(down, right, left);
        }
        else
        {
            boolean right = orientation == facing.getClockWise();
            float top = (right != second) ? 0 : 1;
            float bot = (right != second) ? 1 : 0;
            return Modifiers.cutSideLeftRight(right, top, bot);
        }
    }

    public static QuadModifier.Modifier createVerticalSlopeTriangle(
            Direction facing, Direction orientation, boolean second
    )
    {
        boolean down = orientation == Direction.DOWN;
        float right = (second == down) ? 0 : 1;
        float left  = (second == down) ? 1 : 0;
        return Modifiers.cutTopBottom(facing.getOpposite(), right, left);
    }

    public static void createSideTriangle(
            QuadMap quadMap,
            BakedQuad quad,
            Direction facing,
            HorizontalRotation rotation,
            boolean front,
            boolean extended
    )
    {
        Direction face = quad.getDirection();
        Direction orientation = rotation.withFacing(facing);
        boolean yAxis = Utils.isY(orientation);

        if (yAxis)
        {
            boolean up = orientation == Direction.UP;
            float top =    up ? (front ? .5F :  0F) : (front ?  1F : .5F);
            float bottom = up ? (front ?  1F : .5F) : (front ? .5F :  0F);
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(facing.getOpposite(), top, bottom))
                    .applyIf(Modifiers.cutSideLeftRight(facing, .5F), front && !extended)
                    .export(quadMap.get(face));
        }
        else
        {
            boolean rightRot = rotation == HorizontalRotation.RIGHT;
            float right = rightRot ? (front ?  1F : .5F) : (front ? .5F :  0F);
            float left =  rightRot ? (front ? .5F :  0F) : (front ?  1F : .5F);
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(facing.getOpposite(), right, left))
                    .applyIf(Modifiers.cutTopBottom(facing, .5F), front && !extended)
                    .export(quadMap.get(face));
        }
    }

    @Override
    protected void applyInHandTransformation(PoseStack poseStack, ItemDisplayContext ctx)
    {
        poseStack.translate(0, .5, 0);
    }



    public static BlockState itemSource()
    {
        return FBContent.BLOCK_FRAMED_FLAT_SLOPE_PANEL_CORNER.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH)
                .setValue(PropertyHolder.ROTATION, HorizontalRotation.RIGHT);
    }
}
