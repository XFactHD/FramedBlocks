package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
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
    protected DoubleSoundMode calculateSoundMode()
    {
        return DoubleSoundMode.EITHER;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing.getClockWise())
        {
            return this::getCamoTwo;
        }
        if (side == facing.getCounterClockWise())
        {
            return this::getCamo;
        }

        Direction dirTwo = getBlockState().getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;
        if (side == facing || side == dirTwo)
        {
            if (edge == facing.getClockWise())
            {
                return this::getCamoTwo;
            }
            if (edge == facing.getCounterClockWise())
            {
                return this::getCamo;
            }
        }
        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        Direction secDir = top ? Direction.UP : Direction.DOWN;
        if (side == facing || side == secDir)
        {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }
}
