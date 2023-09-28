package xfacthd.framedblocks.selftest.tests;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.cache.StateCache;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockStateCache;

import java.util.Arrays;
import java.util.List;

public final class StateCacheValidity
{
    private static final Direction[] DIRECTIONS = Direction.values();

    public static void checkStateCacheValid(List<Block> blocks)
    {
        FramedBlocks.LOGGER.info("  Checking validity of state caches");

        blocks.stream()
                .map(Block::getStateDefinition)
                .map(StateDefinition::getPossibleStates)
                .flatMap(List::stream).forEach(state ->
                {
                    StateCache cache = ((IFramedBlock) state.getBlock()).getCache(state);

                    boolean anyFullFace = Arrays.stream(DIRECTIONS).anyMatch(cache::isFullFace);
                    if (anyFullFace != cache.hasAnyFullFace())
                    {
                        FramedBlocks.LOGGER.warn(
                                "    StateCache of BlockState '{}' has inconsistency on 'anyFullFace' flag",
                                state
                        );
                    }

                    boolean anyDetailedCon = Arrays.stream(DIRECTIONS)
                            .flatMap(dir -> Arrays.stream(DIRECTIONS).map(edge -> Pair.of(dir, edge)))
                            .anyMatch(pair -> cache.canConnectDetailed(pair.getFirst(), pair.getSecond()));
                    if (anyDetailedCon != cache.hasAnyDetailedConnections())
                    {
                        FramedBlocks.LOGGER.warn(
                                "    StateCache of BlockState '{}' has inconsistency on 'anyFullFace' flag",
                                state
                        );
                    }

                    if (cache instanceof DoubleBlockStateCache doubleCache)
                    {
                        if (doubleCache.getTopInteractionMode() == null)
                        {
                            FramedBlocks.LOGGER.error(
                                    "    DoubleBlockStateCache of BlockState '{}' has invalid top interaction mode",
                                    state
                            );
                        }

                        Tuple<BlockState, BlockState> states = doubleCache.getBlockPair();
                        //noinspection ConstantConditions
                        if (states == null || states.getA() == null || states.getB() == null)
                        {
                            FramedBlocks.LOGGER.error(
                                    "    DoubleBlockStateCache of BlockState '{}' has invalid block pair",
                                    state
                            );
                        }

                        for (Direction side : DIRECTIONS)
                        {
                            if (doubleCache.getSolidityCheck(side) == null)
                            {
                                FramedBlocks.LOGGER.error(
                                        "    DoubleBlockStateCache of BlockState '{}' has invalid solidity check on side '{}'",
                                        state, side
                                );
                            }

                            if (doubleCache.getCamoGetter(side, null) == null)
                            {
                                FramedBlocks.LOGGER.error(
                                        "    DoubleBlockStateCache of BlockState '{}' has invalid camo getter on side '{}' for edge 'null'",
                                        state, side
                                );
                            }

                            for (Direction edge : DIRECTIONS)
                            {
                                if (doubleCache.getCamoGetter(side, edge) == null)
                                {
                                    FramedBlocks.LOGGER.error(
                                            "    DoubleBlockStateCache of BlockState '{}' has invalid camo getter on side '{}' for edge '{}'",
                                            state, side, edge
                                    );
                                }
                            }
                        }
                    }
                });
    }



    private StateCacheValidity() { }
}
