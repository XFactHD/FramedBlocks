package xfacthd.framedblocks.cmdtests.tests;

import com.google.common.base.Stopwatch;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.wrapping.WrapHelper;
import xfacthd.framedblocks.cmdtests.SpecialTestCommand;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.util.MarkdownTable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class BlockStateInfo
{
    public static final String NAME = "BlockStateInfo";
    private static final String RESULT_MSG = "[" + NAME + "] Collected blockstate info for %s blocks in %s. ";
    private static final BlockType[] TYPES = BlockType.values();
    private static final Set<Property<?>> IGNORED_PROPERTIES = Stream.of(
            WrapHelper.IGNORE_DEFAULT_LOCK
    ).flatMap(List::stream).collect(Collectors.toSet());

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
                .header("State lock");

        long totalStates = 0;
        long totalModelStates = 0;
        for (BlockType type : TYPES)
        {
            Block block = FBContent.byType(type);
            //noinspection ConstantConditions
            String name = ForgeRegistries.BLOCKS.getKey(block).getPath();
            int stateCount = block.getStateDefinition().getPossibleStates().size();
            int modelStateCount = filterIgnoredProperties(block.defaultBlockState(), stateCount);

            String solid = type.canOccludeWithSolidCamo() ? checkBooleanProperty(block, FramedProperties.SOLID) : "-";
            String glowing = checkBooleanProperty(block, FramedProperties.GLOWING);
            String skylight = checkBooleanProperty(block, FramedProperties.PROPAGATES_SKYLIGHT);
            String waterlogging = type.supportsWaterLogging() ? checkBooleanProperty(block, BlockStateProperties.WATERLOGGED) : "-";
            String stateLock = type.canLockState() ? checkBooleanProperty(block, FramedProperties.STATE_LOCKED) : "-";

            table.cell(name)
                    .cell("%,d".formatted(stateCount))
                    .cell("%,d".formatted(modelStateCount))
                    .cell(solid)
                    .cell(glowing)
                    .cell(skylight)
                    .cell(waterlogging)
                    .cell(stateLock)
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

    private static int filterIgnoredProperties(BlockState defState, int stateCount)
    {
        for (Property<?> prop : IGNORED_PROPERTIES)
        {
            if (defState.hasProperty(prop))
            {
                stateCount /= prop.getPossibleValues().size();
            }
        }
        return stateCount;
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
