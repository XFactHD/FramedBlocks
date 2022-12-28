package xfacthd.framedblocks.client.model.stairs;

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

public class FramedVerticalSlopedStairsModel extends FramedBlockModel
{
    private final Direction facing;
    private final Direction rotDir;
    private final Direction rotDirTwo;
    private final boolean useYSlope;

    public FramedVerticalSlopedStairsModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        this.rotDir = rot.withFacing(facing);
        this.rotDirTwo = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);
        this.useYSlope = false;
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir == rotDir || quadDir == rotDirTwo)
        {
            if (Utils.isY(quadDir))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(facing.getOpposite(), .5F))
                        .export(quadMap.get(quadDir));
            }
        }
        else if (quadDir == facing.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(rotDir, 1F, 0F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(rotDir.getOpposite(), 1F, 0F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }

        boolean useRotDirQuad = Utils.isY(rotDir) == useYSlope;
        Direction slopeQuadDir = useRotDirQuad ? rotDir : rotDirTwo;
        Direction slopeRotDir = useRotDirQuad ? rotDirTwo : rotDir;

        if (quadDir == slopeQuadDir)
        {
            if (Utils.isY(slopeQuadDir))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(facing, .5F))
                        .apply(Modifiers.makeVerticalSlope(slopeRotDir, 45F))
                        .export(quadMap.get(null));
            }
            else
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(facing, .5F))
                        .apply(Modifiers.makeVerticalSlope(slopeRotDir == Direction.UP, 45F))
                        .export(quadMap.get(null));
            }
        }
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedVerticalSlopedStairs.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
