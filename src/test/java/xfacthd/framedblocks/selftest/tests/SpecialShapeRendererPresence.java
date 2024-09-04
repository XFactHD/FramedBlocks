package xfacthd.framedblocks.selftest.tests;

import net.minecraft.world.level.block.Block;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.client.render.special.BlockOutlineRenderer;
import xfacthd.framedblocks.selftest.SelfTestReporter;

import java.util.List;

public final class SpecialShapeRendererPresence
{
    public static void checkSpecialShapePresent(SelfTestReporter reporter, List<Block> blocks)
    {
        reporter.startTest("shape renderer presence");

        blocks.forEach(block ->
        {
            IBlockType type = ((IFramedBlock) block).getBlockType();
            if (type.hasSpecialHitbox() && !BlockOutlineRenderer.hasOutlineRenderer(type))
            {
                reporter.warn("Block '{}' requests custom outline rendering but no OutlineRender was registered", block);
            }
            else if (!type.hasSpecialHitbox() && BlockOutlineRenderer.hasOutlineRenderer(type))
            {
                reporter.warn("Block '{}' requests standard outline rendering but an OutlineRender was registered", block);
            }
        });

        reporter.endTest();
    }



    private SpecialShapeRendererPresence() { }
}
