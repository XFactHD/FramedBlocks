package xfacthd.framedblocks.api.data;

public enum ContainerType
{
    EMPTY,
    BLOCK,
    FLUID;

    public boolean isEmpty() { return this == EMPTY; }

    public boolean isBlock() { return this == BLOCK; }

    public boolean isFluid() { return this == FLUID; }
}
