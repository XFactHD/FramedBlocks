package io.github.xfacthd.framedblocks.common.data.shapes.slopeedge;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class SlopeEdgeShapes implements ShapeGenerator
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
    public ShapeContainer generatePrimary(List<BlockState> states)
    {
        return generate(states, SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states)
    {
        return generate(states, OCCLUSION_SHAPES);
    }

    private static ShapeContainer generate(List<BlockState> states, ShapeCache<ShapeKey> cache)
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

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states)
        {
            SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean altType = state.getValue(PropertyHolder.ALT_TYPE);
            map.put(state, shapes[makeShapeIndex(dir, type, altType)]);
        }

        return ShapeContainer.of(map);
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
