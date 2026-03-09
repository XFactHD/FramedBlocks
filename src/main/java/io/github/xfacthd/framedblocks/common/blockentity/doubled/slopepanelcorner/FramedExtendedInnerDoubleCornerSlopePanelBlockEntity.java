package io.github.xfacthd.framedblocks.common.blockentity.doubled.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FramedExtendedInnerDoubleCornerSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    public FramedExtendedInnerDoubleCornerSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
    {
        Direction side = hit.getDirection();
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        if ((!top && side == Direction.DOWN) || (top && side == Direction.UP))
        {
            return false;
        }

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return false;
        }

        Vec3 hitVec = hit.getLocation();
        if ((!top && side == Direction.UP) || (top && side == Direction.DOWN))
        {
            double xz1 = MathUtils.fractionInDir(hitVec, facing);
            double xz2 = MathUtils.fractionInDir(hitVec, facing.getCounterClockWise());
            return xz1 > .5 && xz2 > .5;
        }

        double xz = MathUtils.fractionInDir(hitVec, side == facing ? facing.getCounterClockWise() : facing);
        if (xz < .5)
        {
            return false;
        }

        double y = MathUtils.fractionInDir(hitVec, top ? Direction.UP : Direction.DOWN);
        return ((xz - .5) * 2D) > y;
    }
}
