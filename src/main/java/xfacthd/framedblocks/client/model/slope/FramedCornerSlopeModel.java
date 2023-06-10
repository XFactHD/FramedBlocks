package xfacthd.framedblocks.client.model.slope;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

import java.util.List;
import java.util.Map;

public class FramedCornerSlopeModel extends FramedBlockModel
{
    private final Direction dir;
    private final CornerType type;
    private final boolean ySlope;

    public FramedCornerSlopeModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.dir = state.getValue(FramedProperties.FACING_HOR);
        this.type = state.getValue(PropertyHolder.CORNER_TYPE);
        this.ySlope = state.getValue(FramedProperties.Y_SLOPE);
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
        else if (!ySlope && quadDir == dir.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(!top, right ? 0 : 1, right ? 1 : 0))
                    .apply(Modifiers.makeVerticalSlope(!top, 45))
                    .export(quadMap.get(null));
        }
        else if (ySlope && ((!top && quadDir == Direction.UP) || (top && quadDir == Direction.DOWN)))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), right ? 0 : 1, right ? 1 : 0))
                    .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                    .export(quadMap.get(null));
        }
    }

    private void createVerticalCornerSlope(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        boolean yQuad = Utils.isY(quadDir);
        if (!ySlope && yQuad)
        {
            return;
        }

        boolean top = type.isTop();
        Direction cutDir = quadDir.getAxis() == dir.getAxis() ? dir.getClockWise() : dir.getOpposite();
        boolean slope = quadDir == dir.getOpposite() || quadDir == dir.getClockWise();

        if ((!slope && !yQuad) || !ySlope)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, top ? 1 : 0, top ? 0 : 1))
                    .applyIf(Modifiers.makeVerticalSlope(!top, 45), slope)
                    .export(quadMap.get(slope ? null : quadDir));
        }
        else if (yQuad)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), 0, 1))
                    .apply(Modifiers.makeVerticalSlope(dir.getClockWise(), 45))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), 1, 0))
                    .apply(Modifiers.makeVerticalSlope(dir.getOpposite(), 45))
                    .export(quadMap.get(null));
        }
    }



    public static BlockState itemSource()
    {
        return FBContent.BLOCK_FRAMED_CORNER_SLOPE.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}