package io.github.xfacthd.framedblocks.selftest.tests;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.compat.jade.JadeDisplayConfig;
import io.github.xfacthd.framedblocks.selftest.SelfTestReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.List;

public final class JadeRenderStateErrors {
    public static void checkJadeDisplayConfigErrors(SelfTestReporter reporter, List<Block> blocks) {
        reporter.startTest("Jade display config correctness");

        blocks.stream()
                .map(Block::getStateDefinition)
                .map(StateDefinition::getPossibleStates)
                .flatMap(List::stream)
                .forEach(state -> {
                    JadeDisplayConfig displayConfig;
                    try {
                        displayConfig = ((IFramedBlock) state.getBlock()).getJadeDisplayConfig();
                    } catch (Throwable t) {
                        reporter.error(
                                "IFramedBlock#getJadeDisplayConfig() throws exception on state '{}': {}",
                                state, t.getMessage()
                        );
                        return;
                    }
                    try {
                        displayConfig.getDisplayState(state);
                    } catch (Throwable t) {
                        reporter.error(
                                "JadeDisplayConfig#renderState(BlockState) throws exception on state '{}': {}",
                                state, t.getMessage()
                        );
                    }
                });

        reporter.endTest();
    }

    private JadeRenderStateErrors() { }
}
