package xfacthd.framedblocks.common.data.shapes.sign;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;

public final class WallSignShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            switch (state.getValue(FramedProperties.FACING_HOR))
            {
                case NORTH -> builder.put(state, Block.box(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D));
                case EAST -> builder.put(state, Block.box(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D));
                case SOUTH -> builder.put(state, Block.box(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D));
                case WEST -> builder.put(state, Block.box(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D));
            }
        }

        return ShapeProvider.of(builder.build());
    }
}
