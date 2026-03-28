package io.github.xfacthd.framedblocks.common.data.shapes.cube;

import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.common.block.cube.FramedChestBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class ChestShapes {
    public static ShapeContainer generate(List<BlockState> states) {
        VoxelShape shapeSingle = Block.box(1D, 0D, 1D, 15D, 14D, 15D);
        VoxelShape[] conShapes = new VoxelShape[] {
                Block.box(1D, 0D, 1D, 15D, 14D, 16D),
                Block.box(0D, 0D, 1D, 15D, 14D, 15D),
                Block.box(1D, 0D, 0D, 15D, 14D, 15D),
                Block.box(1D, 0D, 1D, 16D, 14D, 15D)
        };

        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states) {
            ChestType type = state.getValue(BlockStateProperties.CHEST_TYPE);
            if (type == ChestType.SINGLE) {
                map.put(state, shapeSingle);
            } else {
                Direction conDir = FramedChestBlock.getConnectionDirection(state);
                map.put(state, conShapes[conDir.get2DDataValue()]);
            }
        }

        return ShapeContainer.of(map);
    }

    private ChestShapes() { }
}
