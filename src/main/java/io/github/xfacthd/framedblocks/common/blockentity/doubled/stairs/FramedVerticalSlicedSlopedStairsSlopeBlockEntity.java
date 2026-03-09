package io.github.xfacthd.framedblocks.common.blockentity.doubled.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.api.util.Triangle;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FramedVerticalSlicedSlopedStairsSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedVerticalSlicedSlopedStairsSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_VERTICAL_SLICED_SLOPED_DOUBLE_STAIRS_SLOPE.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = getBlockState().getValue(PropertyHolder.ROTATION);
        Direction dirTwo = rot.getOpposite().withFacing(facing);
        Direction dirThree = rot.rotate(Rotation.CLOCKWISE_90).withFacing(facing);
        Direction side = hit.getDirection();
        Vec3 hitVec = hit.getLocation();

        if (side == facing)
        {
            double par = MathUtils.fractionInDir(hitVec, dirTwo);
            double perp = MathUtils.fractionInDir(hitVec, dirThree.getOpposite());
            return perp > par;
        }
        if (side == dirTwo || side == dirThree)
        {
            return false;
        }

        if (side == dirTwo.getOpposite() || side == dirThree.getOpposite())
        {
            if (MathUtils.fractionInDir(hitVec, facing) > .5) // Crosshair is definitely on half slope's half-width faces
            {
                return true;
            }
        }
        else if (side == facing.getOpposite())
        {
            double par = MathUtils.fractionInDir(hitVec, dirTwo.getOpposite());
            double perp = MathUtils.fractionInDir(hitVec, dirThree);
            if (perp > par) // Crosshair is definitely on slope's triangle face
            {
                return false;
            }
        }

        Triangle tri = FramedVerticalSlicedSlopedStairsPanelBlockEntity.getTriangle(facing, rot);
        Vec3 origin = eyePos.subtract(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        return tri.intersects(origin, lookVec.normalize());
    }
}
