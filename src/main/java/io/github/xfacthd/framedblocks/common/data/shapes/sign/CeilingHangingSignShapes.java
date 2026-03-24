package io.github.xfacthd.framedblocks.common.data.shapes.sign;

import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class CeilingHangingSignShapes
{
    public static ShapeContainer generate(List<BlockState> states)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape shapeZeroEight = Block.box(1, 0, 7, 15, 10, 9);
        VoxelShape shapeFourTwelve = Block.box(7, 0, 1, 9, 10, 15);
        VoxelShape fallbackShape = Block.box(3, 0, 3, 13, 16, 13);

        for (BlockState state : states)
        {
            int rot = state.getValue(BlockStateProperties.ROTATION_16);
            map.put(state, switch (rot)
            {
                case 0, 8 -> shapeZeroEight;
                case 4, 12 -> shapeFourTwelve;
                default -> fallbackShape;
            });
        }

        return ShapeContainer.of(map);
    }

    private CeilingHangingSignShapes() { }
}
