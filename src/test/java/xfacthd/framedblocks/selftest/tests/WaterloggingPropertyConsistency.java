package xfacthd.framedblocks.selftest.tests;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.selftest.SelfTestReporter;

import java.util.List;

public final class WaterloggingPropertyConsistency
{
    public static void checkWaterloggingProperty(SelfTestReporter reporter, List<Block> blocks)
    {
        reporter.startTest("waterlogging property");

        blocks.forEach(block ->
        {
            boolean onType = ((IFramedBlock) block).getBlockType().supportsWaterLogging();
            boolean onBlock = block.defaultBlockState().hasProperty(BlockStateProperties.WATERLOGGED);
            if (onType != onBlock)
            {
                reporter.warn("Block '{}' has inconsistent waterlogging configuration", block);
            }
        });

        reporter.endTest();
    }



    private WaterloggingPropertyConsistency() { }
}
