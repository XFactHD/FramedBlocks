package xfacthd.framedblocks.api.ghost;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public final class CamoPair
{
    public static final CamoPair EMPTY = new CamoPair(null, null);

    private BlockState camoOne;
    private BlockState camoTwo;

    public CamoPair(BlockState camoOne, BlockState camoTwo)
    {
        this.camoOne = camoOne != null ? camoOne : Blocks.AIR.defaultBlockState();
        this.camoTwo = camoTwo != null ? camoTwo : Blocks.AIR.defaultBlockState();
    }

    public CamoPair swap()
    {
        BlockState temp = camoOne;
        camoOne = camoTwo;
        camoTwo = temp;
        return this;
    }

    public CamoPair clear()
    {
        camoOne = Blocks.AIR.defaultBlockState();
        camoTwo = Blocks.AIR.defaultBlockState();
        return this;
    }

    public BlockState getCamoOne()
    {
        return camoOne;
    }

    public BlockState getCamoTwo()
    {
        return camoTwo;
    }
}
