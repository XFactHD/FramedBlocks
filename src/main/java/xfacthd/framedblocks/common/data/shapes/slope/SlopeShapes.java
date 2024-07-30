package xfacthd.framedblocks.common.data.shapes.slope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.block.ISlopeBlock;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

import java.util.function.Supplier;

public final class SlopeShapes implements SplitShapeGenerator
{
    public static final SlopeShapes INSTANCE = new SlopeShapes();
    public static final ShapeCache<SlopeType> SHAPES = makeCache(() -> ShapeUtils.orUnoptimized(
            Block.box(0,  0, 0, 16,  4, 16),
            Block.box(0,  4, 0, 16,  8, 12),
            Block.box(0,  8, 0, 16, 12,  8),
            Block.box(0, 12, 0, 16, 16,  4)
    ));
    public static final ShapeCache<SlopeType> OCCLUSION_SHAPES = makeCache(() -> ShapeUtils.orUnoptimized(
            Block.box(0,    0, 0, 16,   .5,   16),
            Block.box(0,   .5, 0, 16,    4, 15.5),
            Block.box(0,    4, 0, 16,    8,   12),
            Block.box(0,    8, 0, 16,   12,    8),
            Block.box(0,   12, 0, 16, 15.5,    4),
            Block.box(0, 15.5, 0, 16,   16,   .5)
    ));
    private static final ShapeCache<ShapeKey> FINAL_SHAPES = ShapeCache.create(map ->
    {
        for (SlopeType type : SlopeType.values())
        {
            ShapeUtils.makeHorizontalRotations(SHAPES.get(type), Direction.NORTH, map, type, ShapeKey::new);
        }
    });
    private static final ShapeCache<ShapeKey> FINAL_OCCLUSION_SHAPES = ShapeCache.create(map ->
    {
        for (SlopeType type : SlopeType.values())
        {
            ShapeUtils.makeHorizontalRotations(OCCLUSION_SHAPES.get(type), Direction.NORTH, map, type, ShapeKey::new);
        }
    });

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, FINAL_SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, FINAL_OCCLUSION_SHAPES);
    }

    private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<ShapeKey> shapes)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            ISlopeBlock block = (ISlopeBlock) state.getBlock();
            SlopeType type = block.getSlopeType(state);
            Direction dir = block.getFacing(state);
            builder.put(state, shapes.get(new ShapeKey(dir, type)));
        }

        return ShapeProvider.of(builder.build());
    }

    private static ShapeCache<SlopeType> makeCache(Supplier<VoxelShape> bottomShapeFactory)
    {
        return ShapeCache.createEnum(SlopeType.class, map ->
        {
            VoxelShape bottomShape = bottomShapeFactory.get();
            map.put(SlopeType.BOTTOM, bottomShape);
            map.put(SlopeType.TOP, ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.DOWN, Direction.UP, bottomShape));
            map.put(SlopeType.HORIZONTAL, ShapeUtils.rotateShapeUnoptimizedAroundZ(Direction.DOWN, Direction.WEST, bottomShape));
        });
    }



    private record ShapeKey(Direction dir, SlopeType type) { }
}
