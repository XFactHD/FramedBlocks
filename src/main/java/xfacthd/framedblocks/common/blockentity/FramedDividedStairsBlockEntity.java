package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDividedStairsBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDividedStairsBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DIVIDED_STAIRS.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        Direction side = hit.getDirection();
        if (side == facing.getClockWise())
        {
            return true;
        }
        if (side == facing.getCounterClockWise())
        {
            return false;
        }

        return Utils.fractionInDir(hit.getLocation(), facing.getClockWise()) > .5;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        return DoubleSoundMode.EITHER;
    }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing.getClockWise())
        {
            return getCamoTwo();
        }
        if (side == facing.getCounterClockWise())
        {
            return getCamo();
        }
        return EmptyCamoContainer.EMPTY;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        Direction secDir = top ? Direction.UP : Direction.DOWN;
        if (side == facing || side == secDir)
        {
            return getCamo().isSolid(level, worldPosition) && getCamoTwo().isSolid(level, worldPosition);
        }

        return false;
    }
}
