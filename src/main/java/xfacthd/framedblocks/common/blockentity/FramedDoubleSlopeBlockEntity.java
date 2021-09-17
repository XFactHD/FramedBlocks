package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.SlopeType;
import xfacthd.framedblocks.api.util.Utils;

public class FramedDoubleSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeDoubleFramedSlope.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);
        Direction side = hit.getDirection();

        Vec3 vec = Utils.fraction(hit.getLocation());

        if (type == SlopeType.HORIZONTAL)
        {
            if (side == facing || side == facing.getCounterClockWise()) { return false; }
            if (side == facing.getOpposite() || side == facing.getClockWise()) { return true; }

            boolean secondary = facing.getAxis() == Direction.Axis.X ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());

            if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) { secondary = !secondary; }
            return secondary;
        }
        else
        {
            double hor = facing.getAxis() == Direction.Axis.X ? vec.x() : vec.z();
            if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
            {
                hor = 1D - hor;
            }

            if (type == SlopeType.TOP)
            {
                if (side == facing || side == Direction.UP) { return false; }
                if (side == facing.getOpposite() || side == Direction.DOWN) { return true; }
                return vec.y() <= (1D - hor);
            }
            else if (type == SlopeType.BOTTOM)
            {
                if (side == facing || side == Direction.DOWN) { return false; }
                if (side == facing.getOpposite() || side == Direction.UP) { return true; }
                return vec.y() >= hor;
            }
        }

        return false;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);

        if (type == SlopeType.HORIZONTAL)
        {
            if (side == facing || side == facing.getCounterClockWise()) { return getCamoState(); }
            if (side == facing.getOpposite() || side == facing.getClockWise()) { return getCamoStateTwo(); }
        }
        else if (type == SlopeType.TOP)
        {
            if (side == facing || side == Direction.UP) { return getCamoState(); }
            if (side == facing.getOpposite() || side == Direction.DOWN) { return getCamoStateTwo(); }
        }
        else if (type == SlopeType.BOTTOM)
        {
            if (side == facing || side == Direction.DOWN) { return getCamoState(); }
            if (side == facing.getOpposite() || side == Direction.UP) { return getCamoStateTwo(); }
        }

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        BlockState state = getCamoState(side);
        if (!state.isAir())
        {
            //noinspection ConstantConditions
            return state.isSolidRender(level, worldPosition);
        }
        //noinspection ConstantConditions
        return getCamoState().isSolidRender(level, worldPosition) && getCamoStateTwo().isSolidRender(level, worldPosition);
    }
}