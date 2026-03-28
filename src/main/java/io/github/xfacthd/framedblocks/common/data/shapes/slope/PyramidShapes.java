package io.github.xfacthd.framedblocks.common.data.shapes.slope;

import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.PillarConnection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class PyramidShapes implements ShapeGenerator {
    public static final PyramidShapes FULL = new PyramidShapes(
            () -> ShapeUtils.orUnoptimized(
                    Block.box(0, 0, 8, 16, 16, 16),
                    Block.box(4, 4, 0, 12, 12,  8)
            ),
            () -> ShapeUtils.orUnoptimized(
                    Block.box(   0,    0, 15.5,    16,    16, 16),
                    Block.box( .25,  .25,    8, 15.75, 15.75, 16),
                    Block.box(   4,    4,   .5,    12,    12,  8),
                    Block.box(7.75, 7.75,    0,  8.25,  8.25, .5)
            ),
            () -> ShapeUtils.orUnoptimized(
                    Block.box(  0,   0, 15.5,    16,    16, 16),
                    Block.box(.25, .25,    8, 15.75, 15.75, 16),
                    Block.box(  4,   4,   .5,    12,    12,  8),
                    Block.box(  6,   6,    0,    10,    10, .5)
            ),
            () -> ShapeUtils.orUnoptimized(
                    Block.box(  0,   0, 15.5,    16,    16, 16),
                    Block.box(.25, .25,    8, 15.75, 15.75, 16),
                    Block.box(  4,   4,    0,    12,    12,  8)
            )
    );
    public static final PyramidShapes SLAB = new PyramidShapes(
            () -> ShapeUtils.orUnoptimized(
                    Block.box(0, 0, 12, 16, 16, 16),
                    Block.box(4, 4,  8, 12, 12, 12)
            ),
            () -> ShapeUtils.orUnoptimized(
                    Block.box( 0,  0, 15.5,   16,   16, 16),
                    Block.box(.5, .5,   12, 15.5, 15.5, 16),
                    Block.box( 4,  4,    8,   12,   12, 12)
            ),
            null,
            null
    );
    public static final PyramidShapes UPPER_SLAB = new PyramidShapes(
            () -> ShapeUtils.orUnoptimized(
                    Block.box(0, 0, 4, 16, 16, 8),
                    Block.box(4, 4, 0, 12, 12, 4)
            ),
            () -> ShapeUtils.orUnoptimized(
                    Block.box(  0,   0, 7.5,   16,   16,  8),
                    Block.box( .5,  .5,   4, 15.5, 15.5,  8),
                    Block.box(  4,   4,  .5,   12,   12,  4),
                    Block.box(7.5, 7.5,   0,  8.5,  8.5, .5)
            ),
            () -> ShapeUtils.orUnoptimized(
                    Block.box(  0,   0, 7.5,   16,   16,  8),
                    Block.box( .5,  .5,   4, 15.5, 15.5,  8),
                    Block.box(  4,   4,  .5,   12,   12,  4),
                    Block.box(  6,   6,    0,  10,   10, .5)
            ),
            () -> ShapeUtils.orUnoptimized(
                    Block.box(  0,   0, 7.5,   16,   16,  8),
                    Block.box( .5,  .5,   4, 15.5, 15.5,  8),
                    Block.box(  4,   4,   0,   12,   12,  4)
            )
    );
    public static final PyramidShapes ELEVATED_SLAB = new PyramidShapes(
            () -> ShapeUtils.orUnoptimized(
                    Block.box(0, 0, 4, 16, 16, 16),
                    Block.box(4, 4, 0, 12, 12,  4)
            ),
            () -> ShapeUtils.orUnoptimized(
                    Block.box(  0,   0, 7.5,   16,   16, 16),
                    Block.box( .5,  .5,   4, 15.5, 15.5,  8),
                    Block.box(  4,   4,  .5,   12,   12,  4),
                    Block.box(7.5, 7.5,   0,  8.5,  8.5, .5)
            ),
            () -> ShapeUtils.orUnoptimized(
                    Block.box(  0,   0, 7.5,   16,   16, 16),
                    Block.box( .5,  .5,   4, 15.5, 15.5,  8),
                    Block.box(  4,   4,  .5,   12,   12,  4),
                    Block.box(  6,   6,    0,  10,   10, .5)
            ),
            () -> ShapeUtils.orUnoptimized(
                    Block.box(  0,   0, 7.5,   16,   16, 16),
                    Block.box( .5,  .5,   4, 15.5, 15.5,  8),
                    Block.box(  4,   4,   0,   12,   12,  4)
            )
    );

    private final Supplier<VoxelShape> northShape;
    private final Supplier<VoxelShape> northOcclusionShape;
    @Nullable
    private final Supplier<VoxelShape> northOcclusionShapePost;
    @Nullable
    private final Supplier<VoxelShape> northOcclusionShapePillar;

    private PyramidShapes(
            Supplier<VoxelShape> northShape,
            Supplier<VoxelShape> northOcclusionShape,
            @Nullable Supplier<VoxelShape> northOcclusionShapePost,
            @Nullable Supplier<VoxelShape> northOcclusionShapePillar
    ) {
        this.northShape = northShape;
        this.northOcclusionShape = northOcclusionShape;
        this.northOcclusionShapePost = northOcclusionShapePost;
        this.northOcclusionShapePillar = northOcclusionShapePillar;
    }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generate(states, northShape, null, null);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        return generate(states, northOcclusionShape, northOcclusionShapePost, northOcclusionShapePillar);
    }

    private static ShapeContainer generate(
            List<BlockState> states,
            Supplier<VoxelShape> northShape,
            @Nullable Supplier<VoxelShape> northShapePost,
            @Nullable Supplier<VoxelShape> northShapePillar
    ) {
        VoxelShape shapeNorth = northShape.get();
        VoxelShape shapeNorthPost = northShapePost != null ? northShapePost.get() : null;
        VoxelShape shapeNorthPillar = northShapePillar != null ? northShapePillar.get() : null;

        VoxelShape shapeUp = ShapeUtils.rotateShapeAroundX(Direction.NORTH, Direction.UP, shapeNorth);
        VoxelShape[] upShapes = new VoxelShape[] {
                shapeUp,
                shapeNorthPost != null ? ShapeUtils.rotateShapeAroundX(Direction.NORTH, Direction.UP, shapeNorthPost) : shapeUp,
                shapeNorthPillar != null ? ShapeUtils.rotateShapeAroundX(Direction.NORTH, Direction.UP, shapeNorthPillar) : shapeUp
        };
        VoxelShape shapeDown = ShapeUtils.rotateShapeAroundX(Direction.NORTH, Direction.DOWN, shapeNorth);
        VoxelShape[] downShapes = new VoxelShape[] {
                shapeDown,
                shapeNorthPost != null ? ShapeUtils.rotateShapeAroundX(Direction.NORTH, Direction.DOWN, shapeNorthPost) : shapeDown,
                shapeNorthPillar != null ? ShapeUtils.rotateShapeAroundX(Direction.NORTH, Direction.DOWN, shapeNorthPillar) : shapeDown
        };

        VoxelShape[] horShapesNone = ShapeUtils.makeHorizontalRotations(shapeNorth, Direction.NORTH);
        VoxelShape[][] horShapes = new VoxelShape[][] {
                horShapesNone,
                shapeNorthPost != null ? ShapeUtils.makeHorizontalRotations(shapeNorthPost, Direction.NORTH) : horShapesNone,
                shapeNorthPillar != null ? ShapeUtils.makeHorizontalRotations(shapeNorthPillar, Direction.NORTH) : horShapesNone
        };

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states) {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            int conIndex = state.getValueOrElse(PropertyHolder.PILLAR_CONNECTION, PillarConnection.NONE).ordinal();
            VoxelShape shape = switch (facing) {
                case UP -> upShapes[conIndex];
                case DOWN -> downShapes[conIndex];
                default -> horShapes[conIndex][facing.get2DDataValue()];
            };
            map.put(state, shape);
        }

        return ShapeContainer.of(map);
    }
}
