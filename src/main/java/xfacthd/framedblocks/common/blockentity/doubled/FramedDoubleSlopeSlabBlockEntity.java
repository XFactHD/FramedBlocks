package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDoubleSlopeSlabBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleSlopeSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DOUBLE_SLOPE_SLAB.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction side = hit.getDirection();

        if (side == facing.getOpposite() || side == Direction.UP)
        {
            return true;
        }
        if (side == facing || side == Direction.DOWN)
        {
            return false;
        }

        Vec3 vec = Utils.fraction(hit.getLocation());

        double hor = Utils.isX(facing) ? vec.x() : vec.z();
        if (!Utils.isPositive(facing))
        {
            hor = 1D - hor;
        }

        double y = vec.y();
        if (getBlockState().getValue(PropertyHolder.TOP_HALF))
        {
            y -= .5;
        }
        return (y * 2D) >= hor;
    }

    @Override
    protected DoubleSoundMode calculateSoundMode()
    {
        return DoubleSoundMode.SECOND;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(PropertyHolder.TOP_HALF);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if ((side == Direction.UP && top) || (side == facing.getOpposite() && edge == dirTwo))
        {
            return this::getCamoTwo;
        }
        else if ((side == Direction.DOWN && !top) || (side == facing && edge == dirTwo))
        {
            return this::getCamo;
        }
        else if (side.getAxis() == facing.getClockWise().getAxis() && edge == dirTwo)
        {
            return top ? this::getCamoTwo : this::getCamo;
        }

        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        boolean topHalf = getBlockState().getValue(PropertyHolder.TOP_HALF);
        if (topHalf && side == Direction.UP)
        {
            return SolidityCheck.SECOND;
        }
        else if (!topHalf && side == Direction.DOWN)
        {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.NONE;
    }
}