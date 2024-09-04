package xfacthd.framedblocks.selftest;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.selftest.tests.*;

import java.util.List;

public final class SelfTest
{
    public static void runSelfTest(@SuppressWarnings("unused") final FMLLoadCompleteEvent event)
    {
        SelfTestReporter reporter = new SelfTestReporter();

        List<Block> blocks = FBContent.getRegisteredBlocks()
                .stream()
                .map(Holder::value)
                .filter(IFramedBlock.class::isInstance)
                .toList();

        OcclusionPropertyConsistency.checkOcclusionProperty(reporter, blocks);
        WaterloggingPropertyConsistency.checkWaterloggingProperty(reporter, blocks);
        LockingPropertyConsistency.checkLockingProperty(reporter, blocks);
        ClientBlockExtensionsPresence.checkClientExtensionsPresent(reporter, blocks);
        SpecialShapeRendererPresence.checkSpecialShapePresent(reporter, blocks);
        SkipPredicatePresenceConsistency.checkSkipPredicateConsistency(reporter);
        StateCacheValidity.checkStateCacheValid(reporter, blocks);
        DoubleBlockCamoConnectionConsistency.checkConnectionConsistency(reporter, blocks);
        DoubleBlockSolidSideConsistency.checkSolidSideConsistency(reporter, blocks);
        RotateMirrorErrors.checkRotateMirrorErrors(reporter, blocks);
        JadeRenderStateErrors.checkJadeRenderStateErrors(reporter, blocks);
        BlockEntityPresence.checkBlockEntityTypePresent(reporter, blocks);

        reporter.finish();
    }



    private SelfTest() { }
}
