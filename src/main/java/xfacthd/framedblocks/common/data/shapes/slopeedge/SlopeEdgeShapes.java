package xfacthd.framedblocks.common.data.shapes.slopeedge;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

import java.util.function.Supplier;

public final class SlopeEdgeShapes implements SplitShapeGenerator
{
    public static final ShapeCache<ShapeKey> SHAPES = makeCache(() -> ShapeUtils.orUnoptimized(
            Block.box(0, 0, 0, 16, 4, 8),
            Block.box(0, 4, 0, 16, 8, 4)
    ));
    public static final ShapeCache<ShapeKey> OCCLUSION_SHAPES = makeCache(() -> ShapeUtils.orUnoptimized(
            Block.box(0, 0, 0, 16, 0.25, 8),
            Block.box(0, 0.25, 0, 16, 4, 7.75),
            Block.box(0, 4, 0, 16, 7.75, 4),
            Block.box(0, 7.75, 0, 16, 8, 0.25)
    ));

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

    private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<ShapeKey> cache)
    {
        VoxelShape[] shapes = new VoxelShape[3 * 4 * 2];

        for (SlopeType type : SlopeType.values())
        {
            ShapeUtils.makeHorizontalRotations(
                    cache.get(new ShapeKey(type, false)),
                    Direction.NORTH,
                    shapes,
                    type,
                    (dir, keyType) -> makeShapeIndex(dir, keyType, false)
            );
            ShapeUtils.makeHorizontalRotations(
                    cache.get(new ShapeKey(type, true)),
                    Direction.NORTH,
                    shapes,
                    type,
                    (dir, keyType) -> makeShapeIndex(dir, keyType, true)
            );
        }

        ImmutableMap.Builder<BlockState, VoxelShape> builder = new ImmutableMap.Builder<>();

        for (BlockState state : states)
        {
            SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean altType = state.getValue(PropertyHolder.ALT_TYPE);
            builder.put(state, shapes[makeShapeIndex(dir, type, altType)]);
        }

        return ShapeProvider.of(builder.build());
    }

    private static int makeShapeIndex(Direction dir, SlopeType type, boolean altType)
    {
        return (type.ordinal() << 3) | (dir.get2DDataValue() << 1) | (altType ? 1 : 0);
    }

    private static ShapeCache<ShapeKey> makeCache(Supplier<VoxelShape> bottomShape)
    {
        return ShapeCache.create(map ->
        {
            VoxelShape shapeBottom = bottomShape.get();
            map.put(new ShapeKey(SlopeType.BOTTOM, false), shapeBottom);
            map.put(new ShapeKey(SlopeType.BOTTOM, true), shapeBottom.move(0, .5, .5));

            VoxelShape shapeTop = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.DOWN, Direction.UP, shapeBottom);
            map.put(new ShapeKey(SlopeType.TOP, false), shapeTop);
            map.put(new ShapeKey(SlopeType.TOP, true), shapeTop.move(0, -.5, .5));

            VoxelShape shapeHorizontal = ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.DOWN, Direction.WEST, shapeBottom);
            map.put(new ShapeKey(SlopeType.HORIZONTAL, false), shapeHorizontal);
            map.put(new ShapeKey(SlopeType.HORIZONTAL, true), shapeHorizontal.move(.5, 0, .5));
        });
    }



    public record ShapeKey(SlopeType type, boolean altType) { }
}
