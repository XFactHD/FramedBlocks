package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedVerticalDoubleStairsBlockEntity extends FramedDoubleBlockEntity
{
    public FramedVerticalDoubleStairsBlockEntity(BlockPos worldPosition, BlockState blockState)
    {
        super(FBContent.blockEntityTypeFramedVerticalDoubleStairs.get(), worldPosition, blockState);
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

        Vec3 vec = Utils.fraction(hit.getLocation());

        if (side == facing.getOpposite())
        {
            double xz = Utils.isX(facing) ? vec.z : vec.x;
            boolean positive = Utils.isPositive(facing.getCounterClockWise());
            return xz > .5 != positive;
        }
        else if (side == facing.getClockWise())
        {
            double xz = Utils.isX(facing) ? vec.x : vec.z;
            boolean positive = Utils.isPositive(facing);
            return xz > .5 != positive;
        }
        else
        {
            double xz = Utils.isX(facing) ? vec.x : vec.z;
            double xzCCW = Utils.isX(facing) ? vec.z : vec.x;

            boolean positive = Utils.isPositive(facing);
            boolean positiveCCW = Utils.isPositive(facing.getCounterClockWise());

            return (xzCCW > .5 != positiveCCW) && (xz > .5 != positive);
        }
    }

    @Override
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.EITHER; }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == facing || side == facing.getCounterClockWise())
        {
            return getCamoState();
        }
        else if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return getCamoStateTwo();
        }

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == facing || side == facing.getCounterClockWise())
        {
            //noinspection ConstantConditions
            return getCamoState().isSolidRender(level, worldPosition);
        }

        return false;
    }
}
