package io.github.xfacthd.framedblocks.common.blockentity.doubled.prism;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FramedElevatedDoublePrismBlockEntity extends FramedDoubleBlockEntity {
    public FramedElevatedDoublePrismBlockEntity(BlockPos pos, BlockState state) {
        this(FBContent.BE_TYPE_FRAMED_ELEVATED_DOUBLE_PRISM.value(), pos, state);
    }

    protected FramedElevatedDoublePrismBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos) {
        Direction side = hit.getDirection();

        Direction facing = getFacing(getBlockState());
        if (side == facing) {
            return true;
        }
        if (side == facing.getOpposite()) {
            return false;
        }
        if (!isDoubleSide(side) && side.getAxis() != facing.getAxis()) {
            return false;
        }

        if (isDoubleSide(side)) {
            Direction horDir = side.getClockWise(facing.getAxis());
            double hor = MathUtils.fractionInDir(hit.getLocation(), horDir);
            hor = Math.abs(hor - .5);

            double vert = MathUtils.fractionInDir(hit.getLocation(), facing) - .5;

            return vert > hor;
        }

        return false;
    }

    protected boolean isDoubleSide(Direction side) {
        return side.getAxis() == getBlockState().getValue(PropertyHolder.FACING_AXIS).axis();
    }

    protected Direction getFacing(BlockState state) {
        return state.getValue(PropertyHolder.FACING_AXIS).direction();
    }
}
