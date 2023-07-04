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
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedVerticalDoubleHalfSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedVerticalDoubleHalfSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_VERTICAL_DOUBLE_HALF_SLOPE.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        Direction side = hit.getDirection();

        if (side == facing || side == facing.getCounterClockWise())
        {
            return false;
        }
        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return true;
        }

        Vec3 vec = Utils.fraction(hit.getLocation());
        boolean secondary = Utils.isX(facing) ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());

        if (Utils.isPositive(facing))
        {
            secondary = !secondary;
        }
        return secondary;
    }

    @Override
    protected DoubleBlockTopInteractionMode calculateTopInteractionMode()
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        if ((!top && edge == Direction.DOWN) || (top && edge == Direction.UP))
        {
            Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
            if (side == facing || side == facing.getCounterClockWise())
            {
                return this::getCamo;
            }
            else if (side == facing.getOpposite() || side == facing.getClockWise())
            {
                return this::getCamoTwo;
            }
        }
        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        if ((!top && side == Direction.DOWN) || (top && side == Direction.UP))
        {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }
}
