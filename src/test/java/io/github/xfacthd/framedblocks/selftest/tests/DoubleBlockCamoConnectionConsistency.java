package io.github.xfacthd.framedblocks.selftest.tests;

import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.cache.DoubleBlockStateCache;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.selftest.SelfTestReporter;
import net.minecraft.world.level.block.Block;

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
                    DirUtils.forAllDirections(side -> DirUtils.forAllDirectionsAndNull(edge ->
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
