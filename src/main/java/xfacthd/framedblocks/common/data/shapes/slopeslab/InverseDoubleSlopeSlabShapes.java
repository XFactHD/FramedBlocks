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

public final class InverseDoubleSlopeSlabShapes implements SplitShapeGenerator
{
    public static final InverseDoubleSlopeSlabShapes INSTANCE = new InverseDoubleSlopeSlabShapes();
    private static final ShapeCache<Direction> SHAPES = makeCache(SlopeSlabShapes.SHAPES);
    private static final ShapeCache<Direction> OCCLUSION_SHAPES = makeCache(SlopeSlabShapes.OCCLUSION_SHAPES);

    private InverseDoubleSlopeSlabShapes() { }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, OCCLUSION_SHAPES);
    }

    private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<Direction> cache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            builder.put(state, cache.get(dir));
        }

        return ShapeProvider.of(builder.build());
    }

    private static ShapeCache<Direction> makeCache(ShapeCache<SlopeSlabShape> cache)
    {
        return ShapeCache.createEnum(Direction.class, map ->
        {
            VoxelShape shape = ShapeUtils.orUnoptimized(
                    cache.get(SlopeSlabShape.BOTTOM_TOP_HALF),
                    ShapeUtils.rotateShapeUnoptimized(
                            Direction.NORTH, Direction.SOUTH, cache.get(SlopeSlabShape.TOP_BOTTOM_HALF)
                    )
            );
            ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map);
        });
    }
}
