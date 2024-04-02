package xfacthd.framedblocks.api.ghost;

import xfacthd.framedblocks.api.camo.CamoContent;
import xfacthd.framedblocks.api.camo.empty.EmptyCamoContent;

@SuppressWarnings("unused")
public final class CamoPair
{
    public static final CamoPair EMPTY = new CamoPair(EmptyCamoContent.EMPTY, EmptyCamoContent.EMPTY);

    private CamoContent<?> camoOne;
    private CamoContent<?> camoTwo;

    public CamoPair(CamoContent<?> camoOne, CamoContent<?> camoTwo)
    {
        this.camoOne = camoOne != null ? camoOne : EmptyCamoContent.EMPTY;
        this.camoTwo = camoTwo != null ? camoTwo : EmptyCamoContent.EMPTY;
    }

    public CamoPair swap()
    {
        CamoContent<?> temp = camoOne;
        camoOne = camoTwo;
        camoTwo = temp;
        return this;
    }

    public CamoPair clear()
    {
        camoOne = EmptyCamoContent.EMPTY;
        camoTwo = EmptyCamoContent.EMPTY;
        return this;
    }

    public CamoContent<?> getCamoOne()
    {
        return camoOne;
    }

    public CamoContent<?> getCamoTwo()
    {
        return camoTwo;
    }
}
