package xfacthd.framedblocks.common.data.shapes.slope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public final class VerticalHalfSlopeShapes implements SplitShapeGenerator
{
    public static final ShapeCache<Boolean> SHAPES = ShapeCache.createIdentity(map ->
    {
        map.put(Boolean.FALSE, ShapeUtils.andUnoptimized(
                SlopeShapes.SHAPES.get(SlopeType.HORIZONTAL),
                CommonShapes.SLAB.get(Boolean.FALSE)
        ));
        map.put(Boolean.TRUE, ShapeUtils.andUnoptimized(
                SlopeShapes.SHAPES.get(SlopeType.HORIZONTAL),
                CommonShapes.SLAB.get(Boolean.TRUE)
        ));
    });
    public static final ShapeCache<Boolean> OCCLUSION_SHAPES = ShapeCache.createIdentity(map ->
    {
        map.put(Boolean.FALSE, ShapeUtils.andUnoptimized(
                SlopeShapes.OCCLUSION_SHAPES.get(SlopeType.HORIZONTAL),
                CommonShapes.SLAB.get(Boolean.FALSE)
        ));
        map.put(Boolean.TRUE, ShapeUtils.andUnoptimized(
                SlopeShapes.OCCLUSION_SHAPES.get(SlopeType.HORIZONTAL),
                CommonShapes.SLAB.get(Boolean.TRUE)
        ));
    });

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generateShapes(states, SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generateShapes(states, OCCLUSION_SHAPES);
    }

    private static ShapeProvider generateShapes(ImmutableList<BlockState> states, ShapeCache<Boolean> shapeCache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(
                shapeCache.get(Boolean.FALSE), shapeCache.get(Boolean.TRUE), Direction.NORTH
        );

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }
}
