package xfacthd.framedblocks.cmdtests.tests;

import com.google.common.base.Stopwatch;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.client.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.cmdtests.SpecialTestCommand;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.util.MarkdownTable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class ModelPerformanceTest
{
    public static final String NAME = "ModelPerformance";
    private static final String PREFIX = "[" + NAME + "] ";
    private static final int RUNS = 10;
    private static final int SAMPLE_COUNT = 10_000;
    private static final Direction[] DIRECTIONS = Stream.concat(
            Arrays.stream(Direction.values()), Stream.of((Direction) null)
    ).toArray(Direction[]::new);
    private static final RandomSource RANDOM = RandomSource.create();
    private static final ModelData MODEL_DATA_EMPTY = makeModelData(Blocks.AIR.defaultBlockState());
    private static final ModelData MODEL_DATA_CAMO = makeModelData(Blocks.STONE.defaultBlockState());

    public static void testModelPerformance(
            @SuppressWarnings("unused") CommandContext<CommandSourceStack> ctx, Consumer<Component> msgQueueAppender
    )
    {
        Map<String, BlockState> testStates = new LinkedHashMap<>();

        String stoneName = BuiltInRegistries.BLOCK.getKey(Blocks.STONE).toString();
        testStates.put(stoneName, Blocks.STONE.defaultBlockState());
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
            testModel(state, MODEL_DATA_EMPTY);
            testModel(state, MODEL_DATA_CAMO);
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
                long timeEmpty = testModel(state, MODEL_DATA_EMPTY);
                long timeCamo = stone ? 0 : testModel(state, MODEL_DATA_CAMO);
                results.computeIfAbsent(entry.getKey(), $ -> new ArrayList<>()).add(new Result(timeEmpty, timeCamo));
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
        float[] allEmptyRel = new float[results.size() - 1];
        float[] allCamoRel = new float[results.size() - 1];
        results.forEach((name, values) ->
        {
            boolean stone = name.equals(stoneName);

            table.cell(name + " (empty)");
            long total = 0;
            for (Result entry : values)
            {
                table.cell("%6d us".formatted(entry.timeEmpty));
                total += entry.timeEmpty;
            }
            long emptyAvg = total / RUNS;
            float emptyRel = stone ? 1 : ((float) emptyAvg / (float) stoneAvg[0]);
            table.cell("%6d us".formatted(emptyAvg)).cell("%6.02f".formatted(emptyRel)).newRow();

            if (stone)
            {
                stoneAvg[0] = emptyAvg;
                count[0]++;
                return;
            }

            table.cell(name + " (camo)");
            total = 0;
            for (Result entry : values)
            {
                table.cell("%6d us".formatted(entry.timeCamo));
                total += entry.timeCamo;
            }
            long camoAvg = total / RUNS;
            float camoRel = ((float) camoAvg / (float) stoneAvg[0]);
            table.cell("%6d us".formatted(camoAvg)).cell("%6.02f".formatted(camoRel)).newRow();

            allEmptyRel[count[0] - 1] = emptyRel;
            allCamoRel[count[0] - 1] = camoRel;
            count[0]++;
        });

        StringBuilder data = new StringBuilder();

        float minBlank = Float.MAX_VALUE;
        float maxBlank = 0;
        float minCamo = Float.MAX_VALUE;
        float maxCamo = 0;
        for (int i = 0; i < results.size() - 1; i++)
        {
            minBlank = Math.min(allEmptyRel[i], minBlank);
            maxBlank = Math.max(allEmptyRel[i], maxBlank);
            minCamo = Math.min(allCamoRel[i], minCamo);
            maxCamo = Math.max(allCamoRel[i], maxCamo);
        }

        data.append("Relative speed:\n")
                .append("- Min (blank): ").append("%6.2f\n".formatted(minBlank))
                .append("- Max (blank): ").append("%6.2f\n".formatted(maxBlank))
                .append("- Min (camo):  ").append("%6.2f\n".formatted(minCamo))
                .append("- Max (camo):  ").append("%6.2f\n".formatted(maxCamo))
                .append("\n\n").append(table.print());

        Component msg = SpecialTestCommand.writeResultToFile("modelperf", "md", data.toString());
        msgQueueAppender.accept(Component.literal(PREFIX).append(msg));
    }

    private static long testModel(BlockState state, ModelData data)
    {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        if (model instanceof FramedBlockModel framedModel)
        {
            framedModel.clearCache();
        }

        Stopwatch watch = Stopwatch.createStarted();

        for (int i = 0; i < SAMPLE_COUNT; i++)
        {
            for (RenderType layer : model.getRenderTypes(state, RANDOM, data))
            {
                for (Direction side : DIRECTIONS)
                {
                    model.getQuads(state, side, RANDOM, data, layer);
                }
            }
        }

        watch.stop();
        return watch.elapsed(TimeUnit.MICROSECONDS);
    }

    private static ModelData makeModelData(BlockState camo)
    {
        FramedBlockData dataOne = new FramedBlockData(camo, false);
        FramedBlockData dataTwo = new FramedBlockData(camo, true);

        return ModelData.builder()
                .with(FramedBlockData.PROPERTY, dataOne)
                .with(FramedDoubleBlockEntity.DATA_LEFT, ModelData.builder()
                        .with(FramedBlockData.PROPERTY, dataOne)
                        .build()
                )
                .with(FramedDoubleBlockEntity.DATA_RIGHT, ModelData.builder()
                        .with(FramedBlockData.PROPERTY, dataTwo)
                        .build()
                )
                .build();
    }



    private record Result(long timeEmpty, long timeCamo) { }



    private ModelPerformanceTest() { }
}
