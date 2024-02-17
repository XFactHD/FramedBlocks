package xfacthd.framedblocks.cmdtests.tests;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.cmdtests.SpecialTestCommand;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.selftest.tests.SkipPredicatePresenceConsistency;
import xfacthd.framedblocks.util.AsyncTypeTest;

import java.util.*;
import java.util.function.*;

public final class SkipPredicateRedundancy
{
    private static final BlockType[] TYPES = BlockType.values();
    private static final Direction[] SIDES = Direction.values();
    public static final String NAME = "SkipPredicatesRedundancy";
    private static final String MSG_PREFIX = "[" + NAME + "] ";
    private static final String PROGRESS_MSG = MSG_PREFIX + "%,d";
    private static final String RESULT_MSG = MSG_PREFIX + "Tested %,d combinations in %dms. ";
    private static final BlockPos CENTER = new BlockPos(1, 1, 1);
    // Set of blocks which are known to appear redundant when tested against themselves
    private static final Set<BlockType> IGNORED_SELF_TESTS = EnumSet.of(BlockType.FRAMED_COLLAPSIBLE_BLOCK);

    public static void testSkipPredicates(
            @SuppressWarnings("unused") CommandContext<CommandSourceStack> ctx, Consumer<Component> msgQueueAppender
    )
    {
        List<Redundancy> redundancies = new ArrayList<>();
        AsyncTypeTest.Stats stats = AsyncTypeTest.execute(
                SkipPredicateRedundancy::testTypeAgainstAll,
                (result, error) -> redundancies.addAll(result.redundancies),
                PROGRESS_MSG,
                msgQueueAppender
        );

        MutableComponent resultMsg = Component.literal("No issues found");
        ChatFormatting color = ChatFormatting.DARK_GREEN;

        if (!redundancies.isEmpty())
        {
            StringBuilder testResult = new StringBuilder("Encountered redundant tests while testing skip predicates (deduplicated):");
            Set<Redundancy> deduplicated = new HashSet<>();
            for (Redundancy redundancy : redundancies)
            {
                if (!deduplicated.add(redundancy))
                {
                    continue;
                }

                testResult.append("\n\t- Type: ")
                        .append(redundancy.typeOne)
                        .append(", Against: ")
                        .append(redundancy.typeTwo);
            }

            Component exportMsg = SpecialTestCommand.writeResultToFile("skippredicates_redundancies", testResult.toString());
            resultMsg = Component.literal("Found %d issues. ".formatted(redundancies.size())).append(exportMsg);
            color = ChatFormatting.DARK_RED;
        }

        resultMsg = Component.literal(RESULT_MSG.formatted(stats.combinations(), stats.time()))
                .withStyle(color)
                .append(resultMsg);
        msgQueueAppender.accept(resultMsg);
    }

    private static Result testTypeAgainstAll(BlockType type, LongConsumer combinationCollector)
    {
        List<Redundancy> redundancies = new ArrayList<>();
        Block block = FBContent.byType(type);
        SideSkipPredicate skipPredicate = type.getSideSkipPredicate();
        long[] combinations = new long[1];
        long[] lastSent = new long[1];
        for (BlockType adjType : TYPES)
        {
            if (type == adjType && IGNORED_SELF_TESTS.contains(type))
            {
                continue;
            }

            SkipPredicatePresenceConsistency.Test test = SkipPredicatePresenceConsistency.getTestOf(type);
            if (test == null || !test.targets().contains(adjType))
            {
                // No point in checking for redundancy if no test is present -> bail out early
                continue;
            }

            Block adjBlock = FBContent.byType(adjType);
            boolean result = testTypeAgainstType(block, adjBlock, skipPredicate, combinations, lastSent, combinationCollector);

            if (!result)
            {
                redundancies.add(new Redundancy(type, adjType));
            }
        }
        combinationCollector.accept(combinations[0] - lastSent[0]);

        return new Result(combinations[0], redundancies);
    }

    private static boolean testTypeAgainstType(
            Block block,
            Block adjBlock,
            SideSkipPredicate skipPredicate,
            long[] combinations,
            long[] lastSent,
            LongConsumer combinationCollector
    )
    {
        for (BlockState state : block.getStateDefinition().getPossibleStates())
        {
            for (BlockState adjState : adjBlock.getStateDefinition().getPossibleStates())
            {
                for (Direction side : SIDES)
                {
                    if (skipPredicate.test(EmptyBlockGetter.INSTANCE, CENTER, state, adjState, side))
                    {
                        // Bail out early on the first test that returns true, no point in checking further
                        return true;
                    }

                    combinations[0]++;
                    if (combinations[0] % 100000L == 0)
                    {
                        combinationCollector.accept(100000L);
                        lastSent[0] = combinations[0];
                    }
                }
            }
        }
        return false;
    }

    private record Redundancy(BlockType typeOne, BlockType typeTwo) { }

    private record Result(long combinations, List<Redundancy> redundancies) { }



    private SkipPredicateRedundancy() { }
}
