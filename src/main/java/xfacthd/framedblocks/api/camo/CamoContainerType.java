package xfacthd.framedblocks.api.camo;

public enum CamoContainerType
{
    EMPTY,
    BLOCK,
    FLUID;

    public boolean isEmpty()
    {
        return this == EMPTY;
    }

    public boolean isBlock()
    {
        return this == BLOCK;
    }

    public boolean isFluid()
    {
        return this == FLUID;
    }
}
