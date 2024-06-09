package xfacthd.framedblocks;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.cmdtests.SpecialTestCommand;
import xfacthd.framedblocks.cmdtests.tests.ChunkBanTest;
import xfacthd.framedblocks.selftest.SelfTest;

@Mod(value = FramedConstants.MOD_ID, dist = Dist.CLIENT)
public final class FramedBlocksTest
{
    public FramedBlocksTest(IEventBus modBus)
    {
        modBus.addListener(SelfTest::runSelfTest);

        NeoForge.EVENT_BUS.addListener(SpecialTestCommand::registerCommands);
        NeoForge.EVENT_BUS.addListener(ChunkBanTest::onLevelTick);
    }
}
