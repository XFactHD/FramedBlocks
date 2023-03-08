package xfacthd.framedblocks.client.model.slope;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
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

public class FramedPyramidSlabModel extends FramedBlockModel
{
    private static final Vector3f ZERO = new Vector3f();
    private static final Vector3f MINUS_ONE = new Vector3f(-1, -1, -1);

    private final Direction facing;
    private final boolean ySlope;

    public FramedPyramidSlabModel(BlockState state, BakedModel baseModel)
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
                        .apply(Modifiers.cutSideUpDown(!up, .5F))
                        .apply(Modifiers.cutSideLeftRight(false, up ? 0 : 1, up ? 1 : 0))
                        .apply(Modifiers.cutSideLeftRight(true, up ? 0 : 1, up ? 1 : 0))
                        .apply(Modifiers.makeVerticalSlope(up, 45))
                        .export(quadMap.get(null));
            }
            else if (ySlope && quadDir == facing)
            {
                for (Direction dir : Direction.Plane.HORIZONTAL)
                {
                    boolean northeast = dir == Direction.NORTH || dir == Direction.EAST;
                    float angle = up ? -45 : 45;
                    if (northeast) { angle *= -1F; }
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutTopBottom(dir, .5F))
                            .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), 0, 1))
                            .apply(Modifiers.cutTopBottom(dir.getClockWise(), 1, 0))
                            .apply(Modifiers.setPosition(.5F))
                            .apply(Modifiers.rotateCentered(dir.getClockWise().getAxis(), angle, true))
                            .export(quadMap.get(null));
                }
            }
        }
        else
        {
            if (!ySlope && quadDir.getAxis() == facing.getAxis())
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(true, .5F))
                        .apply(Modifiers.cutSideLeftRight(facing.getClockWise(), 1, 0))
                        .apply(Modifiers.cutSideLeftRight(facing.getCounterClockWise(), 1, 0))
                        .apply(Modifiers.makeVerticalSlope(true, 45))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(false, .5F))
                        .apply(Modifiers.cutSideLeftRight(facing.getClockWise(), 0, 1))
                        .apply(Modifiers.cutSideLeftRight(facing.getCounterClockWise(), 0, 1))
                        .apply(Modifiers.makeVerticalSlope(false, 45))
                        .export(quadMap.get(null));
            }
            else if (ySlope && Utils.isY(quadDir))
            {
                boolean up = quadDir == Direction.UP;

                float angle = up ? 45 : -45;
                if (facing == Direction.NORTH || facing == Direction.EAST) { angle *= -1F; }

                Vector3f origin = facing.getOpposite().step();
                origin.clamp(MINUS_ONE, ZERO);
                if (up) { origin.add(0, 1, 0); }

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing, .5F))
                        .apply(Modifiers.cutTopBottom(facing.getCounterClockWise(), 0, 1))
                        .apply(Modifiers.cutTopBottom(facing.getClockWise(), 1, 0))
                        .apply(Modifiers.rotate(facing.getClockWise().getAxis(), origin, angle, true))
                        .export(quadMap.get(null));
            }
            else if (quadDir.getAxis() == facing.getClockWise().getAxis())
            {
                boolean right = quadDir == facing.getClockWise();
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(facing, .5F))
                        .apply(Modifiers.cutSideUpDown(true, right ? 1 : 0, right ? 0 : 1))
                        .apply(Modifiers.cutSideUpDown(false, right ? 1 : 0, right ? 0 : 1))
                        .apply(Modifiers.makeHorizontalSlope(!right, 45))
                        .export(quadMap.get(null));
            }
        }
    }

    @Override
    protected void applyInHandTransformation(PoseStack poseStack, ItemTransforms.TransformType type)
    {
        poseStack.translate(0, .5, 0);
    }



    public static BlockState itemSource() { return FBContent.blockFramedPyramidSlab.get().defaultBlockState(); }
}
