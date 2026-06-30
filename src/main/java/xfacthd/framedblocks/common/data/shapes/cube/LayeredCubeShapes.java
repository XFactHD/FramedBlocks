package xfacthd.framedblocks.common.data.shapes.cube;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;

public final class LayeredCubeShapes
{
    private static final int LAYER_COUNT = 8;
    private static final int DIR_COUNT = Direction.values().length;

    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        VoxelShape[] shapes = new VoxelShape[LAYER_COUNT * DIR_COUNT];
        for (int i = 1; i <= LAYER_COUNT; i++)
        {
            VoxelShape layerShapeUp = Block.box(0, 0, 0, 16, i * 2, 16);
            VoxelShape layerShapeDown = ShapeUtils.rotateShapeAroundX(Direction.UP, Direction.DOWN, layerShapeUp);
            VoxelShape layerShapeNorth = ShapeUtils.rotateShapeAroundX(Direction.UP, Direction.NORTH, layerShapeUp);

            shapes[index(Direction.UP, i)] = layerShapeUp;
            shapes[index(Direction.DOWN, i)] = layerShapeDown;
            ShapeUtils.makeHorizontalRotations(layerShapeNorth, Direction.NORTH, shapes, i, LayeredCubeShapes::index);
        }

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(BlockStateProperties.FACING);
            int layers = state.getValue(BlockStateProperties.LAYERS);
            builder.put(state, shapes[index(dir, layers)]);
        }

        return ShapeProvider.of(builder.build());
    }

    private static int index(Direction dir, int layers)
    {
        return ((layers - 1) * DIR_COUNT) + dir.ordinal();
    }

    private LayeredCubeShapes() { }
}
