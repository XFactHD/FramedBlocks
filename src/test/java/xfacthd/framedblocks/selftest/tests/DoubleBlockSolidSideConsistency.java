package xfacthd.framedblocks.selftest.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.IFramedDoubleBlock;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockStateCache;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;

import java.util.List;

public final class DoubleBlockSolidSideConsistency
{
    public static void checkSolidSideConsistency(List<Block> blocks)
    {
        blocks.stream()
                .filter(IFramedDoubleBlock.class::isInstance)
                .map(IFramedDoubleBlock.class::cast)
                .forEach(block -> ((Block) block).getStateDefinition().getPossibleStates().forEach(state ->
                {
                    if (!state.hasProperty(FramedProperties.SOLID) || !state.getValue(FramedProperties.SOLID)) return;

                    DoubleBlockStateCache cache = block.getCache(state);
                    Utils.forAllDirections(false, side ->
                    {
                        VoxelShape faceShape = state.getFaceOcclusionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, side);
                        boolean solidShape = !Shapes.joinIsNotEmpty(faceShape, Shapes.block(), BooleanOp.ONLY_SECOND);
                        boolean solidCache = cache.getSolidityCheck(side) != SolidityCheck.NONE;

                        if (solidShape != solidCache)
                        {
                            FramedBlocks.LOGGER.warn(
                                    "    Block '{}' has inconsistent side solidity for state {} on side {} (shape: {}, cache: {})",
                                    block, state, side, solidShape, solidCache
                            );
                        }
                    });
                }));
    }



    private DoubleBlockSolidSideConsistency() { }
}
