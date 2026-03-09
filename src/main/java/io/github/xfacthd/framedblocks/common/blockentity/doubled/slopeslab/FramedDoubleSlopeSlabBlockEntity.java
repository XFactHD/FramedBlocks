package io.github.xfacthd.framedblocks.common.blockentity.doubled.slopeslab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FramedDoubleSlopeSlabBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleSlopeSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DOUBLE_SLOPE_SLAB.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction side = hit.getDirection();

        if (side == facing.getOpposite() || side == Direction.UP)
        {
            return true;
        }
        if (side == facing || side == Direction.DOWN)
        {
            return false;
        }

        Vec3 vec = MathUtils.fraction(hit.getLocation());

        double hor = DirUtils.isX(facing) ? vec.x() : vec.z();
        if (!DirUtils.isPositive(facing))
        {
            hor = 1D - hor;
        }

        double y = vec.y();
        if (getBlockState().getValue(PropertyHolder.TOP_HALF))
        {
            y -= .5;
        }
        return (y * 2D) >= hor;
    }
}