package xfacthd.framedblocks.common.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.VoxelShape;

public interface VoxelShapeGenerator
{
    ImmutableMap<BlockState, VoxelShape> generate(ImmutableList<BlockState> states);
}