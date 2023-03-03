package xfacthd.framedblocks.cmdtests.tests;

import com.google.common.base.Stopwatch;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.cmdtests.SpecialTestCommand;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public final class SkipPredicates
{
    private static final BlockType[] TYPES = BlockType.values();
    private static final Direction[] SIDES = Direction.values();
    public static final String NAME = "SkipPredicates";
    private static final String MSG_PREFIX = "[" + NAME + "] ";
    private static final String PROGRESS_MSG = MSG_PREFIX + "%,d";
    private static final String RESULT_MSG = MSG_PREFIX + "Tested %,d combinations in %dms. ";
    private static final BlockPos CENTER = new BlockPos(1, 1, 1);

    public static void testSkipPredicates(CommandContext<CommandSourceStack> ctx, Consumer<Component> msgQueueAppender)
    {
        List<Triple<BlockType, BlockType, Throwable>> errors = new ArrayList<>();
        AtomicInteger combinations = new AtomicInteger(0);
        AtomicInteger lastPrinted = new AtomicInteger(0);
        IntConsumer combinationCollector = i ->
        {
            int val = combinations.addAndGet(i);
            if (val - lastPrinted.get() > 10000000)
            {
                lastPrinted.set(val);
                msgQueueAppender.accept(new TextComponent(PROGRESS_MSG.formatted(val)));
            }
        };

        Stopwatch watch = Stopwatch.createStarted();
        ExecutorService exec = Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors() / 2, 1));
        List<CompletableFuture<Result>> futures = new ArrayList<>(TYPES.length);
        for (BlockType type : TYPES)
        {
            futures.add(
                    CompletableFuture.supplyAsync(
                            () -> testTypeAgainstAll(type, combinationCollector), exec
                    ).whenComplete((result, throwable) ->
                    {
                        synchronized (errors)
                        {
                            errors.addAll(result.errors);
                        }
                    })
            );
        }
        CompletableFuture<Void> future = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        future.join();
        exec.shutdown();

        watch.stop();
        long time = watch.elapsed(TimeUnit.MILLISECONDS);

        Component resultMsg = new TextComponent("No issues found");
        ChatFormatting color = ChatFormatting.DARK_GREEN;

        if (!errors.isEmpty())
        {
            StringBuilder testResult = new StringBuilder("Encountered errors while testing skip predicates:");
            for (Triple<BlockType, BlockType, Throwable> error : errors)
            {
                testResult.append("\n\t- Type: ")
                        .append(error.getLeft())
                        .append(", Against: ")
                        .append(error.getMiddle())
                        .append(", Error: ")
                        .append(error.getRight().getMessage());
            }

            Component exportMsg = SpecialTestCommand.writeResultToFile("skippredicates", testResult.toString());
            resultMsg = new TextComponent("Found %d issues. ".formatted(errors.size())).append(exportMsg);
            color = ChatFormatting.DARK_RED;
        }

        resultMsg = new TextComponent(RESULT_MSG.formatted(combinations.intValue(), time))
                .withStyle(color)
                .append(resultMsg);
        msgQueueAppender.accept(resultMsg);
    }

    private static Result testTypeAgainstAll(BlockType type, IntConsumer combinationCollector)
    {
        List<Triple<BlockType, BlockType, Throwable>> errors = new ArrayList<>();
        Block block = FBContent.byType(type);
        SideSkipPredicate skipPredicate = type.getSideSkipPredicate();
        int combinations = 0;
        int lastSent = 0;
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
                            errors.add(Triple.of(type, adjType, t));
                        }

                        combinations++;
                        if (combinations % 100000 == 0)
                        {
                            combinationCollector.accept(100000);
                            lastSent = combinations;
                        }
                    }
                }
            }
        }
        combinationCollector.accept(combinations - lastSent);

        return new Result(combinations, errors);
    }

    private record Result(int combinations, List<Triple<BlockType, BlockType, Throwable>> errors) { }



    private SkipPredicates() { }
}
