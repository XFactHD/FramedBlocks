package io.github.xfacthd.framedblocks.cmdtests.tests;

import com.google.common.base.Stopwatch;
import com.mojang.brigadier.context.CommandContext;
import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.block.SimpleBlockCamoContainer;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedDoubleBlockData;
import io.github.xfacthd.framedblocks.api.util.SingleBlockFakeLevel;
import io.github.xfacthd.framedblocks.client.model.baked.FramedBlockModel;
import io.github.xfacthd.framedblocks.cmdtests.SpecialTestCommand;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.util.MarkdownTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.neoforged.neoforge.model.data.ModelData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;

public final class ModelPerformanceTest
{
    public static final String NAME = "ModelPerformance";
    private static final String PREFIX = "[" + NAME + "] ";
    private static final int RUNS = 10;
    private static final int SAMPLE_COUNT = 10_000;
    private static final Direction[] DIRECTIONS = Arrays.copyOf(Direction.values(), 7);
    private static final RandomSource RANDOM = new SingleThreadedRandomSource(RandomSupport.generateUniqueSeed());
    private static final CamoContainer<?, ?> TEST_CAMO_CONTAINER = new SimpleBlockCamoContainer(Blocks.STONE.defaultBlockState(), FBContent.FACTORY_BLOCK.get());
    private static final String STONE_NAME = BuiltInRegistries.BLOCK.getKey(Blocks.STONE).toString();

    public static void testModelPerformance(
            @SuppressWarnings("unused") CommandContext<CommandSourceStack> ctx, Consumer<Component> msgQueueAppender
    )
    {
        Map<String, BlockState> testStates = new LinkedHashMap<>();

        testStates.put(STONE_NAME, Blocks.STONE.defaultBlockState());
        for (BlockType type : BlockType.values())
        {
            BlockState state = FBContent.byType(type).defaultBlockState();
            String blockName = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
            testStates.put(blockName, state);
        }

        // Warmup runs
        msgQueueAppender.accept(Component.literal(PREFIX + "Warmup..."));
        for (BlockState state : testStates.values())
        {
            testModel(state, makeModelData(state, EmptyCamoContainer.EMPTY, false));
            testModel(state, makeModelData(state, TEST_CAMO_CONTAINER, false));
            testModel(state, makeModelData(state, TEST_CAMO_CONTAINER, true));
        }

        msgQueueAppender.accept(Component.literal(PREFIX + "Measure..."));
        Map<String, List<Result>> results = new LinkedHashMap<>();
        for (int i = 0; i < RUNS; i++)
        {
            msgQueueAppender.accept(Component.literal(PREFIX + "  Run " + (i + 1)));

            for (Map.Entry<String, BlockState> entry : testStates.entrySet())
            {
                BlockState state = entry.getValue();
                boolean stone = state.getBlock() == Blocks.STONE;
                long timeEmpty = testModel(state, makeModelData(state, EmptyCamoContainer.EMPTY, false));
                long timeCamo = stone ? 0 : testModel(state, makeModelData(state, TEST_CAMO_CONTAINER, false));
                long timeCamoEmissive = stone ? 0 : testModel(state, makeModelData(state, TEST_CAMO_CONTAINER, true));
                results.computeIfAbsent(entry.getKey(), $ -> new ArrayList<>()).add(new Result(timeEmpty, timeCamo, timeCamoEmissive));
            }
        }

        msgQueueAppender.accept(Component.literal(PREFIX + "Done, exporting results..."));

        MarkdownTable table = new MarkdownTable();
        table.header("Block");
        for (int i = 0; i < RUNS; i++)
        {
            table.header("Run %d".formatted(i + 1), true);
        }
        table.header("Average", true).header("Relative", true);

        int[] count = new int[1];
        long[] stoneAvg = new long[1];
        long[] allEmptyAvg = new long[results.size() - 1];
        float[] allEmptyRel = new float[results.size() - 1];
        long[] allCamoAvg = new long[results.size() - 1];
        float[] allCamoRel = new float[results.size() - 1];
        long[] allCamoEmissiveAvg = new long[results.size() - 1];
        float[] allCamoEmissiveRel = new float[results.size() - 1];
        results.forEach((name, values) ->
        {
            int index = count[0] - 1;
            computeAndPrintEntry(table, index, name, TestType.EMPTY, values, allEmptyAvg, allEmptyRel, stoneAvg);
            computeAndPrintEntry(table, index, name, TestType.CAMO, values, allCamoAvg, allCamoRel, stoneAvg);
            computeAndPrintEntry(table, index, name, TestType.CAMO_EMISSIVE, values, allCamoEmissiveAvg, allCamoEmissiveRel, stoneAvg);

            count[0]++;
        });

        StringBuilder data = new StringBuilder();

        int minBlank = 0;
        int maxBlank = 0;
        int minCamo = 0;
        int maxCamo = 0;
        int minCamoEmissive = 0;
        int maxCamoEmissive = 0;
        for (int i = 0; i < results.size() - 1; i++)
        {
            minBlank = compare(allEmptyAvg, i, minBlank, true);
            maxBlank = compare(allEmptyAvg, i, maxBlank, false);
            minCamo = compare(allCamoAvg, i, minCamo, true);
            maxCamo = compare(allCamoAvg, i, maxCamo, false);
            minCamoEmissive = compare(allCamoEmissiveAvg, i, minCamoEmissive, true);
            maxCamoEmissive = compare(allCamoEmissiveAvg, i, maxCamoEmissive, false);
        }

        data.append("Relative speed:\n")
                .append("- Min (blank):          ").append("%6.2f (%6d us)\n".formatted(allEmptyRel[minBlank], allEmptyAvg[minBlank]))
                .append("- Max (blank):          ").append("%6.2f (%6d us)\n".formatted(allEmptyRel[maxBlank], allEmptyAvg[maxBlank]))
                .append("- Min (camo):           ").append("%6.2f (%6d us)\n".formatted(allCamoRel[minCamo], allCamoAvg[minCamo]))
                .append("- Max (camo):           ").append("%6.2f (%6d us)\n".formatted(allCamoRel[maxCamo], allCamoAvg[maxCamo]))
                .append("- Min (camo emissive):  ").append("%6.2f (%6d us)\n".formatted(allCamoEmissiveRel[minCamoEmissive], allCamoEmissiveAvg[minCamoEmissive]))
                .append("- Max (camo emissive):  ").append("%6.2f (%6d us)\n".formatted(allCamoEmissiveRel[maxCamoEmissive], allCamoEmissiveAvg[maxCamoEmissive]))
                .append("\n\n").append(table.print());

        Component msg = SpecialTestCommand.writeResultToFile("modelperf", "md", data.toString());
        msgQueueAppender.accept(Component.literal(PREFIX).append(msg));
    }

    private static void computeAndPrintEntry(MarkdownTable table, int index, String name, TestType type, List<Result> values, long[] allAverage, float[] allRelative, long[] stoneAvg)
    {
        boolean stone = name.equals(STONE_NAME);
        if (stone && type != TestType.EMPTY) return;

        if (!stone)
        {
            name += " (" + type.suffix + ")";
        }
        table.cell(name);
        long total = 0;
        for (Result entry : values)
        {
            long time = type.resultGetter.applyAsLong(entry);
            table.cell("%6d us".formatted(time));
            total += time;
        }
        long average = total / RUNS;
        float relative = stone ? 1 : ((float) average / (float) stoneAvg[0]);
        table.cell("%6d us".formatted(average)).cell("%6.02f".formatted(relative)).newRow();

        if (stone)
        {
            stoneAvg[0] = average;
        }
        else
        {
            allAverage[index] = average;
            allRelative[index] = relative;
        }
    }

    private static int compare(long[] data, int idx, int prevIdx, boolean min)
    {
        if (min ? data[idx] < data[prevIdx] : data[idx] > data[prevIdx])
        {
            return idx;
        }
        return prevIdx;
    }

    private static long testModel(BlockState state, ModelData data)
    {
        BlockStateModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        if (model instanceof FramedBlockModel framedModel)
        {
            framedModel.clearCache();
        }

        BlockAndTintGetter level = SingleBlockFakeLevel.withoutRealLevel(state, data);

        Stopwatch watch = Stopwatch.createStarted();

        for (int i = 0; i < SAMPLE_COUNT; i++)
        {
            for (BlockModelPart part : model.collectParts(level, BlockPos.ZERO, state, RANDOM))
            {
                for (Direction side : DIRECTIONS)
                {
                    part.getQuads(side);
                }
            }
        }

        watch.stop();
        return watch.elapsed(TimeUnit.MICROSECONDS);
    }

    private static ModelData makeModelData(BlockState state, CamoContainer<?, ?> camo, boolean emissive)
    {
        AbstractFramedBlockData fbData;
        if (state.getBlock() instanceof IFramedDoubleBlock doubleBlock)
        {
            FramedBlockData dataOne = new FramedBlockData(camo, FramedBlockData.NO_CULLED_FACES, false, false, emissive, TriState.DEFAULT);
            FramedBlockData dataTwo = new FramedBlockData(camo, FramedBlockData.NO_CULLED_FACES, true, false, emissive, TriState.DEFAULT);
            fbData = new FramedDoubleBlockData(doubleBlock.getCache(state).getParts(), dataOne, dataTwo);
        }
        else
        {
            fbData = new FramedBlockData(camo, FramedBlockData.NO_CULLED_FACES, false, false, emissive, TriState.DEFAULT);
        }
        return ModelData.of(AbstractFramedBlockData.PROPERTY, fbData);
    }

    private record Result(long timeEmpty, long timeCamo, long timeCamoEmissive) { }

    private enum TestType
    {
        EMPTY("empty", Result::timeEmpty),
        CAMO("camo", Result::timeCamo),
        CAMO_EMISSIVE("camo emissive", Result::timeCamoEmissive),
        ;

        private final String suffix;
        private final ToLongFunction<Result> resultGetter;

        TestType(String suffix, ToLongFunction<Result> resultGetter)
        {
            this.suffix = suffix;
            this.resultGetter = resultGetter;
        }
    }

    private ModelPerformanceTest() { }
}
