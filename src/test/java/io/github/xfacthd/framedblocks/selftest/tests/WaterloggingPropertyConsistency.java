package io.github.xfacthd.framedblocks.selftest.tests;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.selftest.SelfTestReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public final class WaterloggingPropertyConsistency {
    public static void checkWaterloggingProperty(SelfTestReporter reporter, List<Block> blocks) {
        reporter.startTest("waterlogging property");

        blocks.forEach(block -> {
            boolean onType = ((IFramedBlock) block).getBlockType().isWaterloggable();
            boolean onBlock = block.defaultBlockState().hasProperty(BlockStateProperties.WATERLOGGED);
            if (onType != onBlock) {
                reporter.warn("Block '{}' has inconsistent waterlogging configuration", block);
            }
        });

        reporter.endTest();
    }

    private WaterloggingPropertyConsistency() { }
}
