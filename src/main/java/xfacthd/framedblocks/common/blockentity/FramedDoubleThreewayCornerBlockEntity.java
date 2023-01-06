package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.data.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDoubleThreewayCornerBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleThreewayCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeDoubleFramedThreewayCorner.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction side = hit.getDirection();

        Vec3 vec = Utils.fraction(hit.getLocation());

        if (top)
        {
            if (side == facing || side == Direction.UP || side == facing.getCounterClockWise()) { return false; }

            if (side == facing.getClockWise())
            {
                double hor = Utils.isX(facing) ? vec.x() : vec.z();
                if (!Utils.isPositive(facing))
                {
                    hor = 1D - hor;
                }
                return vec.y() <= (1D - hor);
            }
            else if (side == facing.getOpposite())
            {
                Direction dir = facing.getCounterClockWise();
                double hor = Utils.isX(dir) ? vec.x() : vec.z();
                if (!Utils.isPositive(dir))
                {
                    hor = 1D - hor;
                }
                return vec.y() <= (1D - hor);
            }
            else if (side == Direction.DOWN)
            {
                boolean secondary = Utils.isX(facing) ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());
                if (Utils.isPositive(facing)) { secondary = !secondary; }
                return secondary;
            }
        }
        else
        {
            if (side == facing || side == Direction.DOWN || side == facing.getCounterClockWise()) { return false; }

            if (side == facing.getClockWise())
            {
                double hor = Utils.isX(facing) ? vec.x() : vec.z();
                if (!Utils.isPositive(facing))
                {
                    hor = 1D - hor;
                }
                return vec.y() >= hor;
            }
            else if (side == facing.getOpposite())
            {
                Direction dir = facing.getCounterClockWise();
                double hor = Utils.isX(dir) ? vec.x() : vec.z();
                if (!Utils.isPositive(dir))
                {
                    hor = 1D - hor;
                }
                return vec.y() >= hor;
            }
            else if (side == Direction.UP)
            {
                boolean secondary = Utils.isX(facing) ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());
                if (Utils.isPositive(facing)) { secondary = !secondary; }
                return secondary;
            }
        }
        return false;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        return getBlockState().getValue(FramedProperties.TOP) ? DoubleSoundMode.FIRST : DoubleSoundMode.EITHER;
    }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        if (top)
        {
            if (side == dir || side == Direction.UP || side == dir.getCounterClockWise()) { return getCamo(); }
            if (side == dir.getOpposite() || side == Direction.DOWN || side == dir.getClockWise()) { return getCamoTwo(); }
        }
        else
        {
            if (side == dir || side == Direction.DOWN || side == dir.getCounterClockWise()) { return getCamo(); }
            if (side == dir.getOpposite() || side == Direction.UP || side == dir.getClockWise()) { return getCamoTwo(); }
        }

        return EmptyCamoContainer.EMPTY;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        if (side == dir || side == dir.getCounterClockWise() || (side == Direction.DOWN && !top) || (side == Direction.UP && top))
        {
            //noinspection ConstantConditions
            return getCamo(side).getState().isSolidRender(level, worldPosition);
        }
        //noinspection ConstantConditions
        return getCamo().getState().isSolidRender(level, worldPosition) && getCamoTwo().getState().isSolidRender(level, worldPosition);
    }
}