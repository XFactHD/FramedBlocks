package xfacthd.framedblocks.common.data.shapes.slopepanel;

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

public final class FlatInverseDoubleSlopePanelCornerShapes implements SplitShapeGenerator
{
    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, FlatSlopePanelCornerShapes.SHAPES, FlatSlopePanelCornerShapes.INNER_SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, FlatSlopePanelCornerShapes.OCCLUSION_SHAPES, FlatSlopePanelCornerShapes.INNER_OCCLUSION_SHAPES);
    }

    private static ShapeProvider generate(
            ImmutableList<BlockState> states,
            ShapeCache<FlatSlopePanelCornerShapes.ShapeKey> cache,
            ShapeCache<FlatSlopePanelCornerShapes.ShapeKey> innerCache
    )
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = new VoxelShape[4 * 4];
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape frontShape = cache.get(new FlatSlopePanelCornerShapes.ShapeKey(rot.getOpposite(), true));
            HorizontalRotation backRot = rot.rotate(rot.isVertical() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);
            VoxelShape backShape = innerCache.get(new FlatSlopePanelCornerShapes.ShapeKey(backRot, true));
            backShape = ShapeUtils.rotateShapeUnoptimizedAroundY(Direction.NORTH, Direction.SOUTH, backShape);

            VoxelShape preShape = ShapeUtils.orUnoptimized(frontShape, backShape);
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
