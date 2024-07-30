package xfacthd.framedblocks.common.data.shapes.slopeslab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.block.slopeslab.SlopeSlabShape;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public final class ElevatedSlopeSlabShapes implements SplitShapeGenerator
{
    public static final ElevatedSlopeSlabShapes INSTANCE = new ElevatedSlopeSlabShapes();
    public static final ShapeCache<Boolean> SHAPES = ShapeCache.createIdentity(map ->
    {
        map.put(Boolean.FALSE, ShapeUtils.orUnoptimized(
                SlopeSlabShapes.SHAPES.get(SlopeSlabShape.BOTTOM_TOP_HALF),
                Block.box(0, 0, 0, 16, 8, 16)
        ));

        map.put(Boolean.TRUE, ShapeUtils.orUnoptimized(
                SlopeSlabShapes.SHAPES.get(SlopeSlabShape.TOP_BOTTOM_HALF),
                Block.box(0, 8, 0, 16, 16, 16)
        ));
    });
    public static final ShapeCache<Boolean> OCCLUSION_SHAPES = ShapeCache.createIdentity(map ->
    {
        map.put(Boolean.FALSE, ShapeUtils.orUnoptimized(
                SlopeSlabShapes.OCCLUSION_SHAPES.get(SlopeSlabShape.BOTTOM_TOP_HALF),
                Block.box(0, 0, 0, 16, 8, 16)
        ));

        map.put(Boolean.TRUE, ShapeUtils.orUnoptimized(
                SlopeSlabShapes.OCCLUSION_SHAPES.get(SlopeSlabShape.TOP_BOTTOM_HALF),
                Block.box(0, 8, 0, 16, 16, 16)
        ));
    });
    private static final ShapeCache<ShapeKey> FINAL_SHAPES = ShapeCache.create(map ->
            ShapeUtils.makeHorizontalRotationsWithFlag(
                    SHAPES.get(Boolean.FALSE),
                    SHAPES.get(Boolean.TRUE),
                    Direction.NORTH,
                    map,
                    ShapeKey::new
            )
    );
    private static final ShapeCache<ShapeKey> FINAL_OCCLUSION_SHAPES = ShapeCache.create(map ->
            ShapeUtils.makeHorizontalRotationsWithFlag(
                    OCCLUSION_SHAPES.get(Boolean.FALSE),
                    OCCLUSION_SHAPES.get(Boolean.TRUE),
                    Direction.NORTH,
                    map,
                    ShapeKey::new
            )
    );

    private ElevatedSlopeSlabShapes() { }

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

    private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<ShapeKey> cache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, cache.get(new ShapeKey(dir, top)));
        }

        return ShapeProvider.of(builder.build());
    }



    private record ShapeKey(Direction dir, boolean top) { }
}
