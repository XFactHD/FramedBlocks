package io.github.xfacthd.framedblocks.common.data.shapes.pillar;

import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class PillarShapes implements ShapeGenerator {
    public static final PillarShapes PILLAR = new PillarShapes(8);
    public static final PillarShapes POST = new PillarShapes(4);

    private final int minSize;
    private final int maxSize;

    private PillarShapes(int thickness) {
        this.minSize = 8 - (thickness / 2);
        this.maxSize = 8 + (thickness / 2);
    }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape shapeX = Block.box(0, minSize, minSize, 16, maxSize, maxSize);
        VoxelShape shapeY = Block.box(minSize, 0, minSize, maxSize, 16, maxSize);
        VoxelShape shapeZ = Block.box(minSize, minSize, 0, maxSize, maxSize, 16);

        for (BlockState state : states) {
            map.put(state, switch (state.getValue(BlockStateProperties.AXIS)) {
                case X -> shapeX;
                case Y -> shapeY;
                case Z -> shapeZ;
            });
        }

        return ShapeContainer.of(map);
    }
}
