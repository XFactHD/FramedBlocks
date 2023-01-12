package xfacthd.framedblocks.cmdtests.tests;

import com.google.common.base.Stopwatch;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.cmdtests.SpecialTestCommand;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class SkipPredicates
{
    public static final String NAME = "SkipPredicates";
    private static final String MSG_PREFIX = "[" + NAME + "] ";
    private static final String PROGRESS_MSG = MSG_PREFIX + "%,d";
    private static final String RESULT_MSG = MSG_PREFIX + "Tested %,d combinations in %dms. ";
    private static final BlockPos CENTER = new BlockPos(1, 1, 1);

    public static void testSkipPredicates(CommandContext<CommandSourceStack> ctx, Consumer<Component> msgQueueAppender)
    {
        BlockType[] types = BlockType.values();
        Direction[] sides = Direction.values();

        List<Triple<BlockType, BlockType, Throwable>> errors = new ArrayList<>();
        int combinations = 0;
        Stopwatch watch = Stopwatch.createStarted();
        for (BlockType type : types)
        {
            Block block = FBContent.byType(type);
            SideSkipPredicate skipPredicate = type.getSideSkipPredicate();
            for (BlockState state : block.getStateDefinition().getPossibleStates())
            {
                for (BlockType adjType : types)
                {
                    Block adjBlock = FBContent.byType(adjType);
                    for (BlockState adjState : adjBlock.getStateDefinition().getPossibleStates())
                    {
                        for (Direction side : sides)
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
                            if (combinations % 1000000 == 0)
                            {
                                msgQueueAppender.accept(Component.literal(PROGRESS_MSG.formatted(combinations)));
                            }
                        }
                    }
                }
            }
        }
        watch.stop();
        long time = watch.elapsed(TimeUnit.MILLISECONDS);

        Component resultMsg = Component.literal("No issues found");
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
            resultMsg = Component.literal("Found %d issues. ".formatted(errors.size())).append(exportMsg);
            color = ChatFormatting.DARK_RED;
        }

        resultMsg = Component.literal(RESULT_MSG.formatted(combinations, time))
                .withStyle(color)
                .append(resultMsg);
        msgQueueAppender.accept(resultMsg);
    }



    private SkipPredicates() { }
}
