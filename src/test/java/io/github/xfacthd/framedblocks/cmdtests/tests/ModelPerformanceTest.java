package io.github.xfacthd.framedblocks.cmdtests.tests;

import com.google.common.base.Stopwatch;
import com.mojang.brigadier.context.CommandContext;
import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.block.overlay.TintSource;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.block.SimpleBlockCamoContainer;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedDoubleBlockData;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.render.fakelevel.FreestandingBlockRenderFakeLevel;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.client.model.baked.FramedBlockStateModel;
import io.github.xfacthd.framedblocks.cmdtests.SpecialTestCommand;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.util.MarkdownTable;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;

public final class ModelPerformanceTest {
    public static final String NAME = "ModelPerformance";
    private static final String PREFIX = "[" + NAME + "] ";
    private static final int RUNS = 10;
    private static final int SAMPLE_COUNT = 10_000;
    private static final List<BlockStateModelPart> PART_SCRATCH_LIST = new ObjectArrayList<>();
    private static final Direction[] DIRECTIONS = Arrays.copyOf(Direction.values(), 7);
    private static final RandomSource RANDOM = new SingleThreadedRandomSource(RandomSupport.generateUniqueSeed());
    private static final CamoContainer<?, ?> TEST_CAMO_CONTAINER = new SimpleBlockCamoContainer(Blocks.STONE.defaultBlockState(), FBContent.FACTORY_BLOCK.get());
    private static final Holder<BlockOverlay> TEST_OVERLAY = Holder.direct(new BlockOverlay(
            Utils.id("minecraft", "block/grass_block_top"),
            Utils.id("minecraft", "block/grass_block_side_overlay"),
            BlockOverlay.SolidFace.TOP,
            new TintSource(Holder.direct(Blocks.GRASS_BLOCK)),
            Holder.direct(Items.SHORT_GRASS),
            false
    ));
    private static final String STONE_NAME = BuiltInRegistries.BLOCK.getKey(Blocks.STONE).toString();
    // Exclude banners, they use empty models, poisoning the data
    private static final Set<BlockType> EXCLUDED = Set.of(BlockType.FRAMED_BANNER, BlockType.FRAMED_WALL_BANNER);

    public static void testModelPerformance(CommandContext<CommandSourceStack> ignored, Consumer<Component> msgQueueAppender) {
        Map<String, BlockState> testStates = new LinkedHashMap<>();

        testStates.put(STONE_NAME, Blocks.STONE.defaultBlockState());
        for (BlockType type : BlockType.values()) {
            if (EXCLUDED.contains(type)) {
                continue;
            }

            BlockState state = FBContent.byType(type).defaultBlockState();
            String blockName = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
            testStates.put(blockName, state);
        }

        // Warmup runs
        msgQueueAppender.accept(Component.literal(PREFIX + "Warmup..."));
        for (BlockState state : testStates.values()) {
            testModel(state, makeModelData(state, EmptyCamoContainer.EMPTY, false, null));
            testModel(state, makeModelData(state, TEST_CAMO_CONTAINER, false, null));
            testModel(state, makeModelData(state, TEST_CAMO_CONTAINER, true, null));
            testModel(state, makeModelData(state, TEST_CAMO_CONTAINER, false, TEST_OVERLAY));
        }

        msgQueueAppender.accept(Component.literal(PREFIX + "Measure..."));
        Map<String, List<Result>> results = new LinkedHashMap<>();
        for (int i = 0; i < RUNS; i++) {
            msgQueueAppender.accept(Component.literal(PREFIX + "  Run " + (i + 1)));

            for (Map.Entry<String, BlockState> entry : testStates.entrySet()) {
                BlockState state = entry.getValue();
                boolean stone = state.getBlock() == Blocks.STONE;
                long timeEmpty = testModel(state, makeModelData(state, EmptyCamoContainer.EMPTY, false, null));
                long timeCamo = stone ? 0 : testModel(state, makeModelData(state, TEST_CAMO_CONTAINER, false, null));
                long timeCamoEmissive = stone ? 0 : testModel(state, makeModelData(state, TEST_CAMO_CONTAINER, true, null));
                long timeCamoOverlay = stone ? 0 : testModel(state, makeModelData(state, TEST_CAMO_CONTAINER, true, TEST_OVERLAY));
                results.computeIfAbsent(entry.getKey(), _ -> new ArrayList<>()).add(new Result(timeEmpty, timeCamo, timeCamoEmissive, timeCamoOverlay));
            }
        }

        msgQueueAppender.accept(Component.literal(PREFIX + "Done, exporting results..."));

        MarkdownTable table = new MarkdownTable();
        table.header("Block");
        for (int i = 0; i < RUNS; i++) {
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
        long[] allCamoOverlayAvg = new long[results.size() - 1];
        float[] allCamoOverlayRel = new float[results.size() - 1];
        results.forEach((name, values) -> {
            int index = count[0] - 1;
            computeAndPrintEntry(table, index, name, TestType.EMPTY, values, allEmptyAvg, allEmptyRel, stoneAvg);
            computeAndPrintEntry(table, index, name, TestType.CAMO, values, allCamoAvg, allCamoRel, stoneAvg);
            computeAndPrintEntry(table, index, name, TestType.CAMO_EMISSIVE, values, allCamoEmissiveAvg, allCamoEmissiveRel, stoneAvg);
            computeAndPrintEntry(table, index, name, TestType.CAMO_OVERLAY, values, allCamoOverlayAvg, allCamoOverlayRel, stoneAvg);

            count[0]++;
        });

        StringBuilder data = new StringBuilder();

        int minBlank = 0;
        int maxBlank = 0;
        int minCamo = 0;
        int maxCamo = 0;
        int minCamoEmissive = 0;
        int maxCamoEmissive = 0;
        int minCamoOverlay = 0;
        int maxCamoOverlay = 0;
        for (int i = 0; i < results.size() - 1; i++) {
            minBlank = compare(allEmptyAvg, i, minBlank, true);
            maxBlank = compare(allEmptyAvg, i, maxBlank, false);
            minCamo = compare(allCamoAvg, i, minCamo, true);
            maxCamo = compare(allCamoAvg, i, maxCamo, false);
            minCamoEmissive = compare(allCamoEmissiveAvg, i, minCamoEmissive, true);
            maxCamoEmissive = compare(allCamoEmissiveAvg, i, maxCamoEmissive, false);
            minCamoOverlay = compare(allCamoOverlayAvg, i, minCamoOverlay, true);
            maxCamoOverlay = compare(allCamoOverlayAvg, i, maxCamoOverlay, false);
        }

        data.append("Relative speed:\n")
                .append("- Min (blank):          ").append("%6.2f (%6d us)\n".formatted(allEmptyRel[minBlank], allEmptyAvg[minBlank]))
                .append("- Max (blank):          ").append("%6.2f (%6d us)\n".formatted(allEmptyRel[maxBlank], allEmptyAvg[maxBlank]))
                .append("- Min (camo):           ").append("%6.2f (%6d us)\n".formatted(allCamoRel[minCamo], allCamoAvg[minCamo]))
                .append("- Max (camo):           ").append("%6.2f (%6d us)\n".formatted(allCamoRel[maxCamo], allCamoAvg[maxCamo]))
                .append("- Min (camo emissive):  ").append("%6.2f (%6d us)\n".formatted(allCamoEmissiveRel[minCamoEmissive], allCamoEmissiveAvg[minCamoEmissive]))
                .append("- Max (camo emissive):  ").append("%6.2f (%6d us)\n".formatted(allCamoEmissiveRel[maxCamoEmissive], allCamoEmissiveAvg[maxCamoEmissive]))
                .append("- Min (camo overlay):   ").append("%6.2f (%6d us)\n".formatted(allCamoOverlayRel[minCamoOverlay], allCamoOverlayAvg[minCamoOverlay]))
                .append("- Max (camo overlay):   ").append("%6.2f (%6d us)\n".formatted(allCamoOverlayRel[maxCamoOverlay], allCamoOverlayAvg[maxCamoOverlay]))
                .append("\n\n").append(table.print());

        Component msg = SpecialTestCommand.writeResultToFile("modelperf", "md", data.toString());
        msgQueueAppender.accept(Component.literal(PREFIX).append(msg));
    }

    private static void computeAndPrintEntry(MarkdownTable table, int index, String name, TestType type, List<Result> values, long[] allAverage, float[] allRelative, long[] stoneAvg) {
        boolean stone = name.equals(STONE_NAME);
        if (stone && type != TestType.EMPTY) { return; }

        if (!stone) {
            name += " (" + type.suffix + ")";
        }
        table.cell(name);
        long total = 0;
        for (Result entry : values) {
            long time = type.resultGetter.applyAsLong(entry);
            table.cell("%6d us".formatted(time));
            total += time;
        }
        long average = total / RUNS;
        float relative = stone ? 1 : ((float) average / (float) stoneAvg[0]);
        table.cell("%6d us".formatted(average)).cell("%6.02f".formatted(relative)).newRow();

        if (stone) {
            stoneAvg[0] = average;
        } else {
            allAverage[index] = average;
            allRelative[index] = relative;
        }
    }

    private static int compare(long[] data, int idx, int prevIdx, boolean min) {
        if (min ? data[idx] < data[prevIdx] : data[idx] > data[prevIdx]) {
            return idx;
        }
        return prevIdx;
    }

    private static long testModel(BlockState state, ModelData data) {
        BlockStateModel model = ModelUtils.getModel(state);
        if (model instanceof FramedBlockStateModel framedModel) {
            framedModel.clearCache();
        }

        BlockAndTintGetter level = new FreestandingBlockRenderFakeLevel.Simple(state, data);

        Stopwatch watch = Stopwatch.createStarted();

        for (int i = 0; i < SAMPLE_COUNT; i++) {
            model.collectParts(level, BlockPos.ZERO, state, RANDOM, PART_SCRATCH_LIST);
            for (BlockStateModelPart part : PART_SCRATCH_LIST) {
                for (Direction side : DIRECTIONS) {
                    part.getQuads(side);
                }
            }
            PART_SCRATCH_LIST.clear();
        }

        watch.stop();
        return watch.elapsed(TimeUnit.MICROSECONDS);
    }

    private static ModelData makeModelData(BlockState state, CamoContainer<?, ?> camo, boolean emissive, @Nullable Holder<BlockOverlay> overlay) {
        AbstractFramedBlockData fbData;
        if (state.getBlock() instanceof IFramedDoubleBlock doubleBlock) {
            FramedBlockData dataOne = new FramedBlockData(state, camo, (byte) 0, false, false, emissive, TriState.DEFAULT, overlay);
            FramedBlockData dataTwo = new FramedBlockData(state, camo, (byte) 0, true, false, emissive, TriState.DEFAULT, null);
            fbData = new FramedDoubleBlockData(doubleBlock.getCache(state).getParts(), dataOne, dataTwo);
        } else {
            fbData = new FramedBlockData(state, camo, (byte) 0, false, false, emissive, TriState.DEFAULT, overlay);
        }
        return ModelData.of(AbstractFramedBlockData.PROPERTY, fbData);
    }

    private record Result(long timeEmpty, long timeCamo, long timeCamoEmissive, long timeCamoOverlay) { }

    private enum TestType {
        EMPTY("empty", Result::timeEmpty),
        CAMO("camo", Result::timeCamo),
        CAMO_EMISSIVE("camo emissive", Result::timeCamoEmissive),
        CAMO_OVERLAY("camo overlay", Result::timeCamoOverlay),
        ;

        private final String suffix;
        private final ToLongFunction<Result> resultGetter;

        TestType(String suffix, ToLongFunction<Result> resultGetter) {
            this.suffix = suffix;
            this.resultGetter = resultGetter;
        }
    }

    private ModelPerformanceTest() { }
}
