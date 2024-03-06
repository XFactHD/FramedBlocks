package xfacthd.framedblocks.cmdtests.tests;

import com.google.common.base.Stopwatch;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.client.modelwrapping.ModelWrappingHandler;
import xfacthd.framedblocks.client.modelwrapping.ModelWrappingManager;
import xfacthd.framedblocks.cmdtests.SpecialTestCommand;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.util.MarkdownTable;

import java.util.stream.Collectors;

public final class BlockStateInfo
{
    public static final String NAME = "BlockStateInfo";
    private static final String RESULT_MSG = "[" + NAME + "] Collected blockstate info for %s blocks in %s. ";
    private static final BlockType[] TYPES = BlockType.values();

    public static int dumpBlockStateInfo(CommandContext<CommandSourceStack> ctx)
    {
        Stopwatch watch = Stopwatch.createStarted();

        MarkdownTable table = new MarkdownTable();
        table.header("Block")
                .header("State count", true)
                .header("Model state count", true)
                .header("Solid")
                .header("Glowing")
                .header("Skylight")
                .header("Waterlogging")
                .header("State lock")
                .header("Ignored properties");

        long totalStates = 0;
        long totalModelStates = 0;
        for (BlockType type : TYPES)
        {
            Block block = FBContent.byType(type);
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            ModelWrappingHandler wrapper = ModelWrappingManager.getHandler(block);
            int stateCount = block.getStateDefinition().getPossibleStates().size();
            int modelStateCount = wrapper.getVisitedStateCount();

            String solid = type.canOccludeWithSolidCamo() ? checkBooleanProperty(block, FramedProperties.SOLID) : "-";
            String glowing = checkBooleanProperty(block, FramedProperties.GLOWING);
            String skylight = checkBooleanProperty(block, FramedProperties.PROPAGATES_SKYLIGHT);
            String waterlogging = type.supportsWaterLogging() ? checkBooleanProperty(block, BlockStateProperties.WATERLOGGED) : "-";
            String stateLock = type.canLockState() ? checkBooleanProperty(block, FramedProperties.STATE_LOCKED) : "-";
            String ignoredProperties = printIgnoredProperties(wrapper, block);

            table.cell(name)
                    .cell("%,d".formatted(stateCount))
                    .cell("%,d".formatted(modelStateCount))
                    .cell(solid)
                    .cell(glowing)
                    .cell(skylight)
                    .cell(waterlogging)
                    .cell(stateLock)
                    .cell(ignoredProperties)
                    .newRow();

            totalStates += stateCount;
            totalModelStates += modelStateCount;
        }

        String dump = table.print() +
                "\nBlock count: " + TYPES.length +
                "\\\nTotal states: " + totalStates +
                "\\\nTotal model states: " + totalModelStates +
                "\n";

        watch.stop();

        Component exportMsg = SpecialTestCommand.writeResultToFile("blockstate_info", "md", dump);
        Component resultMsg = Component.literal(RESULT_MSG.formatted(TYPES.length, watch)).append(exportMsg);
        ctx.getSource().sendSuccess(() -> resultMsg, true);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("deprecation")
    private static String printIgnoredProperties(ModelWrappingHandler wrapper, Block block)
    {
        return wrapper.getStateMerger()
                .getHandledProperties(block.builtInRegistryHolder())
                .stream()
                .filter(block.defaultBlockState()::hasProperty)
                .map(Property::getName)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private static String checkBooleanProperty(Block block, BooleanProperty property)
    {
        if (!block.defaultBlockState().hasProperty(property))
        {
            return "missing";
        }
        else if (block.defaultBlockState().getValue(property))
        {
            return "wrong default";
        }
        return "present";
    }



    private BlockStateInfo() { }
}
