package io.github.xfacthd.framedblocks.selftest.tests;

import io.github.xfacthd.framedblocks.api.block.render.FramedClientBlockExtensions;
import io.github.xfacthd.framedblocks.selftest.SelfTestReporter;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;

import java.util.List;

public final class ClientBlockExtensionsPresence {
    public static void checkClientExtensionsPresent(SelfTestReporter reporter, List<Block> blocks) {
        reporter.startTest("client block extension presence");

        blocks.forEach(block -> {
            if (!(IClientBlockExtensions.of(block) instanceof FramedClientBlockExtensions)) {
                reporter.warn("Block '{}' does not provide FramedBlockClientExtensions", block);
            }
        });

        reporter.endTest();
    }

    private ClientBlockExtensionsPresence() { }
}
