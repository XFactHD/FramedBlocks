package io.github.xfacthd.framedblocks.common.blockentity.doubled.slopeslab;

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

public class FramedElevatedDoubleSlopeSlabBlockEntity extends FramedDoubleBlockEntity
{
    public FramedElevatedDoubleSlopeSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction side = hit.getDirection();

        if (side == Direction.UP)
        {
            return !top;
        }
        if (side == Direction.DOWN || side == facing)
        {
            return top;
        }

        Vec3 vec = MathUtils.fraction(hit.getLocation());
        if (side == facing.getOpposite())
        {
            return (vec.y() >= .5D) != top;
        }

        double hor = DirUtils.isX(facing) ? vec.x() : vec.z();
        if (!DirUtils.isPositive(facing))
        {
            hor = 1D - hor;
        }

        double y = vec.y();
        if (top)
        {
            y = 1D - y;
        }
        y -= .5D;
        return (y * 2D) >= hor;
    }
}
