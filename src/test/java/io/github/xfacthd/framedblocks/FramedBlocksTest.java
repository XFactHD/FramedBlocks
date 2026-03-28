package io.github.xfacthd.framedblocks;

import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.cmdtests.SpecialTestCommand;
import io.github.xfacthd.framedblocks.cmdtests.tests.ChunkBanTest;
import io.github.xfacthd.framedblocks.selftest.SelfTest;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = FramedConstants.MOD_ID, dist = Dist.CLIENT)
public final class FramedBlocksTest {
    public FramedBlocksTest(IEventBus modBus) {
        modBus.addListener(SelfTest::runStartupSelfTest);

        NeoForge.EVENT_BUS.addListener(SpecialTestCommand::registerCommands);
        NeoForge.EVENT_BUS.addListener(ChunkBanTest::onLevelTick);
        NeoForge.EVENT_BUS.addListener(SelfTest::runInWorldSelfTest);
    }
}
