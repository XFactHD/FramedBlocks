package io.github.xfacthd.framedblocks.common.data.shapes.cube;

import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerTubeOrientation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class CornerTubeShapes
{
    public static ShapeContainer generate(List<BlockState> states)
    {
        VoxelShape[] thinShapes = makeShapes(2);
        VoxelShape[] thickShapes = makeShapes(3);

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states)
        {
            CornerTubeOrientation orientation = state.getValue(PropertyHolder.CORNER_TYPE_ORIENTATION);
            boolean thick = state.getValue(PropertyHolder.THICK);
            VoxelShape shape = (thick ? thickShapes : thinShapes)[orientation.ordinal()];
            map.put(state, shape);
        }

        return ShapeContainer.of(map);
    }

    private static VoxelShape[] makeShapes(int thickness)
    {
        int max = 16 - thickness;

        VoxelShape shapeUpNorth = ShapeUtils.andUnoptimized(
                Shapes.joinUnoptimized(
                        Shapes.block(),
                        Block.box(thickness, thickness, thickness, max, 16, max),
                        BooleanOp.ONLY_FIRST
                ),
                Shapes.joinUnoptimized(
                        Shapes.block(),
                        Block.box(thickness, thickness, 0, max, max, max),
                        BooleanOp.ONLY_FIRST
                )
        );
        VoxelShape shapeDownNorth = ShapeUtils.rotateShapeAroundZ(Direction.UP, Direction.DOWN, shapeUpNorth);
        VoxelShape shapeNorthEast = ShapeUtils.rotateShapeAroundZ(Direction.UP, Direction.EAST, shapeUpNorth);

        VoxelShape[] shapes = new VoxelShape[CornerTubeOrientation.COUNT];
        ShapeUtils.makeHorizontalRotations(shapeUpNorth, Direction.SOUTH, shapes, 0);
        ShapeUtils.makeHorizontalRotations(shapeDownNorth, Direction.SOUTH, shapes, 4);
        ShapeUtils.makeHorizontalRotations(shapeNorthEast, Direction.SOUTH, shapes, 8);
        return shapes;
    }

    private CornerTubeShapes() { }
}
