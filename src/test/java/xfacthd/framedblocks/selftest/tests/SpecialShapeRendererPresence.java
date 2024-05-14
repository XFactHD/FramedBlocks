package xfacthd.framedblocks.selftest.tests;

import net.minecraft.world.level.block.Block;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.client.render.special.BlockOutlineRenderer;

import java.util.List;

public final class SpecialShapeRendererPresence
{
    public static void checkSpecialShapePresent(List<Block> blocks)
    {
        FramedBlocks.LOGGER.info("  Checking presence of shape renderers");

        blocks.forEach(block ->
        {
            IBlockType type = ((IFramedBlock) block).getBlockType();
            if (type.hasSpecialHitbox() && !BlockOutlineRenderer.hasOutlineRenderer(type))
            {
                FramedBlocks.LOGGER.warn("    Block '{}' requests custom outline rendering but no OutlineRender was registered", block);
            }
            else if (!type.hasSpecialHitbox() && BlockOutlineRenderer.hasOutlineRenderer(type))
            {
                FramedBlocks.LOGGER.warn("    Block '{}' requests standard outline rendering but an OutlineRender was registered", block);
            }
        });
    }



    private SpecialShapeRendererPresence() { }
}
