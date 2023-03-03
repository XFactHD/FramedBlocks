package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedStackedSlopeSlabBlockEntity extends FramedDoubleBlockEntity
{
    private final boolean corner;
    private final boolean innerCorner;

    public FramedStackedSlopeSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedStackedSlopeSlab.get(), pos, state);
        BlockType type = (BlockType) getBlockType();
        this.corner = type != BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER;
        this.innerCorner = type == BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER;
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        if (hit.getDirection() == Direction.DOWN)
        {
            return top;
        }

        boolean upper = hit.getDirection() == Direction.UP || Mth.frac(hit.getLocation().y()) >= .5F;
        return upper != top;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        return top ? DoubleSoundMode.FIRST : DoubleSoundMode.SECOND;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        if (side == Direction.UP)
        {
            return top ? getCamoState() : getCamoStateTwo();
        }
        if (side == Direction.DOWN)
        {
            return top ? getCamoStateTwo() : getCamoState();
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        if ((!top && side == Direction.DOWN) || (top && side == Direction.UP))
        {
            //noinspection ConstantConditions
            return getCamoState(side).isSolidRender(level, worldPosition);
        }

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if ((side == facing && !corner) || (side == facing.getCounterClockWise() && innerCorner))
        {
            //noinspection ConstantConditions
            return getCamoState().isSolidRender(level, worldPosition) && getCamoStateTwo().isSolidRender(level, worldPosition);
        }

        return false;
    }
}