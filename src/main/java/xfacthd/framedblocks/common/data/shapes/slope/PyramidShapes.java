package xfacthd.framedblocks.common.data.shapes.slope;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

import java.util.function.Supplier;

public final class PyramidShapes implements SplitShapeGenerator
{
    public static final PyramidShapes FULL = new PyramidShapes(
            () -> ShapeUtils.orUnoptimized(
                    Block.box(0, 0, 8, 16, 16, 16),
                    Block.box(4, 4, 0, 12, 12,  8)
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
            )
    );

    private final Supplier<VoxelShape> northShape;
    private final Supplier<VoxelShape> northOcclusionShape;

    private PyramidShapes(Supplier<VoxelShape> northShape, Supplier<VoxelShape> northOcclusionShape)
    {
        this.northShape = northShape;
        this.northOcclusionShape = northOcclusionShape;
    }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, northShape);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, northOcclusionShape);
    }

    private static ShapeProvider generate(ImmutableList<BlockState> states, Supplier<VoxelShape> northShape)
    {
        VoxelShape shapeNorth = northShape.get();
        VoxelShape shapeUp = ShapeUtils.rotateShapeAroundX(Direction.NORTH, Direction.UP, shapeNorth);
        VoxelShape shapeDown = ShapeUtils.rotateShapeAroundX(Direction.NORTH, Direction.DOWN, shapeNorth);

        VoxelShape[] horShapes = ShapeUtils.makeHorizontalRotations(shapeNorth, Direction.NORTH);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = new ImmutableMap.Builder<>();

        for (BlockState state : states)
        {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            VoxelShape shape = switch (facing)
            {
                case UP -> shapeUp;
                case DOWN -> shapeDown;
                default -> horShapes[facing.get2DDataValue()];
            };
            builder.put(state, shape);
        }

        return ShapeProvider.of(builder.build());
    }
}
