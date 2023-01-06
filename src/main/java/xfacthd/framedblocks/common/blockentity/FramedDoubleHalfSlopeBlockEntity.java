package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.data.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDoubleHalfSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleHalfSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedDoubleHalfSlope.get(), pos, state);
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
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.SECOND; }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == facing || side == Direction.DOWN)
        {
            return getCamo();
        }
        if (side == facing.getOpposite() || side == Direction.UP)
        {
            return getCamoTwo();
        }
        return EmptyCamoContainer.EMPTY;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean right = getBlockState().getValue(PropertyHolder.RIGHT);
        if ((!right && side == facing.getCounterClockWise()) || (right && side == facing.getClockWise()))
        {
            return getCamo().isSolid(level, worldPosition) && getCamoTwo().isSolid(level, worldPosition);
        }
        return false;
    }
}
