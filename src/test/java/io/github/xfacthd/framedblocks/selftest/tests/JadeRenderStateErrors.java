package io.github.xfacthd.framedblocks.selftest.tests;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.selftest.SelfTestReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.List;

public final class JadeRenderStateErrors
{
    public static void checkJadeRenderStateErrors(SelfTestReporter reporter, List<Block> blocks)
    {
        reporter.startTest("Jade render state correctness");

        blocks.stream()
                .map(Block::getStateDefinition)
                .map(StateDefinition::getPossibleStates)
                .flatMap(List::stream)
                .forEach(state ->
                {
                    try
                    {
                        ((IFramedBlock) state.getBlock()).getJadeRenderState(state);
                    }
                    catch (Throwable t)
                    {
                        reporter.error(
                                "IFramedBlock#getJadeRenderState throws exception on state '{}': {}",
                                state, t.getMessage()
                        );
                    }
                });

        reporter.endTest();
    }

    private JadeRenderStateErrors() { }
}
