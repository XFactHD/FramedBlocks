package xfacthd.framedblocks.common.data.shapes.slope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public final class HalfSlopeShapes implements SplitShapeGenerator
{
    public static final ShapeCache<ShapeKey> SHAPES = ShapeCache.create(map ->
    {
        map.put(new ShapeKey(false, false), ShapeUtils.andUnoptimized(
                SlopeShapes.SHAPES.get(SlopeType.BOTTOM),
                CommonShapes.PANEL.get(Direction.WEST)
        ));
        map.put(new ShapeKey(false, true), ShapeUtils.andUnoptimized(
                SlopeShapes.SHAPES.get(SlopeType.BOTTOM),
                CommonShapes.PANEL.get(Direction.EAST)
        ));
        map.put(new ShapeKey(true, false), ShapeUtils.andUnoptimized(
                SlopeShapes.SHAPES.get(SlopeType.TOP),
                CommonShapes.PANEL.get(Direction.WEST)
        ));
        map.put(new ShapeKey(true, true), ShapeUtils.andUnoptimized(
                SlopeShapes.SHAPES.get(SlopeType.TOP),
                CommonShapes.PANEL.get(Direction.EAST)
        ));
    });
    public static final ShapeCache<ShapeKey> OCCLUSION_SHAPES = ShapeCache.create(map ->
    {
        map.put(new ShapeKey(false, false), ShapeUtils.andUnoptimized(
                SlopeShapes.OCCLUSION_SHAPES.get(SlopeType.BOTTOM),
                CommonShapes.PANEL.get(Direction.WEST)
        ));
        map.put(new ShapeKey(false, true), ShapeUtils.andUnoptimized(
                SlopeShapes.OCCLUSION_SHAPES.get(SlopeType.BOTTOM),
                CommonShapes.PANEL.get(Direction.EAST)
        ));
        map.put(new ShapeKey(true, false), ShapeUtils.andUnoptimized(
                SlopeShapes.OCCLUSION_SHAPES.get(SlopeType.TOP),
                CommonShapes.PANEL.get(Direction.WEST)
        ));
        map.put(new ShapeKey(true, true), ShapeUtils.andUnoptimized(
                SlopeShapes.OCCLUSION_SHAPES.get(SlopeType.TOP),
                CommonShapes.PANEL.get(Direction.EAST)
        ));
    });

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generateShapes(states, SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generateShapes(states, OCCLUSION_SHAPES);
    }

    private static ShapeProvider generateShapes(ImmutableList<BlockState> states, ShapeCache<ShapeKey> shapeCache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        int maskTop = 0b0100;
        int maskRight = 0b1000;
        VoxelShape[] shapes = new VoxelShape[4 * 4];
        for (int i = 0; i < 4; i++)
        {
            ShapeUtils.makeHorizontalRotations(
                    shapeCache.get(new ShapeKey((i & 0b01) != 0, (i & 0b10) != 0)),
                    Direction.NORTH,
                    shapes,
                    i << 2
            );
        }

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            int top = state.getValue(FramedProperties.TOP) ? maskTop : 0;
            int right = state.getValue(PropertyHolder.RIGHT) ? maskRight : 0;
            int idx = dir.get2DDataValue() | (top | right);
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }



    public record ShapeKey(boolean top, boolean right) { }
}
