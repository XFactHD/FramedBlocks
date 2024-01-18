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
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedVerticalSlicedStairsBlockEntity extends FramedDoubleBlockEntity
{
    public FramedVerticalSlicedStairsBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_VERTICAL_SLICED_STAIRS.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean right = getBlockState().getValue(PropertyHolder.RIGHT);
        Direction dirTwo = right ? dir.getCounterClockWise() : dir.getClockWise();

        Direction face = hit.getDirection();
        Vec3 hitVec = hit.getLocation();
        if (face.getAxis() == dir.getAxis() || Utils.isY(face))
        {
            return Utils.fractionInDir(hitVec, dirTwo) > .5;
        }
        else if (face == dirTwo)
        {
            return Utils.fractionInDir(hitVec, dir) > .5;
        }
        return false;
    }
}
