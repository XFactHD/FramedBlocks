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
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedDoubleHalfSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleHalfSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DOUBLE_HALF_SLOPE.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        Direction side = hit.getDirection();
        Vec3 vec = Utils.fraction(hit.getLocation());

        double hor = Utils.isX(facing) ? vec.x() : vec.z();
        if (!Utils.isPositive(facing))
        {
            hor = 1D - hor;
        }

        if (side == facing || side == Direction.DOWN)
        {
            return false;
        }
        if (side == facing.getOpposite() || side == Direction.UP)
        {
            return true;
        }
        return vec.y() >= hor;
    }

    @Override
    protected DoubleBlockTopInteractionMode calculateTopInteractionMode()
    {
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean right = getBlockState().getValue(PropertyHolder.RIGHT);
        Direction dirTwo = right ? facing.getClockWise() : facing.getCounterClockWise();
        if (edge == dirTwo)
        {
            if (side == facing || side == Direction.DOWN)
            {
                return this::getCamo;
            }
            if (side == facing.getOpposite() || side == Direction.UP)
            {
                return this::getCamoTwo;
            }
        }
        else if (side == dirTwo)
        {
            if (edge == facing || edge == Direction.DOWN)
            {
                return this::getCamo;
            }
            if (edge == facing.getOpposite() || edge == Direction.UP)
            {
                return this::getCamoTwo;
            }
        }
        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean right = getBlockState().getValue(PropertyHolder.RIGHT);
        if ((!right && side == facing.getCounterClockWise()) || (right && side == facing.getClockWise()))
        {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }
}
