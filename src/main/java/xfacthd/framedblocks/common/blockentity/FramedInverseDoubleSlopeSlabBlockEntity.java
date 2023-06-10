package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.EmptyCamoContainer;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedInverseDoubleSlopeSlabBlockEntity extends FramedDoubleBlockEntity
{
    public FramedInverseDoubleSlopeSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_INVERSE_DOUBLE_SLOPE_SLAB.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        if (hit.getDirection() == Direction.DOWN)
        {
            return false;
        }
        return hit.getDirection() == Direction.UP || Mth.frac(hit.getLocation().y()) >= .5F;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        return DoubleSoundMode.SECOND;
    }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == Direction.UP || side == facing)
        {
            return getCamoTwo();
        }
        if (side == Direction.DOWN || side == facing.getOpposite())
        {
            return getCamo();
        }

        return EmptyCamoContainer.EMPTY;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        return false;
    }
}