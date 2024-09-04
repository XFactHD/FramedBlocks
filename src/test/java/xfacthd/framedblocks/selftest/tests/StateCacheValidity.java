package xfacthd.framedblocks.selftest.tests;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.cache.StateCache;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockStateCache;
import xfacthd.framedblocks.selftest.SelfTestReporter;

import java.util.Arrays;
import java.util.List;

public final class StateCacheValidity
{
    private static final Direction[] DIRECTIONS = Direction.values();

    public static void checkStateCacheValid(SelfTestReporter reporter, List<Block> blocks)
    {
        reporter.startTest("state caches validity");

        blocks.stream()
                .map(Block::getStateDefinition)
                .map(StateDefinition::getPossibleStates)
                .flatMap(List::stream).forEach(state ->
                {
                    StateCache cache = ((IFramedBlock) state.getBlock()).getCache(state);

                    boolean anyFullFace = Arrays.stream(DIRECTIONS).anyMatch(cache::isFullFace);
                    if (anyFullFace != cache.hasAnyFullFace())
                    {
                        reporter.warn("StateCache of BlockState '{}' has inconsistency on 'anyFullFace' flag", state);
                    }

                    boolean anyDetailedCon = Arrays.stream(DIRECTIONS)
                            .flatMap(dir -> Arrays.stream(DIRECTIONS).map(edge -> Pair.of(dir, edge)))
                            .anyMatch(pair -> cache.canConnectDetailed(pair.getFirst(), pair.getSecond()));
                    if (anyDetailedCon != cache.hasAnyDetailedConnections())
                    {
                        reporter.warn("StateCache of BlockState '{}' has inconsistency on 'anyFullFace' flag", state);
                    }

                    if (cache instanceof DoubleBlockStateCache doubleCache)
                    {
                        if (doubleCache.getTopInteractionMode() == null)
                        {
                            reporter.error("DoubleBlockStateCache of BlockState '{}' has invalid top interaction mode", state);
                        }

                        Tuple<BlockState, BlockState> states = doubleCache.getBlockPair();
                        //noinspection ConstantConditions
                        if (states == null || states.getA() == null || states.getB() == null)
                        {
                            reporter.error("DoubleBlockStateCache of BlockState '{}' has invalid block pair", state);
                        }

                        for (Direction side : DIRECTIONS)
                        {
                            if (doubleCache.getSolidityCheck(side) == null)
                            {
                                reporter.error(
                                        "DoubleBlockStateCache of BlockState '{}' has invalid solidity check on side '{}'",
                                        state, side
                                );
                            }

                            if (doubleCache.getCamoGetter(side, null) == null)
                            {
                                reporter.error(
                                        "DoubleBlockStateCache of BlockState '{}' has invalid camo getter on side '{}' for edge 'null'",
                                        state, side
                                );
                            }

                            for (Direction edge : DIRECTIONS)
                            {
                                if (doubleCache.getCamoGetter(side, edge) == null)
                                {
                                    reporter.error(
                                            "DoubleBlockStateCache of BlockState '{}' has invalid camo getter on side '{}' for edge '{}'",
                                            state, side, edge
                                    );
                                }
                            }
                        }
                    }
                });

        reporter.endTest();
    }



    private StateCacheValidity() { }
}
