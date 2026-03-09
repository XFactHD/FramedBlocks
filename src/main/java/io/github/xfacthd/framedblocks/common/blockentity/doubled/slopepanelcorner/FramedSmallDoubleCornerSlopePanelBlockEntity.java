package io.github.xfacthd.framedblocks.common.blockentity.doubled.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FramedSmallDoubleCornerSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    public FramedSmallDoubleCornerSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
    {
        Direction side = hit.getDirection();
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        if (DirUtils.isY(side))
        {
            boolean up = side == Direction.UP;
            return top != up;
        }

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return false;
        }

        Vec3 hitVec = hit.getLocation();
        double xz = MathUtils.fractionInDir(hitVec, side == facing ? facing.getCounterClockWise() : facing) - .5;
        double y = MathUtils.fractionInDir(hitVec, top ? Direction.UP : Direction.DOWN);
        return (xz * 2D) > y;
    }
}
