package xfacthd.framedblocks.selftest.tests;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.IFramedBlock;

import java.util.List;

public final class WaterloggingPropertyConsistency
{
    public static void checkWaterloggingProperty(List<Block> blocks)
    {
        FramedBlocks.LOGGER.info("  Checking waterlogging property");

        blocks.forEach(block ->
        {
            boolean onType = ((IFramedBlock) block).getBlockType().supportsWaterLogging();
            boolean onBlock = block.defaultBlockState().hasProperty(BlockStateProperties.WATERLOGGED);
            if (onType != onBlock)
            {
                FramedBlocks.LOGGER.warn("    Block '{}' has inconsistent waterlogging configuration", block);
            }
        });
    }



    private WaterloggingPropertyConsistency() { }
}
