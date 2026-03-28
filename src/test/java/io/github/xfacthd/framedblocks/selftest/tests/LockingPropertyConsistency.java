package io.github.xfacthd.framedblocks.selftest.tests;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.ShapeLockableBlock;
import io.github.xfacthd.framedblocks.selftest.SelfTestReporter;
import net.minecraft.world.level.block.Block;

import java.util.List;

public final class LockingPropertyConsistency {
    public static void checkLockingProperty(SelfTestReporter reporter, List<Block> blocks) {
        reporter.startTest("locking property");

        blocks.forEach(block -> {
            boolean hasInterface = block instanceof ShapeLockableBlock;
            boolean hasProperty = block.defaultBlockState().hasProperty(FramedProperties.STATE_LOCKED);
            if (hasInterface != hasProperty) {
                reporter.warn("Block '{}' has inconsistent locking configuration", block);
            }
        });

        reporter.endTest();
    }

    private LockingPropertyConsistency() { }
}
