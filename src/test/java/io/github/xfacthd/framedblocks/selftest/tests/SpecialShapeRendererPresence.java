package io.github.xfacthd.framedblocks.selftest.tests;

import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.client.render.special.BlockOutlineRenderer;
import io.github.xfacthd.framedblocks.selftest.SelfTestReporter;
import net.minecraft.world.level.block.Block;

import java.util.List;

public final class SpecialShapeRendererPresence {
    public static void checkSpecialShapePresent(SelfTestReporter reporter, List<Block> blocks) {
        reporter.startTest("shape renderer presence");

        blocks.forEach(block -> {
            IBlockType type = ((IFramedBlock) block).getBlockType();
            if (type.hasSpecialOutline() && !BlockOutlineRenderer.hasOutlineRenderer(type)) {
                reporter.warn("Block '{}' requests custom outline rendering but no OutlineRender was registered", block);
            } else if (!type.hasSpecialOutline() && BlockOutlineRenderer.hasOutlineRenderer(type)) {
                reporter.warn("Block '{}' requests standard outline rendering but an OutlineRender was registered", block);
            }
        });

        reporter.endTest();
    }

    private SpecialShapeRendererPresence() { }
}
