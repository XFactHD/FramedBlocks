package xfacthd.framedblocks.selftest.tests;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.IFramedBlock;

import java.util.List;

public final class JadeRenderStateErrors
{
    public static void checkJadeRenderStateErrors(List<Block> blocks)
    {
        FramedBlocks.LOGGER.info("  Checking Jade render state correctness");

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
                        FramedBlocks.LOGGER.error(
                                "    IFramedBlock#getJadeRenderState throws exception on state '{}': {}",
                                state, t.getMessage()
                        );
                    }
                });
    }



    private JadeRenderStateErrors() { }
}
