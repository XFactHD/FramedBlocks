package io.github.xfacthd.framedblocks.common.data.shapes.pane;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class LadderShapes implements ShapeGenerator {
    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        return generateShapes(states, 2F);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states) {
        // Misuse separate occlusion shape handling for collision shapes
        return generateShapes(states, 3F);
    }

    private static ShapeContainer generateShapes(List<BlockState> states, float depth) {
        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotations(Block.box(0, 0, 0, 16, 16, depth), Direction.NORTH);

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>();
        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            map.put(state, shapes[dir.get2DDataValue()]);
        }
        return ShapeContainer.of(map);
    }
}
