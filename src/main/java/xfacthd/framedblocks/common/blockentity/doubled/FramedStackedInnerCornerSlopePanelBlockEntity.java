package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedStackedInnerCornerSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    public FramedStackedInnerCornerSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction side = hit.getDirection();

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return false;
        }

        Vec3 hitVec = hit.getLocation();
        if (Utils.isY(side))
        {
            double xz1 = Utils.fractionInDir(hitVec, facing);
            double xz2 = Utils.fractionInDir(hitVec, facing.getCounterClockWise());
            return xz1 > .5 && xz2 > .5;
        }

        double xz = Utils.fractionInDir(hitVec, side == facing ? facing.getCounterClockWise() : facing);
        return xz > .5;
    }
}
