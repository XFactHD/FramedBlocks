package xfacthd.framedblocks.selftest.tests;

import net.minecraft.world.level.block.Block;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.selftest.SelfTestReporter;

import java.util.List;

public final class LockingPropertyConsistency
{
    public static void checkLockingProperty(SelfTestReporter reporter, List<Block> blocks)
    {
        reporter.startTest("locking property");

        blocks.forEach(block ->
        {
            boolean onType = ((IFramedBlock) block).getBlockType().canLockState();
            boolean onBlock = block.defaultBlockState().hasProperty(FramedProperties.STATE_LOCKED);
            if (onType != onBlock)
            {
                reporter.warn("Block '{}' has inconsistent locking configuration", block);
            }
        });

        reporter.endTest();
    }


    private LockingPropertyConsistency() { }
}
