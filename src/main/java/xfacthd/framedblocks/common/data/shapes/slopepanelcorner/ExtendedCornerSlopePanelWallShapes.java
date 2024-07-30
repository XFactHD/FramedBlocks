package xfacthd.framedblocks.common.data.shapes.slopepanelcorner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;
import xfacthd.framedblocks.common.data.shapes.slopepanel.ExtendedSlopePanelShapes;
import xfacthd.framedblocks.common.data.shapes.slopeslab.ElevatedSlopeSlabShapes;

public final class ExtendedCornerSlopePanelWallShapes implements SplitShapeGenerator
{
    public static final ShapeCache<HorizontalRotation> SHAPES = makeCache(ElevatedSlopeSlabShapes.SHAPES, ExtendedSlopePanelShapes.SHAPES, false);
    public static final ShapeCache<HorizontalRotation> OCCLUSION_SHAPES = makeCache(ElevatedSlopeSlabShapes.OCCLUSION_SHAPES, ExtendedSlopePanelShapes.OCCLUSION_SHAPES, false);
    public static final ShapeCache<HorizontalRotation> INNER_SHAPES = makeCache(ElevatedSlopeSlabShapes.SHAPES, ExtendedSlopePanelShapes.SHAPES, true);
    public static final ShapeCache<HorizontalRotation> INNER_OCCLUSION_SHAPES = makeCache(ElevatedSlopeSlabShapes.OCCLUSION_SHAPES, ExtendedSlopePanelShapes.OCCLUSION_SHAPES, true);
    private static final ShapeCache<ShapeKey> FINAL_SHAPES = makeFinalCache(SHAPES);
    private static final ShapeCache<ShapeKey> FINAL_OCCLUSION_SHAPES = makeFinalCache(OCCLUSION_SHAPES);
    private static final ShapeCache<ShapeKey> FINAL_INNER_SHAPES = makeFinalCache(INNER_SHAPES);
    private static final ShapeCache<ShapeKey> FINAL_INNER_OCCLUSION_SHAPES = makeFinalCache(INNER_OCCLUSION_SHAPES);
    public static final ExtendedCornerSlopePanelWallShapes OUTER = new ExtendedCornerSlopePanelWallShapes(FINAL_SHAPES, FINAL_OCCLUSION_SHAPES);
    public static final ExtendedCornerSlopePanelWallShapes INNER = new ExtendedCornerSlopePanelWallShapes(FINAL_INNER_SHAPES, FINAL_INNER_OCCLUSION_SHAPES);

    private final ShapeCache<ShapeKey> shapes;
    private final ShapeCache<ShapeKey> occlusionShapes;

    private ExtendedCornerSlopePanelWallShapes(ShapeCache<ShapeKey> shapes, ShapeCache<ShapeKey> occlusionShapes)
    {
        this.shapes = shapes;
        this.occlusionShapes = occlusionShapes;
    }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, shapes);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, occlusionShapes);
    }

    private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<ShapeKey> cache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            builder.put(state, cache.get(new ShapeKey(dir, rot)));
        }

        return ShapeProvider.of(builder.build());
    }

    private static ShapeCache<HorizontalRotation> makeCache(
            ShapeCache<Boolean> slabCache, ShapeCache<HorizontalRotation> panelCache, boolean inner
    )
    {
        return ShapeCache.createEnum(HorizontalRotation.class, map ->
        {
            VoxelShape leftPanel = panelCache.get(HorizontalRotation.LEFT);
            VoxelShape rightPanel = panelCache.get(HorizontalRotation.RIGHT);
            VoxelShape shapeOneUpLeft = ShapeUtils.rotateShapeUnoptimized(
                    Direction.NORTH, inner ? Direction.EAST : Direction.WEST, inner ? rightPanel : leftPanel
            );
            VoxelShape shapeOneDownRight = ShapeUtils.rotateShapeUnoptimized(
                    Direction.NORTH, inner ? Direction.WEST : Direction.EAST, inner ? leftPanel : rightPanel
            );

            for (HorizontalRotation rot : HorizontalRotation.values())
            {
                VoxelShape shapeOne = switch (rot)
                {
                    case UP, LEFT -> shapeOneUpLeft;
                    case DOWN, RIGHT -> shapeOneDownRight;
                };
                VoxelShape shapeTwo = switch (rot)
                {
                    case UP, RIGHT ->  slabCache.get(!inner);
                    case DOWN, LEFT -> slabCache.get(inner);
                };
                map.put(rot, Shapes.joinUnoptimized(shapeOne, shapeTwo, inner ? BooleanOp.OR : BooleanOp.AND));
            }
        });
    }

    private static ShapeCache<ShapeKey> makeFinalCache(ShapeCache<HorizontalRotation> cache)
    {
        return ShapeCache.create(map ->
        {
            for (HorizontalRotation rot : HorizontalRotation.values())
            {
                ShapeUtils.makeHorizontalRotations(cache.get(rot), Direction.NORTH, map, rot, ShapeKey::new);
            }
        });
    }



    private record ShapeKey(Direction dir, HorizontalRotation rot) { }
}
