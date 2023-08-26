package xfacthd.framedblocks.selftest.tests;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.render.FramedBlockRenderProperties;

import java.util.List;

public final class ClientBlockExtensionsPresence
{
    public static void checkClientExtensionsPresent(List<Block> blocks)
    {
        FramedBlocks.LOGGER.info("  Checking presence of client block extensions");

        blocks.forEach(block ->
        {
            if (!(IClientBlockExtensions.of(block) instanceof FramedBlockRenderProperties))
            {
                FramedBlocks.LOGGER.warn("    Block '{}' does not provide FramedBlockRenderProperties", block);
            }
        });
    }



    private ClientBlockExtensionsPresence() { }
}
