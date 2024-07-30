package xfacthd.framedblocks.common.data.shapes.pillar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.shapes.ShapeProvider;

public final class HalfPillarShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeNorth = Block.box(4, 4, 0, 12, 12,  8);
        VoxelShape shapeSouth = Block.box(4, 4, 8, 12, 12, 16);
        VoxelShape shapeEast =  Block.box(8, 4, 4, 16, 12, 12);
        VoxelShape shapeWest =  Block.box(0, 4, 4,  8, 12, 12);
        VoxelShape shapeUp =    Block.box(4, 8, 4, 12, 16, 12);
        VoxelShape shapeDown =  Block.box(4, 0, 4, 12,  8, 12);

        for (BlockState state : states)
        {
            builder.put(state, switch (state.getValue(BlockStateProperties.FACING))
            {
                case NORTH -> shapeNorth;
                case EAST -> shapeEast;
                case SOUTH -> shapeSouth;
                case WEST -> shapeWest;
                case UP -> shapeUp;
                case DOWN -> shapeDown;
            });
        }

        return ShapeProvider.of(builder.build());
    }



    private HalfPillarShapes() { }
}
