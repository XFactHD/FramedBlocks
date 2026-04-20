package io.github.xfacthd.framedblocks.api.shapes;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.Map;

public final class CommonShapes {
    public record DirBoolKey(Direction dir, boolean top) { }

    public static final ShapeCache<Boolean> SLAB = ShapeCache.createIdentity(map -> {
        map.put(Boolean.FALSE, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D));
        map.put(Boolean.TRUE,  Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D));
    });
    public static final ShapeCache<Direction> PANEL = ShapeCache.createEnum(Direction.class, map -> {
        VoxelShape shape = Block.box(0, 0, 0, 16, 16, 8);
        ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map);
    });
    public static final ShapeCache<DirBoolKey> SLAB_EDGE = ShapeCache.create(map -> {
        VoxelShape shapeBot = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
        VoxelShape shapeTop = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
        ShapeUtils.makeHorizontalRotationsWithFlag(shapeBot, shapeTop, Direction.NORTH, map, DirBoolKey::new);
    });
    public static final ShapeCache<Direction> CORNER_PILLAR = ShapeCache.createEnum(Direction.class, map -> {
        VoxelShape shape = Block.box(0, 0, 0, 8, 16, 8);
        ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map);
    });
    public static final ShapeCache<DirBoolKey> STRAIGHT_STAIRS = ShapeCache.create(map -> {
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
    public static final ShapeCache<Direction> STRAIGHT_VERTICAL_STAIRS = ShapeCache.createEnum(Direction.class, map -> {
        VoxelShape shape = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 8, 16, 16, 16),
                Block.box(8, 0, 0, 16, 16, 8)
        );
        ShapeUtils.makeHorizontalRotations(shape, Direction.SOUTH, map);
    });

    public static final ShapeGenerator SLAB_GENERATOR = createSlabGenerator(FramedProperties.TOP);
    public static final ShapeGenerator PANEL_GENERATOR = createPanelGenerator(FramedProperties.FACING_HOR);

    public static ShapeGenerator createSlabGenerator(BooleanProperty topProp) {
        return states -> {
            Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

            for (BlockState state : states) {
                map.put(state, SLAB.get(state.getValue(topProp)));
            }

            return ShapeContainer.of(map);
        };
    }

    public static ShapeGenerator createPanelGenerator(EnumProperty<Direction> dirProp) {
        return states -> {
            Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

            for (BlockState state : states) {
                Direction dir = state.getValue(dirProp);
                map.put(state, PANEL.get(dir));
            }

            return ShapeContainer.of(map);
        };
    }

    public static ShapeGenerator createPanelGenerator(EnumProperty<Direction> dirProp, BooleanProperty invProp) {
        return states -> {
            Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

            for (BlockState state : states) {
                Direction dir = state.getValue(dirProp);
                if (state.getValue(invProp)) {
                    dir = dir.getOpposite();
                }
                map.put(state, PANEL.get(dir));
            }

            return ShapeContainer.of(map);
        };
    }

    private CommonShapes() { }
}
