package io.github.xfacthd.framedblocks.common.data.shapes.interactive;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class WallBannerShapes {
    public static ShapeContainer generate(List<BlockState> states) {
        VoxelShape southShape = Block.box(0, 0, 0, 16, 14, 2);
        VoxelShape[] shapes = ShapeUtils.makeHorizontalRotations(southShape, Direction.SOUTH);

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());
        for (BlockState state : states) {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            map.put(state, shapes[dir.get2DDataValue()]);
        }
        return ShapeContainer.of(map);
    }

    private WallBannerShapes() { }
}
