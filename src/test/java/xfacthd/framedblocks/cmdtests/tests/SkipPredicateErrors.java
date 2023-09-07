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
import xfacthd.framedblocks.util.AsyncTypeTest;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public final class SkipPredicateErrors
{
    private static final BlockType[] TYPES = BlockType.values();
    private static final Direction[] SIDES = Direction.values();
    public static final String NAME = "SkipPredicatesErrors";
    private static final String MSG_PREFIX = "[" + NAME + "] ";
    private static final String PROGRESS_MSG = MSG_PREFIX + "%,d";
    private static final String RESULT_MSG = MSG_PREFIX + "Tested %,d combinations in %dms. ";
    private static final BlockPos CENTER = new BlockPos(1, 1, 1);

    public static void testSkipPredicates(
            @SuppressWarnings("unused") CommandContext<CommandSourceStack> ctx, Consumer<Component> msgQueueAppender
    )
    {
        List<Error> errors = new ArrayList<>();
        AsyncTypeTest.Stats stats = AsyncTypeTest.execute(
                SkipPredicateErrors::testTypeAgainstAll,
                (result, error) -> errors.addAll(result.errors),
                PROGRESS_MSG,
                msgQueueAppender
        );

        MutableComponent resultMsg = Component.literal("No issues found");
        ChatFormatting color = ChatFormatting.DARK_GREEN;

        if (!errors.isEmpty())
        {
            StringBuilder testResult = new StringBuilder("Encountered errors while testing skip predicates (deduplicated):");
            Set<Error> deduplicated = new HashSet<>();
            for (Error error : errors)
            {
                if (!deduplicated.add(error))
                {
                    continue;
                }

                testResult.append("\n\t- Type: ")
                        .append(error.typeOne)
                        .append(", Against: ")
                        .append(error.typeTwo())
                        .append(", Error: ")
                        .append(error.error);
            }

            Component exportMsg = SpecialTestCommand.writeResultToFile("skippredicates_errors", testResult.toString());
            resultMsg = Component.literal("Found %d issues. ".formatted(errors.size())).append(exportMsg);
            color = ChatFormatting.DARK_RED;
        }

        resultMsg = Component.literal(RESULT_MSG.formatted(stats.combinations(), stats.time()))
                .withStyle(color)
                .append(resultMsg);
        msgQueueAppender.accept(resultMsg);
    }

    private static Result testTypeAgainstAll(BlockType type, LongConsumer combinationCollector)
    {
        List<Error> errors = new ArrayList<>();
        Block block = FBContent.byType(type);
        SideSkipPredicate skipPredicate = type.getSideSkipPredicate();
        long combinations = 0;
        long lastSent = 0;
        for (BlockState state : block.getStateDefinition().getPossibleStates())
        {
            for (BlockType adjType : TYPES)
            {
                Block adjBlock = FBContent.byType(adjType);
                for (BlockState adjState : adjBlock.getStateDefinition().getPossibleStates())
                {
                    for (Direction side : SIDES)
                    {
                        try
                        {
                            skipPredicate.test(EmptyBlockGetter.INSTANCE, CENTER, state, adjState, side);
                        }
                        catch (Throwable t)
                        {
                            errors.add(new Error(type, adjType, t.getMessage()));
                        }

                        combinations++;
                        if (combinations % 100000L == 0)
                        {
                            combinationCollector.accept(100000L);
                            lastSent = combinations;
                        }
                    }
                }
            }
        }
        combinationCollector.accept(combinations - lastSent);

        return new Result(combinations, errors);
    }

    private record Error(BlockType typeOne, BlockType typeTwo, String error) { }

    private record Result(long combinations, List<Error> errors) { }



    private SkipPredicateErrors() { }
}
