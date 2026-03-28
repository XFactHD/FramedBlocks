package io.github.xfacthd.framedblocks.common.blockentity.doubled.slopepanelcorner;

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

public class FramedExtendedDoubleCornerSlopePanelWallBlockEntity extends FramedDoubleBlockEntity {
    public FramedExtendedDoubleCornerSlopePanelWallBlockEntity(BlockPos pos, BlockState state) {
        super(FBContent.BE_TYPE_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL_WALL.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos) {
        Direction side = hit.getDirection();
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == dir) {
            return false;
        }

        HorizontalRotation rot = getBlockState().getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(dir);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite()) {
            return true;
        }

        Vec3 hitVec = hit.getLocation();
        if (side == dir.getOpposite()) {
            double xz1 = MathUtils.fractionInDir(hitVec, rotDir);
            double xz2 = MathUtils.fractionInDir(hitVec, perpRotDir);
            return xz1 < .5 || xz2 < .5;
        }

        double xzDir = MathUtils.fractionInDir(hitVec, dir.getOpposite());
        double xzPerp;
        if (DirUtils.isY(side)) {
            xzPerp = MathUtils.fractionInDir(hitVec, DirUtils.isY(rotDir) ? perpRotDir : rotDir);
        } else {
            xzPerp = MathUtils.fractionInDir(hitVec, DirUtils.isY(rotDir) ? rotDir : perpRotDir);
        }

        if (xzPerp > .5) {
            return false;
        }
        return xzDir > (xzPerp * 2D);
    }
}
