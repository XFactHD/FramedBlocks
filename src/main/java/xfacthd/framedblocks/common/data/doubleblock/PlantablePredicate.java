package xfacthd.framedblocks.common.data.doubleblock;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;

public interface PlantablePredicate
{
    TriState test(FramedDoubleBlockEntity be, Direction side, BlockState plant);
}
