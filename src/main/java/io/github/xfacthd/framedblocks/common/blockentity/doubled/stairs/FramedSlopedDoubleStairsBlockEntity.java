package io.github.xfacthd.framedblocks.common.blockentity.doubled.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FramedSlopedDoubleStairsBlockEntity extends FramedDoubleBlockEntity {
    public FramedSlopedDoubleStairsBlockEntity(BlockPos pos, BlockState state) {
        super(FBContent.BE_TYPE_FRAMED_SLOPED_DOUBLE_STAIRS.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos) {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = getBlockState().getValue(FramedProperties.TOP) ? Direction.DOWN : Direction.UP;

        Direction side = hit.getDirection();
        Vec3 hitVec = hit.getLocation();

        if (side == dirTwo) {
            double par = MathUtils.fractionInDir(hitVec, facing);
            double perp = MathUtils.fractionInDir(hitVec, facing.getClockWise());
            return perp > par;
        } else if (side == facing.getOpposite() || side == facing.getClockWise()) {
            return MathUtils.fractionInDir(hitVec, dirTwo) > .5;
        }
        return false;
    }
}
