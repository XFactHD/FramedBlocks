package xfacthd.framedblocks.selftest.tests;

import net.minecraft.world.level.block.Block;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;

import java.util.List;

public final class OcclusionPropertyConsistency
{
    public static void checkOcclusionProperty(List<Block> blocks)
    {
        FramedBlocks.LOGGER.info("  Checking occlusion property");

        blocks.forEach(block ->
        {
            boolean onType = ((IFramedBlock) block).getBlockType().canOccludeWithSolidCamo();
            boolean onBlock = block.defaultBlockState().hasProperty(FramedProperties.SOLID);
            if (onType != onBlock)
            {
                FramedBlocks.LOGGER.warn("    Block '{}' has inconsistent occlusion configuration", block);
            }
        });
    }



    private OcclusionPropertyConsistency() { }
}
