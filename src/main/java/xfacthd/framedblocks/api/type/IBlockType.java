package xfacthd.framedblocks.api.type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.util.CtmPredicate;
import xfacthd.framedblocks.api.util.SideSkipPredicate;

public interface IBlockType
{
    boolean hasSpecialHitbox();

    CtmPredicate getCtmPredicate();

    SideSkipPredicate getSideSkipPredicate();

    ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states);

    boolean hasSpecialTile();

    boolean hasBlockItem();

    boolean supportsWaterLogging();

    String getName();

    int compareTo(IBlockType other);
}