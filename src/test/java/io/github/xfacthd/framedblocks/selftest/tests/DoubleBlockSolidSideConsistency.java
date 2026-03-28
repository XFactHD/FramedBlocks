package io.github.xfacthd.framedblocks.selftest.tests;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.cache.DoubleBlockStateCache;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.selftest.SelfTestReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public final class DoubleBlockSolidSideConsistency {
    public static void checkSolidSideConsistency(SelfTestReporter reporter, List<Block> blocks) {
        reporter.startTest("solid side consistency");

        blocks.stream()
                .filter(IFramedDoubleBlock.class::isInstance)
                .map(IFramedDoubleBlock.class::cast)
                .forEach(block -> ((Block) block).getStateDefinition().getPossibleStates().forEach(state -> {
                    if (!state.hasProperty(FramedProperties.SOLID) || !state.getValue(FramedProperties.SOLID)) {
                        return;
                    }

                    DoubleBlockStateCache cache = block.getCache(state);
                    DirUtils.forAllDirections(side -> {
                        VoxelShape faceShape = state.getFaceOcclusionShape(side);
                        boolean solidShape = !Shapes.joinIsNotEmpty(faceShape, Shapes.block(), BooleanOp.ONLY_SECOND);
                        boolean solidCache = cache.getSolidityCheck(side) != SolidityCheck.NONE;

                        if (solidShape != solidCache) {
                            reporter.warn(
                                    "Block '{}' has inconsistent side solidity for state {} on side {} (shape: {}, cache: {})",
                                    block, state, side, solidShape, solidCache
                            );
                        }
                    });
                }));

        reporter.endTest();
    }

    private DoubleBlockSolidSideConsistency() { }
}
