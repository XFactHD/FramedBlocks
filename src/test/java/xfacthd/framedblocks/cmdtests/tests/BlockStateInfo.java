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
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.cmdtests.SpecialTestCommand;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

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
            ClientUtils.IGNORE_DEFAULT_LOCK
    ).flatMap(List::stream).collect(Collectors.toSet());

    public static int dumpBlockStateInfo(CommandContext<CommandSourceStack> ctx)
    {
        Stopwatch watch = Stopwatch.createStarted();

        StringBuilder dump = new StringBuilder("| Block | State count | Model state count | Solid | Glowing | Skylight | Waterlogging | State lock |\n");
        dump.append("| --- | --- | --- | --- | --- | --- | --- | --- |\n");

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

            dump.append("| ")
                    .append(name)
                    .append(" | %,d | ".formatted(stateCount))
                    .append(" %,d | ".formatted(modelStateCount))
                    .append(solid)
                    .append(" | ")
                    .append(glowing)
                    .append(" | ")
                    .append(skylight)
                    .append(" | ")
                    .append(waterlogging)
                    .append(" | ")
                    .append(stateLock)
                    .append(" |\n");

            totalStates += stateCount;
            totalModelStates += modelStateCount;
        }

        dump.append("\nBlock count: ").append(TYPES.length)
                .append("\\\nTotal states: ").append(totalStates)
                .append("\\\nTotal model states: ").append(totalModelStates)
                .append("\n");

        watch.stop();

        Component exportMsg = SpecialTestCommand.writeResultToFile("blockstate_info", "md", dump.toString());
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
