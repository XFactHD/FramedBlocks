package xfacthd.framedblocks;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xfacthd.framedblocks.common.FBContent;

@Mod(FramedBlocks.MODID)
public class FramedBlocks
{
	public static final String MODID = "framedblocks";
	
    private static final Logger LOGGER = LogManager.getLogger();

    public static final ItemGroup FRAMED_GROUP = new ItemGroup("framed_blocks")
    {
        @Override
        public ItemStack createIcon() { return new ItemStack(FBContent.blockFramedCube); }
    };

    public FramedBlocks()
	{
        
    }
}
