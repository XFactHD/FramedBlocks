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

public final class TubeShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        VoxelShape shapeY = ShapeUtils.or(
                Block.box( 0, 0,  0, 16, 16,  2),
                Block.box( 0, 0, 14, 16, 16, 16),
                Block.box( 0, 0,  0,  2, 16, 16),
                Block.box(14, 0,  0, 16, 16, 16)
        );
        VoxelShape shapeZ = ShapeUtils.rotateShapeAroundX(Direction.UP, Direction.SOUTH, shapeY);
        VoxelShape shapeX = ShapeUtils.rotateShapeAroundZ(Direction.UP, Direction.EAST, shapeY);

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            VoxelShape shape = switch (state.getValue(BlockStateProperties.AXIS))
            {
                case X -> shapeX;
                case Y -> shapeY;
                case Z -> shapeZ;
            };
            builder.put(state, shape);
        }

        return ShapeProvider.of(builder.build());
    }



    private TubeShapes() { }
}
