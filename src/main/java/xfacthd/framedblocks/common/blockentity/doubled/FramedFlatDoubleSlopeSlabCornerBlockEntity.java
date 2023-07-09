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

public class FramedFlatDoubleSlopeSlabCornerBlockEntity extends FramedDoubleBlockEntity
{
    public FramedFlatDoubleSlopeSlabCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction side = hit.getDirection();

        if (side == Direction.UP)
        {
            return !top;
        }
        if (side == Direction.DOWN)
        {
            return top;
        }
        if (side == facing || side == facing.getCounterClockWise())
        {
            return false;
        }

        Vec3 vec = Utils.fraction(hit.getLocation());

        Direction perpDir = side == facing.getClockWise() ? facing : facing.getCounterClockWise();
        double hor = Utils.isX(perpDir) ? vec.x() : vec.z();
        if (!Utils.isPositive(perpDir))
        {
            hor = 1D - hor;
        }

        double y = vec.y();
        if (getBlockState().getValue(PropertyHolder.TOP_HALF))
        {
            y -= .5;
        }
        if (top)
        {
            y = .5 - y;
        }
        return (y * 2D) >= hor;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        boolean topHalf = getBlockState().getValue(PropertyHolder.TOP_HALF);

        if (side == Direction.UP && topHalf)
        {
            return top ? this::getCamo : this::getCamoTwo;
        }
        else if (side == Direction.DOWN && !topHalf)
        {
            return top ? this::getCamoTwo : this::getCamo;
        }

        if (side == facing || side == facing.getCounterClockWise())
        {
            if ((!topHalf && edge == Direction.DOWN) || (topHalf && edge == Direction.UP))
            {
                return this::getCamo;
            }
        }
        else if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            if ((!topHalf && edge == Direction.DOWN) || (topHalf && edge == Direction.UP))
            {
                return top == topHalf ? this::getCamo : this::getCamoTwo;
            }
        }

        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        boolean topHalf = getBlockState().getValue(PropertyHolder.TOP_HALF);
        if (topHalf && side == Direction.UP)
        {
            return top ? SolidityCheck.FIRST : SolidityCheck.SECOND;
        }
        else if (!topHalf && side == Direction.DOWN)
        {
            return top ? SolidityCheck.SECOND : SolidityCheck.FIRST;
        }
        return SolidityCheck.NONE;
    }
}
