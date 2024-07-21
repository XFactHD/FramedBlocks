package xfacthd.framedblocks.common.data.shapes.sign;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.shapes.ShapeProvider;

public final class CeilingHangingSignShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeZeroEight = Block.box(1, 0, 7, 15, 10, 9);
        VoxelShape shapeFourTwelve = Block.box(7, 0, 1, 9, 10, 15);
        VoxelShape fallbackShape = Block.box(3, 0, 3, 13, 16, 13);

        for (BlockState state : states)
        {
            int rot = state.getValue(BlockStateProperties.ROTATION_16);
            builder.put(state, switch (rot)
            {
                case 0, 8 -> shapeZeroEight;
                case 4, 12 -> shapeFourTwelve;
                default -> fallbackShape;
            });
        }

        return ShapeProvider.of(builder.build());
    }
}
