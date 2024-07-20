package xfacthd.framedblocks.common.data.shapes.slope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public final class ThreewayCornerShapes implements SplitShapeGenerator
{
    public static final ThreewayCornerShapes OUTER = new ThreewayCornerShapes(BooleanOp.AND);
    public static final ThreewayCornerShapes INNER = new ThreewayCornerShapes(BooleanOp.OR);

    private final BooleanOp joinOp;

    private ThreewayCornerShapes(BooleanOp joinOp)
    {
        this.joinOp = joinOp;
    }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, SlopeShapes.SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, SlopeShapes.OCCLUSION_SHAPES);
    }

    private ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<SlopeType> shapeCache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeTop = Shapes.joinUnoptimized(
                Shapes.joinUnoptimized(
                        shapeCache.get(SlopeType.TOP),
                        shapeCache.get(SlopeType.HORIZONTAL),
                        joinOp
                ),
                ShapeUtils.rotateShapeUnoptimizedAroundY(
                        Direction.NORTH, Direction.WEST, shapeCache.get(SlopeType.TOP)
                ),
                joinOp
        );

        VoxelShape shapeBottom = Shapes.joinUnoptimized(
                Shapes.joinUnoptimized(
                        shapeCache.get(SlopeType.BOTTOM),
                        shapeCache.get(SlopeType.HORIZONTAL),
                        joinOp
                ),
                ShapeUtils.rotateShapeUnoptimizedAroundY(
                        Direction.NORTH, Direction.WEST, shapeCache.get(SlopeType.BOTTOM)
                ),
                joinOp
        );

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(shapeBottom, shapeTop, Direction.NORTH);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }
}
