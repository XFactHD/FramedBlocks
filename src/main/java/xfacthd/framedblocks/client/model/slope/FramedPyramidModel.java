package xfacthd.framedblocks.client.model.slope;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.model.slopepanel.FramedSlopePanelModel;
import xfacthd.framedblocks.client.model.slopeslab.FramedSlopeSlabModel;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;
import java.util.Map;

public class FramedPyramidModel extends FramedBlockModel
{
    private static final Vector3f BOTTOM_CENTER = new Vector3f(.5F, 0, .5F);
    private static final Vector3f TOP_CENTER = new Vector3f(.5F, 1, .5F);
    private static final Vector3f ZERO = new Vector3f();

    private final Direction facing;
    private final boolean ySlope;

    public FramedPyramidModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(BlockStateProperties.FACING);
        this.ySlope = state.getValue(FramedProperties.Y_SLOPE);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(facing))
        {
            boolean up = facing == Direction.UP;
            if (!ySlope && quadDir.getAxis() != facing.getAxis())
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(false, up ? .5F : 1, up ? 1 : .5F))
                        .apply(Modifiers.cutSideLeftRight(true, up ? .5F : 1, up ? 1 : .5F))
                        .apply(Modifiers.makeVerticalSlope(up, FramedSlopePanelModel.SLOPE_ANGLE))
                        .export(quadMap.get(null));
            }
            else if (ySlope && quadDir == facing)
            {
                for (Direction dir : Direction.Plane.HORIZONTAL)
                {
                    float angle = up ? -FramedSlopePanelModel.SLOPE_ANGLE : FramedSlopePanelModel.SLOPE_ANGLE;
                    angle = (up ? -90F : 90F) - angle;
                    if (dir == Direction.NORTH || dir == Direction.EAST) { angle *= -1F; }

                    Vector3f origin = up ? TOP_CENTER : BOTTOM_CENTER;

                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F, 1))
                            .apply(Modifiers.cutTopBottom(dir.getClockWise(), 1, .5F))
                            .apply(Modifiers.offset(dir.getOpposite(), .5F))
                            .apply(Modifiers.rotate(dir.getClockWise().getAxis(), origin, angle, true))
                            .export(quadMap.get(null));
                }
            }
        }
        else
        {
            if (!ySlope && quadDir.getAxis() == facing.getAxis())
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(facing.getClockWise(), 1, .5F))
                        .apply(Modifiers.cutSideLeftRight(facing.getCounterClockWise(), 1, .5F))
                        .apply(Modifiers.makeVerticalSlope(true, FramedSlopeSlabModel.SLOPE_ANGLE))
                        .apply(Modifiers.offset(Direction.UP, .5F))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(facing.getClockWise(), .5F, 1))
                        .apply(Modifiers.cutSideLeftRight(facing.getCounterClockWise(), .5F, 1))
                        .apply(Modifiers.makeVerticalSlope(false, FramedSlopeSlabModel.SLOPE_ANGLE))
                        .apply(Modifiers.offset(Direction.DOWN, .5F))
                        .export(quadMap.get(null));
            }
            else if (ySlope && Utils.isY(quadDir))
            {
                boolean up = quadDir == Direction.UP;

                float angle = up ? FramedSlopePanelModel.SLOPE_ANGLE : -FramedSlopePanelModel.SLOPE_ANGLE;
                if (facing == Direction.NORTH || facing == Direction.EAST) { angle *= -1F; }

                Vector3f origin = facing.getOpposite().step().max(ZERO);
                if (up) { origin.add(0, 1, 0); }

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getCounterClockWise(), .5F, 1))
                        .apply(Modifiers.cutTopBottom(facing.getClockWise(), 1, .5F))
                        .apply(Modifiers.rotate(facing.getClockWise().getAxis(), origin, angle, true))
                        .export(quadMap.get(null));
            }
            else if (quadDir.getAxis() == facing.getClockWise().getAxis())
            {
                boolean right = quadDir == facing.getClockWise();
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(true, right ? 1 : .5F, right ? .5F : 1))
                        .apply(Modifiers.cutSideUpDown(false, right ? 1 : .5F, right ? .5F : 1))
                        .apply(Modifiers.makeHorizontalSlope(!right, FramedSlopePanelModel.SLOPE_ANGLE))
                        .export(quadMap.get(null));
            }
        }
    }

    @Override
    protected void applyInHandTransformation(PoseStack poseStack, ItemTransforms.TransformType type)
    {
        poseStack.translate(0, .5, 0);
    }



    public static BlockState itemSource() { return FBContent.blockFramedPyramid.get().defaultBlockState(); }
}
