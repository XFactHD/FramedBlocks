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

/// Holds caches and generators for various common voxel shapes.
public final class CommonShapes {
    /// Map key for shapes based on a direction and a "top" flag.
    public record DirBoolKey(Direction dir, boolean top) { }

    /// Slab shapes, indexed by a boolean indicating top half (true) or bottom half (false).
    public static final ShapeCache<Boolean> SLAB = ShapeCache.createIdentity(map -> {
        map.put(Boolean.FALSE, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D));
        map.put(Boolean.TRUE,  Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D));
    });
    /// "Vertical" slab shapes, indexed by the direction towards the outer face.
    public static final ShapeCache<Direction> PANEL = ShapeCache.createEnum(Direction.class, map -> {
        VoxelShape shape = Block.box(0, 0, 0, 16, 16, 8);
        ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map);
    });
    /// Slab edge (half slab) shapes, indexed by the direction towards the outer face and a boolean indicating top half (true) or bottom half (false).
    public static final ShapeCache<DirBoolKey> SLAB_EDGE = ShapeCache.create(map -> {
        VoxelShape shapeBot = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
        VoxelShape shapeTop = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
        ShapeUtils.makeHorizontalRotationsWithFlag(shapeBot, shapeTop, Direction.NORTH, map, DirBoolKey::new);
    });
    /// Corner pillar (half panel) shapes, indexed by the direction towards the first outer face and whose CCW rotation points towards the second outer face.
    public static final ShapeCache<Direction> CORNER_PILLAR = ShapeCache.createEnum(Direction.class, map -> {
        VoxelShape shape = Block.box(0, 0, 0, 8, 16, 8);
        ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map);
    });
    /// Straight stairs shapes, indexed by the direction towards the outer face and a boolean indicating top half (true) or bottom half (false).
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
    /// Straight vertical stairs shapes, indexed by the direction towards the first outer face and whose CCW rotation points towards the second outer face.
    public static final ShapeCache<Direction> STRAIGHT_VERTICAL_STAIRS = ShapeCache.createEnum(Direction.class, map -> {
        VoxelShape shape = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 8, 16, 16, 16),
                Block.box(8, 0, 0, 16, 16, 8)
        );
        ShapeUtils.makeHorizontalRotations(shape, Direction.SOUTH, map);
    });

    /// Shape generator for slab-like blocks using [FramedProperties#TOP] to indicate the half.
    public static final ShapeGenerator SLAB_GENERATOR = createSlabGenerator(FramedProperties.TOP);
    /// Shape generator for panel-like blocks using [FramedProperties#FACING_HOR] to indicate their horizontal orientation.
    public static final ShapeGenerator PANEL_GENERATOR = createPanelGenerator(FramedProperties.FACING_HOR);

    /// Create a shape generator for a slab-like block using the given property to indicate the half.
    ///
    /// @param topProp The property indicating the block half
    /// @return the shape generator
    public static ShapeGenerator createSlabGenerator(BooleanProperty topProp) {
        return states -> {
            Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

            for (BlockState state : states) {
                map.put(state, SLAB.get(state.getValue(topProp)));
            }

            return ShapeContainer.of(map);
        };
    }

    /// Create a shape generator for a panel-like block using the given property to indicate the horizontal orientation.
    ///
    /// @param dirProp The property indicating the horizontal orientation
    /// @return the shape generator
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

    /// Create a shape generator for a panel-like block using the given direction property to indicate the horizontal
    /// orientation and the given boolean property to indicate whether said orientation needs to be inverted.
    ///
    /// @param dirProp The property indicating the horizontal orientation
    /// @param invProp The property indicating whether the orientation needs to be inverted
    /// @return the shape generator
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
