package xfacthd.framedblocks.common.data;

public enum FramedToolType
{
    HAMMER("framed_hammer"),
    WRENCH("framed_wrench"),
    BLUEPRINT("framed_blueprint"),
    KEY("framed_key"),
    SCREWDRIVER("framed_screwdriver"),
    ;

    private final String name;

    FramedToolType(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}