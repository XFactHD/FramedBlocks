package io.github.xfacthd.framedblocks.common.data.shapes.slopepanel;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.block.slopepanel.SlopePanelShape;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class FlatSlopePanelCornerShapes implements ShapeGenerator
{
    public static final ShapeCache<ShapeKey> SHAPES = makeCache(SlopePanelShapes.SHAPES, BooleanOp.AND);
    public static final ShapeCache<ShapeKey> OCCLUSION_SHAPES = makeCache(SlopePanelShapes.OCCLUSION_SHAPES, BooleanOp.AND);
    public static final ShapeCache<ShapeKey> INNER_SHAPES = makeCache(SlopePanelShapes.SHAPES, BooleanOp.OR);
    public static final ShapeCache<ShapeKey> INNER_OCCLUSION_SHAPES = makeCache(SlopePanelShapes.OCCLUSION_SHAPES, BooleanOp.OR);
    public static final FlatSlopePanelCornerShapes OUTER = new FlatSlopePanelCornerShapes(SHAPES, OCCLUSION_SHAPES);
    public static final FlatSlopePanelCornerShapes INNER = new FlatSlopePanelCornerShapes(INNER_SHAPES, INNER_OCCLUSION_SHAPES);

    private final ShapeCache<ShapeKey> shapes;
    private final ShapeCache<ShapeKey> occlusionShapes;

    private FlatSlopePanelCornerShapes(ShapeCache<ShapeKey> shapes, ShapeCache<ShapeKey> occlusionShapes)
    {
        this.shapes = shapes;
        this.occlusionShapes = occlusionShapes;
    }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states)
    {
        return generate(states, shapes);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states)
    {
        return generate(states, occlusionShapes);
    }

    private static ShapeContainer generate(List<BlockState> states, ShapeCache<ShapeKey> cache)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        int maskFront = 0b100;
        VoxelShape[] shapes = new VoxelShape[4 * 4 * 2];
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape preShape = cache.get(new ShapeKey(rot, false));
            VoxelShape preShapeFront = cache.get(new ShapeKey(rot, true));

            ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, shapes, rot.ordinal() << 3);
            ShapeUtils.makeHorizontalRotations(preShapeFront, Direction.NORTH, shapes, maskFront | (rot.ordinal() << 3));
        }

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            int front = state.getValue(PropertyHolder.FRONT) ? maskFront : 0;
            int idx = dir.get2DDataValue() | front | (rot.ordinal() << 3);
            map.put(state, shapes[idx]);
        }

        return ShapeContainer.of(map);
    }

    private static ShapeCache<ShapeKey> makeCache(ShapeCache<SlopePanelShape> cache, BooleanOp joinOp)
    {
        return ShapeCache.create(map ->
        {
            for (HorizontalRotation rot : HorizontalRotation.values())
            {
                VoxelShape preShape = Shapes.joinUnoptimized(
                        cache.get(SlopePanelShape.get(rot, false)),
                        cache.get(SlopePanelShape.get(rot.rotate(Rotation.COUNTERCLOCKWISE_90), false)),
                        joinOp
                );
                map.put(new ShapeKey(rot, false), preShape);
                map.put(new ShapeKey(rot, true), preShape.move(0, 0, .5));
            }
        });
    }

    public record ShapeKey(HorizontalRotation rot, boolean front) { }
}
