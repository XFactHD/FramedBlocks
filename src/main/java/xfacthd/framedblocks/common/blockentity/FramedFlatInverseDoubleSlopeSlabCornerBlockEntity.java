package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedFlatInverseDoubleSlopeSlabCornerBlockEntity extends FramedDoubleBlockEntity
{
    public FramedFlatInverseDoubleSlopeSlabCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_SLAB_CORNER.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        if (hit.getDirection() == Direction.DOWN)
        {
            return top;
        }
        else if (hit.getDirection() == Direction.UP)
        {
            return !top;
        }
        return (Mth.frac(hit.getLocation().y()) >= .5F) != top;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        return top ? DoubleSoundMode.FIRST : DoubleSoundMode.SECOND;
    }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == facing || side == facing.getCounterClockWise())
        {
            return getCamoTwo();
        }
        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return getCamo();
        }

        boolean top = getBlockState().getValue(FramedProperties.TOP);
        return top == (side == Direction.UP) ? getCamo() : getCamoTwo();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        return false;
    }
}
