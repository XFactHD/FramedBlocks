package xfacthd.framedblocks.selftest.tests;

import net.minecraft.world.level.block.Block;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.IFramedDoubleBlock;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockStateCache;
import xfacthd.framedblocks.selftest.SelfTestReporter;

import java.util.List;

public final class DoubleBlockCamoConnectionConsistency
{
    public static void checkConnectionConsistency(SelfTestReporter reporter, List<Block> blocks)
    {
        reporter.startTest("camo connection consistency");

        blocks.stream()
                .filter(IFramedDoubleBlock.class::isInstance)
                .map(IFramedDoubleBlock.class::cast)
                .forEach(block -> ((Block) block).getStateDefinition().getPossibleStates().forEach(state ->
                {
                    DoubleBlockStateCache cache = block.getCache(state);
                    Utils.forAllDirections(false, side -> Utils.forAllDirections(edge ->
                    {
                        boolean connect = cache.canConnectFullEdge(side, edge);
                        boolean hasCamo = cache.getCamoGetter(side, edge) != CamoGetter.NONE;

                        if (connect != hasCamo)
                        {
                            reporter.warn(
                                    "Block '{}' has inconsistent camo-connection relation for state {} on side {} at edge {} (camo: {}, connect: {})",
                                    block, state, side, edge, hasCamo, connect
                            );
                        }
                    }));
                }));

        reporter.endTest();
    }



    private DoubleBlockCamoConnectionConsistency() { }
}
