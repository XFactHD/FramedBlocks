package io.github.xfacthd.framedblocks.common.blockentity.doubled.slopepanel;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FramedFlatDoubleSlopePanelCornerBlockEntity extends FramedDoubleBlockEntity {
    public FramedFlatDoubleSlopePanelCornerBlockEntity(BlockPos pos, BlockState state) {
        super(FBContent.BE_TYPE_FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos) {
        Direction side = hit.getDirection();

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing) {
            return false;
        }
        if (side == facing.getOpposite()) {
            return true;
        }

        HorizontalRotation rotation = getBlockState().getValue(PropertyHolder.ROTATION);
        Direction rotDir = rotation.withFacing(facing);
        Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);
        if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite()) {
            return false;
        }

        Vec3 vec = MathUtils.fraction(hit.getLocation());

        double hor = DirUtils.isX(facing) ? vec.x() : vec.z();
        if (!DirUtils.isPositive(facing)) {
            hor = 1D - hor;
        }
        if (!getBlockState().getValue(PropertyHolder.FRONT)) {
            hor -= .5D;
        }

        Direction perpDir = side == rotDir ? perpRotDir : rotDir;
        double vert = DirUtils.isY(perpDir) ? vec.y() : (DirUtils.isX(facing) ? vec.z() : vec.x());
        if (perpDir == Direction.DOWN || (!DirUtils.isY(perpDir) && !DirUtils.isPositive(perpDir))) {
            vert = 1F - vert;
        }
        return (hor * 2D) < vert;
    }
}
