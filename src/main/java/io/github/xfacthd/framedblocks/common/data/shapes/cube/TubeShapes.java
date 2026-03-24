package io.github.xfacthd.framedblocks.common.data.shapes.cube;

import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class TubeShapes
{
    public static ShapeContainer generate(List<BlockState> states)
    {
        VoxelShape shapeThinY = ShapeUtils.or(
                Block.box( 0, 0,  0, 16, 16,  2),
                Block.box( 0, 0, 14, 16, 16, 16),
                Block.box( 0, 0,  0,  2, 16, 16),
                Block.box(14, 0,  0, 16, 16, 16)
        );
        VoxelShape shapeThinZ = ShapeUtils.rotateShapeAroundX(Direction.UP, Direction.SOUTH, shapeThinY);
        VoxelShape shapeThinX = ShapeUtils.rotateShapeAroundZ(Direction.UP, Direction.EAST, shapeThinY);

        VoxelShape shapeThickY = ShapeUtils.or(
                Block.box( 0, 0,  0, 16, 16,  3),
                Block.box( 0, 0, 13, 16, 16, 16),
                Block.box( 0, 0,  0,  3, 16, 16),
                Block.box(13, 0,  0, 16, 16, 16)
        );
        VoxelShape shapeThickZ = ShapeUtils.rotateShapeAroundX(Direction.UP, Direction.SOUTH, shapeThickY);
        VoxelShape shapeThickX = ShapeUtils.rotateShapeAroundZ(Direction.UP, Direction.EAST, shapeThickY);

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states)
        {
            boolean thick = state.getValue(PropertyHolder.THICK);
            VoxelShape shape = switch (state.getValue(BlockStateProperties.AXIS))
            {
                case X -> thick ? shapeThickX : shapeThinX;
                case Y -> thick ? shapeThickY : shapeThinY;
                case Z -> thick ? shapeThickZ : shapeThinZ;
            };
            map.put(state, shape);
        }

        return ShapeContainer.of(map);
    }

    private TubeShapes() { }
}
