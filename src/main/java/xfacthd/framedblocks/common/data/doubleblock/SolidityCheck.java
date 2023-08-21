package xfacthd.framedblocks.common.data.doubleblock;

import net.minecraft.core.Direction;
import net.minecraftforge.common.IPlantable;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

import java.util.function.Predicate;

public enum SolidityCheck
{
    NONE(
            be -> false,
            (be, side, plant) -> false
    ),
    FIRST(
            be -> be.getCamo().isSolid(be.getLevel(), be.getBlockPos()),
            (be, side, plant) -> FramedBlockEntity.canSustainPlant(be, be.getCamo(), side, plant)
    ),
    SECOND(
            be -> be.getCamoTwo().isSolid(be.getLevel(), be.getBlockPos()),
            (be, side, plant) -> FramedBlockEntity.canSustainPlant(be, be.getCamoTwo(), side, plant)
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
