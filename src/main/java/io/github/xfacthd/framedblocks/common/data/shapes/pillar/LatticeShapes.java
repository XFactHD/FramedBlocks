package io.github.xfacthd.framedblocks.common.data.shapes.pillar;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class LatticeShapes implements ShapeGenerator {
    public static final LatticeShapes THIN = new LatticeShapes(4);
    public static final LatticeShapes THICK = new LatticeShapes(8);

    private final int minSize;
    private final int maxSize;

    private LatticeShapes(int thickness) {
        this.minSize = 8 - (thickness / 2);
        this.maxSize = 8 + (thickness / 2);
    }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states) {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        VoxelShape centerShape = Block.box(minSize, minSize, minSize, maxSize, maxSize, maxSize);
        VoxelShape xShape = Block.box(0, minSize, minSize, 16, maxSize, maxSize);
        VoxelShape yShape = Block.box(minSize, 0, minSize, maxSize, 16, maxSize);
        VoxelShape zShape = Block.box(minSize, minSize, 0, maxSize, maxSize, 16);

        int maskX = 0b001;
        int maskY = 0b010;
        int maskZ = 0b100;
        VoxelShape[] shapes = new VoxelShape[8];
        for (int i = 0; i < 8; i++) {
            VoxelShape shape = centerShape;
            if ((i & maskX) != 0) {
                shape = ShapeUtils.orUnoptimized(shape, xShape);
            }
            if ((i & maskY) != 0) {
                shape = ShapeUtils.orUnoptimized(shape, yShape);
            }
            if ((i & maskZ) != 0) {
                shape = ShapeUtils.orUnoptimized(shape, zShape);
            }
            shapes[i] = ShapeUtils.optimize(shape);
        }

        for (BlockState state : states) {
            int x = state.getValue(FramedProperties.X_AXIS) ? maskX : 0;
            int y = state.getValue(FramedProperties.Y_AXIS) ? maskY : 0;
            int z = state.getValue(FramedProperties.Z_AXIS) ? maskZ : 0;
            map.put(state, shapes[x | y | z]);
        }

        return ShapeContainer.of(map);
    }
}
