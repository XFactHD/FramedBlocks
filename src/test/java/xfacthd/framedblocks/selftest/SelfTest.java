package xfacthd.framedblocks.selftest;

import com.google.common.base.Stopwatch;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.selftest.tests.*;

import java.util.List;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class SelfTest
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void runSelfTest(final FMLLoadCompleteEvent event)
    {
        FramedBlocks.LOGGER.info("=======================================");
        FramedBlocks.LOGGER.info("Running self-test");
        Stopwatch stopwatch = Stopwatch.createStarted();

        List<Block> blocks = FBContent.getRegisteredBlocks()
                .stream()
                .map(Holder::value)
                .filter(IFramedBlock.class::isInstance)
                .toList();

        OcclusionPropertyConsistency.checkOcclusionProperty(blocks);
        WaterloggingPropertyConsistency.checkWaterloggingProperty(blocks);
        LockingPropertyConsistency.checkLockingProperty(blocks);
        ClientBlockExtensionsPresence.checkClientExtensionsPresent(blocks);
        SpecialShapeRendererPresence.checkSpecialShapePresent(blocks);
        SkipPredicatePresenceConsistency.checkSkipPredicateConsistency();
        StateCacheValidity.checkStateCacheValid(blocks);
        DoubleBlockCamoConnectionConsistency.checkConnectionConsistency(blocks);
        DoubleBlockSolidSideConsistency.checkSolidSideConsistency(blocks);
        RotateMirrorErrors.checkRotateMirrorErrors(blocks);

        stopwatch.stop();
        FramedBlocks.LOGGER.info("Self test completed in {}", stopwatch);
        FramedBlocks.LOGGER.info("=======================================");
    }



    private SelfTest() { }
}
