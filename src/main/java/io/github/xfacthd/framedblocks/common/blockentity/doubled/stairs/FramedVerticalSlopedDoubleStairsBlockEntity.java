package io.github.xfacthd.framedblocks.common.blockentity.doubled.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
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

public class FramedVerticalSlopedDoubleStairsBlockEntity extends FramedDoubleBlockEntity
{
    public FramedVerticalSlopedDoubleStairsBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_VERTICAL_SLOPED_DOUBLE_STAIRS.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = getBlockState().getValue(PropertyHolder.ROTATION);
        Direction dirTwo = rot.withFacing(facing);
        Direction dirThree = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);

        Direction side = hit.getDirection();
        Vec3 hitVec = hit.getLocation();

        if (side == dirTwo || side == dirThree)
        {
            return MathUtils.fractionInDir(hitVec, facing) < .5;
        }
        else if (side == facing.getOpposite())
        {
            double par = MathUtils.fractionInDir(hitVec, dirTwo.getOpposite());
            double perp = MathUtils.fractionInDir(hitVec, dirThree);
            return perp > par;
        }
        return false;
    }
}
