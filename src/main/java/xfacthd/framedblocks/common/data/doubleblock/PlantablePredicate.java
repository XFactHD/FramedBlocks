package xfacthd.framedblocks.common.data.doubleblock;

import net.minecraft.core.Direction;
import net.minecraftforge.common.IPlantable;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public interface PlantablePredicate
{
    boolean test(FramedDoubleBlockEntity be, Direction side, IPlantable plant);
}
