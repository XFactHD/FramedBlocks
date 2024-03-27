package xfacthd.framedblocks.api.shapes;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;

public final class CommonShapes
{
    public record DirBoolKey(Direction dir, boolean top) { }

    public static final ShapeCache<Boolean> SLAB = ShapeCache.createIdentity(map ->
    {
        map.put(Boolean.FALSE, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D));
        map.put(Boolean.TRUE,  Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D));
    });
    public static final ShapeCache<Direction> PANEL = ShapeCache.createEnum(Direction.class, map ->
    {
        VoxelShape shape = Block.box(0, 0, 0, 16, 16, 8);
        ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map);
    });
    public static final ShapeCache<DirBoolKey> SLAB_EDGE = ShapeCache.create(map ->
    {
        VoxelShape shapeBot = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
        VoxelShape shapeTop = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
        ShapeUtils.makeHorizontalRotationsWithFlag(shapeBot, shapeTop, Direction.NORTH, map, DirBoolKey::new);
    });
    public static final ShapeCache<Direction> CORNER_PILLAR = ShapeCache.createEnum(Direction.class, map ->
    {
        VoxelShape shape = Block.box(0, 0, 0, 8, 16, 8);
        ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map);
    });
    public static final ShapeCache<DirBoolKey> STRAIGHT_STAIRS = ShapeCache.create(map ->
    {
        VoxelShape shapeBottom = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 0, 16, 8, 16),
                Block.box(0, 8, 8, 16, 16, 16)
        );
        VoxelShape shapeTop = ShapeUtils.orUnoptimized(
                Block.box(0, 8, 0, 16, 16, 16),
                Block.box(0, 0, 8, 16, 8, 16)
        );
        ShapeUtils.makeHorizontalRotationsWithFlag(shapeBottom, shapeTop, Direction.SOUTH, map, DirBoolKey::new);
    });
    public static final ShapeCache<Direction> STRAIGHT_VERTICAL_STAIRS = ShapeCache.createEnum(Direction.class, map ->
    {
        VoxelShape shape = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 8, 16, 16, 16),
                Block.box(8, 0, 0, 16, 16, 8)
        );
        ShapeUtils.makeHorizontalRotations(shape, Direction.SOUTH, map);
    });

    public static final ShapeGenerator SLAB_GENERATOR = createSlabGenerator(FramedProperties.TOP);
    public static final ShapeGenerator PANEL_GENERATOR = createPanelGenerator(FramedProperties.FACING_HOR);

    public static ShapeGenerator createSlabGenerator(BooleanProperty topProp)
    {
        return states ->
        {
            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

            for (BlockState state : states)
            {
                builder.put(state, SLAB.get(state.getValue(topProp)));
            }

            return ShapeProvider.of(builder.build());
        };
    }

    public static ShapeGenerator createPanelGenerator(DirectionProperty dirProp)
    {
        return states ->
        {
            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

            for (BlockState state : states)
            {
                Direction dir = state.getValue(dirProp);
                builder.put(state, PANEL.get(dir));
            }

            return ShapeProvider.of(builder.build());
        };
    }

    public static ShapeGenerator createPanelGenerator(DirectionProperty dirProp, BooleanProperty invProp)
    {
        return states ->
        {
            ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

            for (BlockState state : states)
            {
                Direction dir = state.getValue(dirProp);
                if (state.getValue(invProp))
                {
                    dir = dir.getOpposite();
                }
                builder.put(state, PANEL.get(dir));
            }

            return ShapeProvider.of(builder.build());
        };
    }



    private CommonShapes() { }
}
