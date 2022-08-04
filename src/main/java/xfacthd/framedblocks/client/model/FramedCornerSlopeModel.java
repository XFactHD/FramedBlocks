package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.property.CornerType;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedCornerSlopeModel extends FramedBlockModel
{
    private final Direction dir;
    private final CornerType type;

    public FramedCornerSlopeModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(FramedProperties.FACING_HOR);
        type = state.getValue(PropertyHolder.CORNER_TYPE);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (type.isHorizontal())
        {
            createHorizontalCornerSlope(quadMap, quad);
        }
        else
        {
            createVerticalCornerSlope(quadMap, quad);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void createHorizontalCornerSlope(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        boolean top = type.isTop();
        boolean right = type.isRight();

        if ((quadDir == dir.getClockWise() && right) || (quadDir == dir.getCounterClockWise() && !right))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 1 : 0, top ? 0 : 1))
                    .export(quadMap.get(quadDir));
        }
        else if ((quadDir == Direction.UP && top) || (quadDir == Direction.DOWN && !top))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), right ? 0 : 1, right ? 1 : 0))
                    .export(quadMap.get(quadDir));
        }
        else if ((quadDir == dir.getCounterClockWise() && right) || (quadDir == dir.getClockWise() && !right))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), top ? 1 : 0, top ? 0 : 1))
                    .apply(Modifiers.makeHorizontalSlope(!right, 45))
                    .export(quadMap.get(null));
        }
        else if (quadDir == dir.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(!top, right ? 0 : 1, right ? 1 : 0))
                    .apply(Modifiers.makeVerticalSlope(!top, 45))
                    .export(quadMap.get(null));
        }
    }

    private void createVerticalCornerSlope(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir)) { return; }

        boolean top = type.isTop();
        Direction cutDir = quadDir.getAxis() == dir.getAxis() ? dir.getClockWise() : dir.getOpposite();
        boolean slope = quadDir == dir.getOpposite() || quadDir == dir.getClockWise();

        QuadModifier.geometry(quad)
                .apply(Modifiers.cutSideLeftRight(cutDir, top ? 1 : 0, top ? 0 : 1))
                .applyIf(Modifiers.makeVerticalSlope(!top, 45), slope)
                .export(quadMap.get(slope ? null : quadDir));
    }



    public static BlockState itemSource()
    {
        return FBContent.blockFramedCornerSlope.get().defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}