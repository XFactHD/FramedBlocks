package xfacthd.framedblocks.common.tileentity;

import net.minecraft.item.DyeColor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import xfacthd.framedblocks.common.FBContent;

import java.util.function.Function;

public class FramedSignTileEntity extends FramedTileEntity //TODO: implement sign functionality
{
    private final ITextComponent[] lines = new ITextComponent[4];
    private final String[] renderLines = new String[4];
    private DyeColor textColor = DyeColor.BLACK;

    public FramedSignTileEntity()
    {
        super(FBContent.tileTypeFramedSign);
        lines[0] = new StringTextComponent("This is");
        lines[1] = new StringTextComponent("a test of the");
        lines[2] = new StringTextComponent("framed sign");
        lines[3] = new StringTextComponent("renderer");
    }

    public void setLine(int line, ITextComponent text)
    {
        lines[line] = text;
        renderLines[line] = null;
    }

    public String getRenderedLine(int line, Function<ITextComponent, String> converter)
    {
        if (lines[line] != null && renderLines[line] == null)
        {
            renderLines[line] = converter.apply(lines[line]);
        }
        return renderLines[line];
    }

    public DyeColor getTextColor() { return textColor; }
}