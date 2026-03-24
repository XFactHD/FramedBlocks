package io.github.xfacthd.framedblocks.common.data.shapes.pillar;

import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class HalfPillarShapes
{
    public static ShapeContainer generate(List<BlockState> states)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape shapeNorth = Block.box(4, 4, 0, 12, 12,  8);
        VoxelShape shapeSouth = Block.box(4, 4, 8, 12, 12, 16);
        VoxelShape shapeEast =  Block.box(8, 4, 4, 16, 12, 12);
        VoxelShape shapeWest =  Block.box(0, 4, 4,  8, 12, 12);
        VoxelShape shapeUp =    Block.box(4, 8, 4, 12, 16, 12);
        VoxelShape shapeDown =  Block.box(4, 0, 4, 12,  8, 12);

        for (BlockState state : states)
        {
            map.put(state, switch (state.getValue(BlockStateProperties.FACING))
            {
                case NORTH -> shapeNorth;
                case EAST -> shapeEast;
                case SOUTH -> shapeSouth;
                case WEST -> shapeWest;
                case UP -> shapeUp;
                case DOWN -> shapeDown;
            });
        }

        return ShapeContainer.of(map);
    }

    private HalfPillarShapes() { }
}
