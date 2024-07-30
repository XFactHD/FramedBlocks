package xfacthd.framedblocks.common.data.shapes.pillar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;

public final class LatticeShapes implements ShapeGenerator
{
    public static final LatticeShapes THIN = new LatticeShapes(4);
    public static final LatticeShapes THICK = new LatticeShapes(8);

    private final int minSize;
    private final int maxSize;

    private LatticeShapes(int thickness)
    {
        this.minSize = 8 - (thickness / 2);
        this.maxSize = 8 + (thickness / 2);
    }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape centerShape = Block.box(minSize, minSize, minSize, maxSize, maxSize, maxSize);
        VoxelShape xShape = Block.box(0, minSize, minSize, 16, maxSize, maxSize);
        VoxelShape yShape = Block.box(minSize, 0, minSize, maxSize, 16, maxSize);
        VoxelShape zShape = Block.box(minSize, minSize, 0, maxSize, maxSize, 16);

        int maskX = 0b001;
        int maskY = 0b010;
        int maskZ = 0b100;
        VoxelShape[] shapes = new VoxelShape[8];
        for (int i = 0; i < 8; i++)
        {
            VoxelShape shape = centerShape;
            if ((i & maskX) != 0)
            {
                shape = ShapeUtils.orUnoptimized(shape, xShape);
            }
            if ((i & maskY) != 0)
            {
                shape = ShapeUtils.orUnoptimized(shape, yShape);
            }
            if ((i & maskZ) != 0)
            {
                shape = ShapeUtils.orUnoptimized(shape, zShape);
            }
            shapes[i] = shape.optimize();
        }

        for (BlockState state : states)
        {
            int x = state.getValue(FramedProperties.X_AXIS) ? maskX : 0;
            int y = state.getValue(FramedProperties.Y_AXIS) ? maskY : 0;
            int z = state.getValue(FramedProperties.Z_AXIS) ? maskZ : 0;
            builder.put(state, shapes[x | y | z]);
        }

        return ShapeProvider.of(builder.build());
    }
}
