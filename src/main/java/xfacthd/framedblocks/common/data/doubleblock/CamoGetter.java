package xfacthd.framedblocks.common.data.doubleblock;

import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.EmptyCamoContainer;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

import java.util.function.Function;

public enum CamoGetter
{
    NONE(be -> EmptyCamoContainer.EMPTY),
    FIRST(FramedDoubleBlockEntity::getCamo),
    SECOND(FramedDoubleBlockEntity::getCamoTwo);

    private final Function<FramedDoubleBlockEntity, CamoContainer> camoGetter;

    CamoGetter(Function<FramedDoubleBlockEntity, CamoContainer> camoGetter)
    {
        this.camoGetter = camoGetter;
    }

    public CamoContainer getCamo(FramedDoubleBlockEntity be)
    {
        return camoGetter.apply(be);
    }
}
