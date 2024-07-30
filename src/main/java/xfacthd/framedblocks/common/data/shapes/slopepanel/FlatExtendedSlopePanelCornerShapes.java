package xfacthd.framedblocks.common.data.shapes.slopepanel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public final class FlatExtendedSlopePanelCornerShapes implements SplitShapeGenerator
{
    private static final ShapeCache<ShapeKey> FINAL_SHAPES = makeCache(ExtendedSlopePanelShapes.SHAPES, BooleanOp.AND);
    private static final ShapeCache<ShapeKey> FINAL_OCCLUSION_SHAPES = makeCache(ExtendedSlopePanelShapes.OCCLUSION_SHAPES, BooleanOp.AND);
    private static final ShapeCache<ShapeKey> FINAL_INNER_SHAPES = makeCache(ExtendedSlopePanelShapes.SHAPES, BooleanOp.OR);
    private static final ShapeCache<ShapeKey> FINAL_INNER_OCCLUSION_SHAPES = makeCache(ExtendedSlopePanelShapes.OCCLUSION_SHAPES, BooleanOp.OR);
    public static final FlatExtendedSlopePanelCornerShapes OUTER = new FlatExtendedSlopePanelCornerShapes(FINAL_SHAPES, FINAL_OCCLUSION_SHAPES);
    public static final FlatExtendedSlopePanelCornerShapes INNER = new FlatExtendedSlopePanelCornerShapes(FINAL_INNER_SHAPES, FINAL_INNER_OCCLUSION_SHAPES);

    private final ShapeCache<ShapeKey> shapes;
    private final ShapeCache<ShapeKey> occlusionShapes;

    private FlatExtendedSlopePanelCornerShapes(ShapeCache<ShapeKey> shapes, ShapeCache<ShapeKey> occlusionShapes)
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

    private static ShapeCache<ShapeKey> makeCache(ShapeCache<HorizontalRotation> cache, BooleanOp joinOp)
    {
        return ShapeCache.create(map ->
        {
            for (HorizontalRotation rot : HorizontalRotation.values())
            {
                VoxelShape shape = Shapes.joinUnoptimized(
                        cache.get(rot), cache.get(rot.rotate(Rotation.COUNTERCLOCKWISE_90)), joinOp
                );
                ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map, rot, ShapeKey::new);
            }
        });
    }



    private record ShapeKey(Direction dir, HorizontalRotation rot) { }
}
