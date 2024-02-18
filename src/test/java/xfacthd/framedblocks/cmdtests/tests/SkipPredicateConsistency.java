package xfacthd.framedblocks.cmdtests.tests;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.cache.StateCache;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.cmdtests.SpecialTestCommand;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.selftest.tests.SkipPredicatePresenceConsistency;
import xfacthd.framedblocks.util.AsyncTypeTest;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SkipPredicateConsistency
{
    private static final BlockType[] TYPES = BlockType.values();
    private static final Direction[] SIDES = Direction.values();
    public static final String NAME = "SkipPredicatesConsistency";
    private static final String MSG_PREFIX = "[" + NAME + "] ";
    private static final String PROGRESS_MSG = MSG_PREFIX + "%,d";
    private static final String RESULT_MSG = MSG_PREFIX + "Tested %,d combinations in %dms. ";
    private static final BlockPos CENTER = new BlockPos(1, 1, 1);
    private static final Set<Property<?>> IGNORED_PROPERTIES = Stream.of(
            ClientUtils.IGNORE_DEFAULT_LOCK,
            List.of(BlockStateProperties.POWERED, FramedProperties.Y_SLOPE, FramedProperties.OFFSET)
    ).flatMap(List::stream).collect(Collectors.toSet());

    public static void testSkipPredicates(
            @SuppressWarnings("unused") CommandContext<CommandSourceStack> ctx, Consumer<Component> msgQueueAppender
    )
    {
        List<Inconsistency> inconsistencies = new ArrayList<>();
        AsyncTypeTest.Stats stats = AsyncTypeTest.execute(
                SkipPredicateConsistency::testTypeAgainstAll,
                (result, error) -> inconsistencies.addAll(result.inconsistencies),
                PROGRESS_MSG,
                msgQueueAppender
        );

        MutableComponent resultMsg = Component.literal("No issues found");
        ChatFormatting color = ChatFormatting.DARK_GREEN;

        if (!inconsistencies.isEmpty())
        {
            StringBuilder testResult = new StringBuilder("Encountered inconsistencies while testing skip predicates (deduplicated):");
            Set<Inconsistency> deduplicated = new HashSet<>();
            for (Inconsistency inconsistency : inconsistencies)
            {
                if (!deduplicated.add(inconsistency) || !deduplicated.add(inconsistency.mirror()))
                {
                    continue;
                }

                testResult.append("\n\t- Type: ")
                        .append(inconsistency.typeOne)
                        .append(", Against: ")
                        .append(inconsistency.typeTwo())
                        .append(", Side: ")
                        .append(inconsistency.side)
                        .append(", Result one: ")
                        .append(inconsistency.resOne)
                        .append(", Result two: ")
                        .append(inconsistency.resTwo);
            }

            Component exportMsg = SpecialTestCommand.writeResultToFile("skippredicates_inconsistencies", testResult.toString());
            resultMsg = Component.literal("Found %d inconsistencies. ".formatted(inconsistencies.size())).append(exportMsg);
            color = ChatFormatting.GOLD;
        }

        resultMsg = Component.literal(RESULT_MSG.formatted(stats.combinations(), stats.time()))
                .withStyle(color)
                .append(resultMsg);
        msgQueueAppender.accept(resultMsg);
    }

    private static Result testTypeAgainstAll(BlockType type, LongConsumer combinationCollector)
    {
        List<Inconsistency> inconsistencies = new ArrayList<>();
        Block block = FBContent.byType(type);
        SideSkipPredicate skipPredicate = type.getSideSkipPredicate();
        long combinations = 0;
        long lastSent = 0;
        for (BlockState state : block.getStateDefinition().getPossibleStates())
        {
            for (BlockType adjType : TYPES)
            {
                Block adjBlock = FBContent.byType(adjType);
                for (BlockState adjState : adjBlock.getStateDefinition().getPossibleStates())
                {
                    for (Direction side : SIDES)
                    {
                        if (!type.isDoubleBlock() && !adjType.isDoubleBlock())
                        {
                            StateCache cache = ((IFramedBlock) block).getCache(state);
                            StateCache adjCache = ((IFramedBlock) adjBlock).getCache(adjState);
                            if (!cache.isFullFace(side) && !adjCache.isFullFace(side.getOpposite()))
                            {
                                try
                                {
                                    boolean resultOne = skipPredicate.test(
                                            EmptyBlockGetter.INSTANCE, CENTER, state, adjState, side
                                    );
                                    boolean resultTwo = adjType.getSideSkipPredicate().test(
                                            EmptyBlockGetter.INSTANCE, CENTER, adjState, state, side.getOpposite()
                                    );

                                    if (resultOne != resultTwo && !isOneWayCombination(type, adjType))
                                    {
                                        state = resetIgnoredProperties(state);
                                        adjState = resetIgnoredProperties(adjState);
                                        inconsistencies.add(new Inconsistency(state, adjState, side, resultOne, resultTwo));
                                    }
                                }
                                catch (Throwable ignored) { }
                            }
                        }

                        combinations++;
                        if (combinations % 100000L == 0)
                        {
                            combinationCollector.accept(100000L);
                            lastSent = combinations;
                        }
                    }
                }
            }
        }
        combinationCollector.accept(combinations - lastSent);

        return new Result(combinations, inconsistencies);
    }

    private static boolean isOneWayCombination(BlockType type, BlockType adjType)
    {
        return SkipPredicatePresenceConsistency.getTestOf(type).oneWayTargets().contains(adjType) ||
               SkipPredicatePresenceConsistency.getTestOf(adjType).oneWayTargets().contains(type);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static BlockState resetIgnoredProperties(BlockState state)
    {
        BlockState defState = state.getBlock().defaultBlockState();
        for (Property prop : IGNORED_PROPERTIES)
        {
            if (state.hasProperty(prop))
            {
                state = state.setValue(prop, defState.getValue(prop));
            }
        }
        return state;
    }

    private record Inconsistency(BlockState typeOne, BlockState typeTwo, Direction side, boolean resOne, boolean resTwo)
    {
        Inconsistency mirror()
        {
            return new Inconsistency(typeTwo, typeOne, side.getOpposite(), resTwo, resOne);
        }
    }

    private record Result(long combinations, List<Inconsistency> inconsistencies) { }



    private SkipPredicateConsistency() { }
}
