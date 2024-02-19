package xfacthd.framedblocks.selftest;

import com.google.common.base.Stopwatch;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.RegistryObject;
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
                .map(RegistryObject::get)
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

        stopwatch.stop();
        FramedBlocks.LOGGER.info("Self test completed in {}", stopwatch);
        FramedBlocks.LOGGER.info("=======================================");
    }



    private SelfTest() { }
}
