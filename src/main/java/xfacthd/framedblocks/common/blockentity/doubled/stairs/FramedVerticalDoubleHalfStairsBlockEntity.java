package xfacthd.framedblocks.common.blockentity.doubled.stairs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;

public class FramedVerticalDoubleHalfStairsBlockEntity extends FramedDoubleBlockEntity
{
    public FramedVerticalDoubleHalfStairsBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_VERTICAL_DOUBLE_HALF_STAIRS.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction face = hit.getDirection();
        Vec3 hitVec = hit.getLocation();
        if (face == dir || face == dir.getCounterClockWise())
        {
            return false;
        }
        else if (face == dir.getOpposite())
        {
            return Utils.fractionInDir(hitVec, dir.getClockWise()) > .5;
        }
        else if (face == dir.getClockWise())
        {
            return Utils.fractionInDir(hitVec, dir.getOpposite()) > .5;
        }
        else
        {
            boolean overHalfPar = Utils.fractionInDir(hitVec, dir.getOpposite()) > .5;
            boolean overHalfPerp = Utils.fractionInDir(hitVec, dir.getClockWise()) > .5;
            return overHalfPar && overHalfPerp;
        }
    }
}
