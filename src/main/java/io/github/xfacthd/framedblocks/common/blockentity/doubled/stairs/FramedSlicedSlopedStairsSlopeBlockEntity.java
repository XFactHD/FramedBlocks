package io.github.xfacthd.framedblocks.common.blockentity.doubled.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.api.util.Triangle;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FramedSlicedSlopedStairsSlopeBlockEntity extends FramedDoubleBlockEntity {
    public FramedSlicedSlopedStairsSlopeBlockEntity(BlockPos pos, BlockState state) {
        super(FBContent.BE_TYPE_FRAMED_SLICED_SLOPED_DOUBLE_STAIRS_SLOPE.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos) {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = getBlockState().getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;
        Direction side = hit.getDirection();
        Vec3 hitVec = hit.getLocation();

        if (side == facing || side == facing.getCounterClockWise()) {
            return false;
        }
        if (side == dirTwo) {
            double par = MathUtils.fractionInDir(hitVec, facing);
            double perp = MathUtils.fractionInDir(hitVec, facing.getClockWise());
            return perp > par;
        }

        if (side == facing.getOpposite() || side == facing.getClockWise()) {
            if (MathUtils.fractionInDir(hitVec, dirTwo) > .5) { // Crosshair is definitely on half slope's half-width faces
                return true;
            }
        } else if (side == dirTwo.getOpposite()) {
            double par = MathUtils.fractionInDir(hitVec, facing);
            double perp = MathUtils.fractionInDir(hitVec, facing.getClockWise());
            if (perp < par) { // Crosshair is definitely on slope's triangle face
                return false;
            }
        }

        Triangle tri = FramedSlicedSlopedStairsSlabBlockEntity.TRIANGLES[facing.get2DDataValue()];
        Vec3 origin = eyePos.subtract(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        return tri.intersects(origin, lookVec.normalize());
    }
}
