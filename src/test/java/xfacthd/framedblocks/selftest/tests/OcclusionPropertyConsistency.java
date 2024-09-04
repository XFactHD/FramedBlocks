package xfacthd.framedblocks.selftest.tests;

import net.minecraft.world.level.block.Block;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.selftest.SelfTestReporter;

import java.util.List;

public final class OcclusionPropertyConsistency
{
    public static void checkOcclusionProperty(SelfTestReporter reporter, List<Block> blocks)
    {
        reporter.startTest("occlusion property");

        blocks.forEach(block ->
        {
            boolean onType = ((IFramedBlock) block).getBlockType().canOccludeWithSolidCamo();
            boolean onBlock = block.defaultBlockState().hasProperty(FramedProperties.SOLID);
            if (onType != onBlock)
            {
                reporter.warn("Block '{}' has inconsistent occlusion configuration", block);
            }
        });

        reporter.endTest();
    }



    private OcclusionPropertyConsistency() { }
}
