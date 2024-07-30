package xfacthd.framedblocks.common.data.shapes.pillar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.shapes.ShapeGenerator;
import xfacthd.framedblocks.api.shapes.ShapeProvider;

public final class PillarShapes implements ShapeGenerator
{
    public static final PillarShapes PILLAR = new PillarShapes(8);
    public static final PillarShapes POST = new PillarShapes(4);

    private final int minSize;
    private final int maxSize;

    private PillarShapes(int thickness)
    {
        this.minSize = 8 - (thickness / 2);
        this.maxSize = 8 + (thickness / 2);
    }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape shapeX = Block.box(0, minSize, minSize, 16, maxSize, maxSize);
        VoxelShape shapeY = Block.box(minSize, 0, minSize, maxSize, 16, maxSize);
        VoxelShape shapeZ = Block.box(minSize, minSize, 0, maxSize, maxSize, 16);

        for (BlockState state : states)
        {
            builder.put(state, switch (state.getValue(BlockStateProperties.AXIS))
            {
                case X -> shapeX;
                case Y -> shapeY;
                case Z -> shapeZ;
            });
        }

        return ShapeProvider.of(builder.build());
    }
}
