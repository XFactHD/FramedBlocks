package xfacthd.framedblocks.common.data;

public enum FramedToolType
{
    HAMMER("framed_hammer"),
    WRENCH("framed_wrench");

    private final String name;

    FramedToolType(String name) { this.name = name; }

    public String getName() { return name; }
}