package xfacthd.framedblocks.common.data.shapes.slopepanelcorner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public final class InverseDoubleCornerSlopePanelWallShapes implements SplitShapeGenerator
{
    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, CornerSlopePanelWallShapes.SHAPES_LARGE, CornerSlopePanelWallShapes.SHAPES_SMALL_INNER);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, CornerSlopePanelWallShapes.OCCLUSION_SHAPES_LARGE, CornerSlopePanelWallShapes.OCCLUSION_SHAPES_SMALL_INNER);
    }

    private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<HorizontalRotation> cache, ShapeCache<HorizontalRotation> innerCache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = new VoxelShape[4 * 4];
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            HorizontalRotation backRot = rot.rotate(rot.isVertical() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);
            VoxelShape preShape = ShapeUtils.orUnoptimized(
                    cache.get(rot),
                    ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.SOUTH, innerCache.get(backRot))
            );
            ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, shapes, rot.ordinal() << 2);
        }

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            int idx = dir.get2DDataValue() | (rot.ordinal() << 2);
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }
}
