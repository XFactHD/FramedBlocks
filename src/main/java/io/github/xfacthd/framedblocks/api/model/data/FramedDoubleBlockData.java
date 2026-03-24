package io.github.xfacthd.framedblocks.api.model.data;

import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.TriState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class FramedDoubleBlockData extends AbstractFramedBlockData
{
    private final BlockState partStateOne;
    private final BlockState partStateTwo;
    private final FramedBlockData dataOne;
    private final FramedBlockData dataTwo;
    private final int postCamoTintIndexOffset;

    public FramedDoubleBlockData(DoubleBlockParts parts, FramedBlockData dataOne, FramedBlockData dataTwo)
    {
        this.partStateOne = parts.stateOne();
        this.partStateTwo = parts.stateTwo();
        this.dataOne = dataOne;
        this.dataTwo = dataTwo;
        this.postCamoTintIndexOffset = dataOne.getPostCamoTintIndexOffset() + dataTwo.getPostCamoTintIndexOffset();
    }

    @Override
    public FramedBlockData unwrap(BlockState partState)
    {
        if (partState == partStateOne) return dataOne;
        if (partState == partStateTwo) return dataTwo;
        if (!Utils.PRODUCTION)
        {
            throw new IllegalArgumentException("Received invalid part state " + partState + ", expected " + partStateOne + " or " + partStateTwo);
        }
        return FramedBlockData.EMPTY;
    }

    @Override
    public FramedBlockData unwrap(boolean secondary)
    {
        return secondary ? dataTwo : dataOne;
    }

    @Override
    public boolean isCamoEmissive()
    {
        return dataOne.isCamoEmissive() || dataTwo.isCamoEmissive();
    }

    @Override
    public float getCamoShadeBrightness(BlockGetter level, BlockPos pos, float frameShade)
    {
        return Math.max(
                dataOne.getCamoShadeBrightness(level, pos, frameShade),
                dataTwo.getCamoShadeBrightness(level, pos, frameShade)
        );
    }

    @Override
    public TriState isViewBlocking()
    {
        return dataOne.isViewBlocking();
    }

    @Override
    @Nullable
    public Holder<BlockOverlay> getBlockOverlay()
    {
        return dataOne.getBlockOverlay();
    }

    @Override
    public int getCamoTintIndexOffset(boolean secondPart)
    {
        return secondPart ? dataOne.getPostCamoTintIndexOffset() : 0;
    }

    @Override
    public int getPostCamoTintIndexOffset()
    {
        return postCamoTintIndexOffset;
    }
}
