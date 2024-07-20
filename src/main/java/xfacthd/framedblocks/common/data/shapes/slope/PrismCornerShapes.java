package xfacthd.framedblocks.common.data.shapes.slope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

import java.util.function.Supplier;

public final class PrismCornerShapes implements SplitShapeGenerator
{
    public static final PrismCornerShapes OUTER = new PrismCornerShapes(
            () -> ShapeUtils.orUnoptimized(
                    Block.box(0,  0,  0, 16,  4,  4),
                    Block.box(0,  0,  4, 12,  4,  8),
                    Block.box(0,  0,  8,  8,  4, 12),
                    Block.box(0,  0, 12,  4,  4, 16),
                    Block.box(0,  4,  0, 12,  8,  4),
                    Block.box(0,  4,  4,  8,  8,  8),
                    Block.box(0,  4,  8,  4,  8, 12),
                    Block.box(0,  8,  0,  8, 12,  4),
                    Block.box(0,  8,  4,  4, 12,  8),
                    Block.box(0, 12,  0,  4, 16,  4)
            ),
            () -> ShapeUtils.orUnoptimized(
                    Block.box( 0,  0,  0,   .5,   .5,   16),
                    Block.box( 0,  0,  0,    4,    4, 15.5),
                    Block.box( 0,  4,  0,    4,    8,   12),
                    Block.box( 0,  8,  0,    4,   12,    8),
                    Block.box( 0, 12,  0,    4, 15.5,    4),
                    Block.box( 0, 12,  0,   .5,   16,   .5),
                    Block.box( 4,  0,  0,    8,    4,   12),
                    Block.box( 4,  4,  0,    8,    8,    8),
                    Block.box( 4,  8,  0,    8,   12,    4),
                    Block.box( 8,  0,  0,   12,    4,    8),
                    Block.box( 8,  4,  0,   12,    8,    4),
                    Block.box(12,  0,  0, 15.5,    4,    4),
                    Block.box(12,  0,  0,   16,   .5,   .5)
            )
    );
    public static final PrismCornerShapes INNER = new PrismCornerShapes(
            () -> ShapeUtils.orUnoptimized(
                    Block.box(0,  0,  0, 16,  4, 16),
                    Block.box(0,  4,  0, 16,  8, 12),
                    Block.box(0,  4, 12, 12,  8, 16),
                    Block.box(0,  8,  0, 16, 12,  8),
                    Block.box(0,  8, 12,  8, 12, 16),
                    Block.box(0,  8,  8, 12, 12, 12),
                    Block.box(0, 12,  0, 16, 16,  4),
                    Block.box(0, 12,  4, 12, 16,  8),
                    Block.box(0, 12,  8,  8, 16, 12),
                    Block.box(0, 12, 12,  4, 16, 16)
            ),
            () -> ShapeUtils.orUnoptimized(
                    Block.box(   0,    0,    0,   16,   .5,   16),
                    Block.box(   0,    0,    0,   16,    4, 15.5),
                    Block.box(   0,    0, 15.5, 15.5,    4,   16),
                    Block.box(   0,    4,    0,   12,    8,   16),
                    Block.box(  12,    4,    0,   16,    8,   12),
                    Block.box(   0,    8,    0,   16,   12,    8),
                    Block.box(   0,    8,    8,    8,   12,   16),
                    Block.box(   8,    8,    8,   12,   12,   12),
                    Block.box(   0,   12,    0,   16, 15.5,    4),
                    Block.box(   0, 15.5,    0, 15.5,   16,    4),
                    Block.box(15.5, 15.5,    0,   16,   16,   .5),
                    Block.box(   0,   12,    4,    4, 15.5,   16),
                    Block.box(   0, 15.5,    4,    4,   16, 15.5),
                    Block.box(   0, 15.5, 15.5,   .5,   16,   16),
                    Block.box(   4,   12,    4,    8,   16,   12),
                    Block.box(   8,   12,    4,   12,   16,    8)
            )
    );

    private final Supplier<VoxelShape> bottomShape;
    private final Supplier<VoxelShape> bottomOcclusionShape;

    public PrismCornerShapes(Supplier<VoxelShape> bottomShape, Supplier<VoxelShape> bottomOcclusionShape)
    {
        this.bottomShape = bottomShape;
        this.bottomOcclusionShape = bottomOcclusionShape;
    }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, bottomShape);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, bottomOcclusionShape);
    }

    private static ShapeProvider generate(ImmutableList<BlockState> states, Supplier<VoxelShape> bottomShape)
    {
        VoxelShape shapeBottom = bottomShape.get();
        VoxelShape shapeTop = ShapeUtils.rotateShapeAroundY(
                Direction.NORTH, Direction.WEST, ShapeUtils.rotateShapeUnoptimizedAroundZ(
                        Direction.DOWN, Direction.UP, shapeBottom
                )
        );

        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotationsWithFlag(shapeBottom, shapeTop, Direction.NORTH);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            builder.put(state, shapes[dir.get2DDataValue() + (top ? 4 : 0)]);
        }

        return ShapeProvider.of(builder.build());
    }
}
