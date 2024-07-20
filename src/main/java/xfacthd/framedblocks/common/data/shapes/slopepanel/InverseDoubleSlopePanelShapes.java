package xfacthd.framedblocks.common.data.shapes.slopepanel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.block.slopepanel.SlopePanelShape;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public final class InverseDoubleSlopePanelShapes implements SplitShapeGenerator
{
    public static final InverseDoubleSlopePanelShapes INSTANCE = new InverseDoubleSlopePanelShapes();
    private static final ShapeCache<ShapeKey> SHAPES = makeCache(SlopePanelShapes.SHAPES);
    private static final ShapeCache<ShapeKey> OCCLUSION_SHAPES = makeCache(SlopePanelShapes.OCCLUSION_SHAPES);

    private InverseDoubleSlopePanelShapes() { }

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
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            builder.put(state, cache.get(new ShapeKey(dir, rot)));
        }

        return ShapeProvider.of(builder.build());
    }

    private static ShapeCache<ShapeKey> makeCache(ShapeCache<SlopePanelShape> cache)
    {
        return ShapeCache.create(map ->
        {
            for (HorizontalRotation rot : HorizontalRotation.values())
            {
                HorizontalRotation rotOne = rot.isVertical() ? rot.getOpposite() : rot;
                VoxelShape shapeOne = cache.get(SlopePanelShape.get(rotOne, true));
                VoxelShape preShape = ShapeUtils.orUnoptimized(
                        ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.SOUTH, shapeOne),
                        cache.get(SlopePanelShape.get(rot, true))
                );
                ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, map, rot, ShapeKey::new);
            }
        });
    }



    private record ShapeKey(Direction dir, HorizontalRotation rot) { }
}
