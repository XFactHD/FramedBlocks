package io.github.xfacthd.framedblocks.selftest.tests;

import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.client.model.wrapping.ModelWrappingHandler;
import io.github.xfacthd.framedblocks.client.model.wrapping.ModelWrappingManager;
import io.github.xfacthd.framedblocks.selftest.SelfTestReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;

public final class DoubleBlockPartModelStateConsistency
{
    public static void checkBlockPartConsistency(SelfTestReporter reporter, List<Block> blocks)
    {
        reporter.startTest("block part consistency");

        blocks.stream()
                .filter(IFramedDoubleBlock.class::isInstance)
                .map(IFramedDoubleBlock.class::cast)
                .forEach(block -> ((Block) block).getStateDefinition().getPossibleStates().forEach(state ->
                {
                    DoubleBlockParts parts = block.getCache(state).getParts();
                    testPart(reporter, block, parts.stateOne());
                    testPart(reporter, block, parts.stateTwo());
                }));

        reporter.endTest();
    }

    private static void testPart(SelfTestReporter reporter, IFramedDoubleBlock block, BlockState state)
    {
        ModelWrappingHandler handler = ModelWrappingManager.getHandler(state.getBlock());
        BlockState filtered = handler.getStateMerger().apply(state);
        if (filtered != state)
        {
            List<String> diffs = new ArrayList<>();
            for (Property<?> property : state.getProperties())
            {
                if (state.getValue(property) != filtered.getValue(property))
                {
                    diffs.add(property.getName());
                }
            }
            reporter.warn("Part state {} in block {} does not match filtered state of its model (differences: {})", state, block, String.join(", ", diffs));
        }
    }

    private DoubleBlockPartModelStateConsistency() { }
}
