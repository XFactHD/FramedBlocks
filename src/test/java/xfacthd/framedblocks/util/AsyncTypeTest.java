package xfacthd.framedblocks.util;

import com.google.common.base.Stopwatch;
import net.minecraft.network.chat.Component;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.*;

public final class AsyncTypeTest
{
    private static final BlockType[] TYPES = BlockType.values();

    public static <T> Stats execute(
            TypeTestFunction<T> testFunc,
            ResultConsumer<T> resultConsumer,
            String progressMsgTemplate,
            Consumer<Component> msgQueueAppender
    )
    {
        AtomicLong combinations = new AtomicLong(0);
        AtomicLong lastPrinted = new AtomicLong(0);
        LongConsumer combinationCollector = i ->
        {
            long val = combinations.addAndGet(i);
            if (val - lastPrinted.get() > 10000000L)
            {
                lastPrinted.set(val);
                msgQueueAppender.accept(Component.literal(progressMsgTemplate.formatted(val)));
            }
        };

        Stopwatch watch = Stopwatch.createStarted();
        ExecutorService exec = Executors.newFixedThreadPool(Math.max((int) (Runtime.getRuntime().availableProcessors() * .75), 1));
        List<CompletableFuture<T>> futures = new ArrayList<>(TYPES.length);
        for (BlockType type : TYPES)
        {
            futures.add(
                    CompletableFuture.supplyAsync(
                            () -> testFunc.apply(type, combinationCollector), exec
                    ).whenComplete((result, error) ->
                    {
                        synchronized (resultConsumer)
                        {
                            resultConsumer.accept(result, error);
                        }
                    })
            );
        }
        CompletableFuture<Void> future = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        future.join();
        exec.shutdown();

        watch.stop();
        return new Stats(watch.elapsed(TimeUnit.MILLISECONDS), combinations.longValue());
    }



    @FunctionalInterface
    public interface TypeTestFunction<T> extends BiFunction<BlockType, LongConsumer, T>
    {
        @Override
        T apply(BlockType type, LongConsumer combinationCollector);
    }

    @FunctionalInterface
    public interface ResultConsumer<T> extends BiConsumer<T, Throwable>
    {
        @Override
        void accept(T result, Throwable error);
    }

    public record Stats(long time, long combinations) { }



    private AsyncTypeTest() { }
}
