package io.github.xfacthd.framedblocks.common.blockentity.doubled.slope;

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

public class FramedDoubleHalfSlopeBlockEntity extends FramedDoubleBlockEntity {
    public FramedDoubleHalfSlopeBlockEntity(BlockPos pos, BlockState state) {
        super(FBContent.BE_TYPE_FRAMED_DOUBLE_HALF_SLOPE.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos) {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        Direction side = hit.getDirection();
        Vec3 vec = MathUtils.fraction(hit.getLocation());

        double hor = DirUtils.isX(facing) ? vec.x() : vec.z();
        if (!DirUtils.isPositive(facing)) {
            hor = 1D - hor;
        }

        if (side == facing || side == Direction.DOWN) {
            return false;
        }
        if (side == facing.getOpposite() || side == Direction.UP) {
            return true;
        }
        return vec.y() >= hor;
    }
}
