package xfacthd.framedblocks.common.data.shapes.slopeslab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.block.slopeslab.SlopeSlabShape;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public final class FlatInverseDoubleSlopeSlabCornerShapes implements SplitShapeGenerator
{
    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, FlatSlopeSlabCornerShapes.SHAPES, FlatSlopeSlabCornerShapes.INNER_SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, FlatSlopeSlabCornerShapes.OCCLUSION_SHAPES, FlatSlopeSlabCornerShapes.INNER_OCCLUSION_SHAPES);
    }

    private static ShapeProvider generate(
            ImmutableList<BlockState> states, ShapeCache<SlopeSlabShape> cache, ShapeCache<SlopeSlabShape> innerCache
    )
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeBot = ShapeUtils.orUnoptimized(
                cache.get(SlopeSlabShape.BOTTOM_TOP_HALF),
                ShapeUtils.rotateShapeUnoptimizedAroundY(
                        Direction.NORTH, Direction.SOUTH, innerCache.get(SlopeSlabShape.TOP_BOTTOM_HALF)
                )
        );
        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                cache.get(SlopeSlabShape.TOP_BOTTOM_HALF),
                ShapeUtils.rotateShapeUnoptimizedAroundY(
                        Direction.NORTH, Direction.SOUTH, innerCache.get(SlopeSlabShape.BOTTOM_TOP_HALF)
                )
        );

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(shapeBot, shapeTop, Direction.NORTH);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }
}
