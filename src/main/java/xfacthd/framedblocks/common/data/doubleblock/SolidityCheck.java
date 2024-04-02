package xfacthd.framedblocks.common.data.doubleblock;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.common.IPlantable;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;

import java.util.function.Predicate;

public enum SolidityCheck
{
    NONE(
            be -> false,
            (be, side, plant) -> false
    ),
    FIRST(
            be -> be.getCamo().getContent().isSolid(be.getLevel(), be.getBlockPos()),
            (be, side, plant) -> be.getCamo().getContent().canSustainPlant(be.getLevel(), be.getBlockPos(), side, plant)
    ),
    SECOND(
            be -> be.getCamoTwo().getContent().isSolid(be.getLevel(), be.getBlockPos()),
            (be, side, plant) -> be.getCamoTwo().getContent().canSustainPlant(be.getLevel(), be.getBlockPos(), side, plant)
    ),
    BOTH(
            be -> FIRST.isSolid(be) && SECOND.isSolid(be),
            (be, side, plant) -> FIRST.canSustainPlant(be, side, plant) && SECOND.canSustainPlant(be, side, plant)
    );

    private final Predicate<FramedDoubleBlockEntity> predicate;
    private final PlantablePredicate plantablePredicate;

    SolidityCheck(Predicate<FramedDoubleBlockEntity> predicate, PlantablePredicate plantablePredicate)
    {
        this.predicate = predicate;
        this.plantablePredicate = plantablePredicate;
    }

    public boolean isSolid(FramedDoubleBlockEntity be)
    {
        return predicate.test(be);
    }

    public boolean canSustainPlant(FramedDoubleBlockEntity be, Direction side, IPlantable plant)
    {
        return plantablePredicate.test(be, side, plant);
    }
}
