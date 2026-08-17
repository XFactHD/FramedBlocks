package io.github.xfacthd.framedblocks.cmdtests.tests;

import com.google.common.base.Stopwatch;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.github.xfacthd.framedblocks.api.block.CopycatStyleBlock;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.ShapeLockableBlock;
import io.github.xfacthd.framedblocks.client.model.wrapping.ModelWrappingHandler;
import io.github.xfacthd.framedblocks.client.model.wrapping.ModelWrappingManager;
import io.github.xfacthd.framedblocks.cmdtests.SpecialTestCommand;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.util.MarkdownTable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.stream.Collectors;

public final class BlockStateInfo {
    public static final String NAME = "BlockStateInfo";
    private static final String RESULT_MSG = "[" + NAME + "] Collected blockstate info for %s blocks in %s. ";
    private static final BlockType[] TYPES = BlockType.values();

    public static int dumpBlockStateInfo(CommandContext<CommandSourceStack> ctx) {
        Stopwatch watch = Stopwatch.createStarted();

        MarkdownTable table = new MarkdownTable();
        table.header("Block")
                .header("State count", true)
                .header("Model state count", true)
                .header("BlockOverlays")
                .header("Solid")
                .header("Glowing")
                .header("Skylight")
                .header("Waterlogging")
                .header("State lock")
                .header("Copycat")
                .header("Ignored properties");

        long totalPlaceableBlocks = 0;
        long totalDoubleBlocks = 0;
        long totalBlocksWithOverlays = 0;
        long totalBlocksWithCopycat = 0;
        long totalStates = 0;
        long totalModelStates = 0;
        long totalStatesWithOverlays = 0;
        long totalModelStatesWithOverlays = 0;
        for (BlockType type : TYPES) {
            Block block = FBContent.byType(type);
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            ModelWrappingHandler wrapper = ModelWrappingManager.getHandler(block);
            int stateCount = block.getStateDefinition().getPossibleStates().size();
            int modelStateCount = wrapper.getVisitedStateCount();

            String blockOverlays = type.supportsBlockOverlays() ? "supported" : "-";
            String solid = type.canOccludeWithSolidCamo() ? checkBooleanProperty(block, FramedProperties.SOLID) : "-";
            String glowing = checkBooleanProperty(block, FramedProperties.GLOWING);
            String skylight = checkBooleanProperty(block, FramedProperties.PROPAGATES_SKYLIGHT);
            String waterlogging = type.supportsWaterLogging() ? checkBooleanProperty(block, BlockStateProperties.WATERLOGGED) : "-";
            String stateLock = block instanceof ShapeLockableBlock ? checkBooleanProperty(block, FramedProperties.STATE_LOCKED) : "-";
            String copycat = switch (block) {
                case CopycatStyleBlock.Always _ -> "always";
                case CopycatStyleBlock.StateDependent _ -> "state";
                default -> "-";
            };
            String ignoredProperties = printIgnoredProperties(wrapper, block);

            table.cell(name)
                    .cell("%,d".formatted(stateCount))
                    .cell("%,d".formatted(modelStateCount))
                    .cell(blockOverlays)
                    .cell(solid)
                    .cell(glowing)
                    .cell(skylight)
                    .cell(waterlogging)
                    .cell(stateLock)
                    .cell(copycat)
                    .cell(ignoredProperties)
                    .newRow();

            totalStates += stateCount;
            totalModelStates += modelStateCount;
            if (type.hasBlockItem()) {
                totalPlaceableBlocks++;
            }
            if (type.isDoubleBlock()) {
                totalDoubleBlocks++;
            }
            if (type.supportsBlockOverlays()) {
                totalBlocksWithOverlays++;
                totalStatesWithOverlays += stateCount;
                totalModelStatesWithOverlays += modelStateCount;
            }
            if (block instanceof CopycatStyleBlock) {
                totalBlocksWithCopycat++;
            }
        }

        String dump = table.print() +
                "\nBlock count: " + TYPES.length +
                "\\\n↳ With item: " + totalPlaceableBlocks +
                "\\\n↳ With two camos: " + totalDoubleBlocks +
                "\\\n↳ With BlockOverlays: " + totalBlocksWithOverlays +
                "\\\n↳ With copycat: " + totalBlocksWithCopycat +
                "\\\nTotal states: " + totalStates +
                "\\\n↳ With BlockOverlays: " + totalStatesWithOverlays +
                "\\\nTotal model states: " + totalModelStates +
                "\\\n↳ With BlockOverlays: " + totalModelStatesWithOverlays +
                "\n";

        watch.stop();

        Component exportMsg = SpecialTestCommand.writeResultToFile("blockstate_info", "md", dump);
        Component resultMsg = Component.literal(RESULT_MSG.formatted(TYPES.length, watch)).append(exportMsg);
        ctx.getSource().sendSuccess(() -> resultMsg, true);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("deprecation")
    private static String printIgnoredProperties(ModelWrappingHandler wrapper, Block block) {
        return wrapper.getStateMerger()
                .getHandledProperties()
                .stream()
                .filter(block.defaultBlockState()::hasProperty)
                .map(Property::getName)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private static String checkBooleanProperty(Block block, BooleanProperty property) {
        if (!block.defaultBlockState().hasProperty(property)) {
            return "missing";
        }
        if (block.defaultBlockState().getValue(property)) {
            return "wrong default";
        }
        return "present";
    }

    private BlockStateInfo() { }
}
