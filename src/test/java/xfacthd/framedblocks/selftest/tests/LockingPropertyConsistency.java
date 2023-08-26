package xfacthd.framedblocks.selftest.tests;

import net.minecraft.world.level.block.Block;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;

import java.util.List;

public final class LockingPropertyConsistency
{
    public static void checkLockingProperty(List<Block> blocks)
    {
        FramedBlocks.LOGGER.info("  Checking locking property");

        blocks.forEach(block ->
        {
            boolean onType = ((IFramedBlock) block).getBlockType().canLockState();
            boolean onBlock = block.defaultBlockState().hasProperty(FramedProperties.STATE_LOCKED);
            if (onType != onBlock)
            {
                FramedBlocks.LOGGER.warn("    Block '{}' has inconsistent locking configuration", block);
            }
        });
    }


    private LockingPropertyConsistency() { }
}
