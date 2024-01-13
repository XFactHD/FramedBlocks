package xfacthd.framedblocks.common.data.doubleblock;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.common.IPlantable;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;

public interface PlantablePredicate
{
    boolean test(FramedDoubleBlockEntity be, Direction side, IPlantable plant);
}
