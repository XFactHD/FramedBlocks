package io.github.xfacthd.framedblocks.common.blockentity.doubled.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FramedDoubleCornerBlockEntity extends FramedDoubleBlockEntity {
    public FramedDoubleCornerBlockEntity(BlockPos pos, BlockState state) {
        super(FBContent.BE_TYPE_DOUBLE_FRAMED_CORNER.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos) {
        CornerType type = getBlockState().getValue(PropertyHolder.CORNER_TYPE);
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction side = hit.getDirection();

        Vec3 vec = MathUtils.fraction(hit.getLocation());

        if (type.isHorizontal()) {
            if (side == facing || (!type.isTop() && side == Direction.DOWN) || (type.isTop() && side == Direction.UP) ||
                (!type.isRight() && side == facing.getCounterClockWise()) || (type.isRight() && side == facing.getClockWise())
            ) {
                return false;
            }

            if (side == facing.getOpposite()) {
                return true;
            }

            if (DirUtils.isY(side)) {
                boolean secondary;
                if (type.isRight()) {
                    secondary = DirUtils.isX(facing) ? vec.x() >= (1D - vec.z()) : vec.z() >= vec.x();
                } else {
                    secondary = DirUtils.isX(facing) ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());
                }

                if (DirUtils.isPositive(facing)) { secondary = !secondary; }
                return secondary;
            } else if (side == facing.getClockWise() || side == facing.getCounterClockWise()) {
                double hor = DirUtils.isX(facing) ? vec.x() : vec.z();
                if (!DirUtils.isPositive(facing)) {
                    hor = 1D - hor;
                }

                boolean secondary;
                if (type.isTop()) {
                    secondary = vec.y() <= (1D - hor);
                } else {
                    secondary = vec.y() >= hor;
                }

                return secondary;
            }
        } else if (type == CornerType.TOP) {
            if (side == facing || side == Direction.UP || side == facing.getCounterClockWise()) {
                return false;
            }
            if (side == Direction.DOWN) {
                return true;
            }

            if (side == facing.getClockWise()) {
                double hor = DirUtils.isX(facing) ? vec.x() : vec.z();
                if (!DirUtils.isPositive(facing)) {
                    hor = 1D - hor;
                }
                return vec.y() <= (1D - hor);
            } else if (side == facing.getOpposite()) {
                Direction dir = facing.getCounterClockWise();
                double hor = DirUtils.isX(dir) ? vec.x() : vec.z();
                if (!DirUtils.isPositive(dir)) {
                    hor = 1D - hor;
                }
                return vec.y() <= (1D - hor);
            }
        } else if (type == CornerType.BOTTOM) {
            if (side == facing || side == Direction.DOWN || side == facing.getCounterClockWise()) {
                return false;
            }
            if (side == Direction.UP) {
                return true;
            }

            if (side == facing.getClockWise()) {
                double hor = DirUtils.isX(facing) ? vec.x() : vec.z();
                if (!DirUtils.isPositive(facing)) {
                    hor = 1D - hor;
                }
                return vec.y() >= hor;
            } else if (side == facing.getOpposite()) {
                Direction dir = facing.getCounterClockWise();
                double hor = DirUtils.isX(dir) ? vec.x() : vec.z();
                if (!DirUtils.isPositive(dir)) {
                    hor = 1D - hor;
                }
                return vec.y() >= hor;
            }
        }
        return false;
    }
}
