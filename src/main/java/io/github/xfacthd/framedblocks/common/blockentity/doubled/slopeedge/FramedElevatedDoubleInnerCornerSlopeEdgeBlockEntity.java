package io.github.xfacthd.framedblocks.common.blockentity.doubled.slopeedge;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CornerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FramedElevatedDoubleInnerCornerSlopeEdgeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedElevatedDoubleInnerCornerSlopeEdgeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_ELEVATED_DOUBLE_INNER_CORNER_SLOPE_EDGE.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
    {
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
        CornerType type = getBlockState().getValue(PropertyHolder.CORNER_TYPE);
        Direction side = hit.getDirection();
        Direction baseFace = switch (type)
        {
            case BOTTOM -> Direction.DOWN;
            case TOP -> Direction.UP;
            default -> dir;
        };

        Direction xFront;
        Direction yFront;
        if (type.isHorizontal())
        {
            xFront = type.isRight() ? dir.getCounterClockWise() : dir.getClockWise();
            yFront = type.isTop() ? Direction.DOWN : Direction.UP;
        }
        else
        {
            xFront = dir.getClockWise();
            yFront = dir.getOpposite();
        }

        if (side == baseFace || side == xFront.getOpposite() || side == yFront.getOpposite()) return false;

        Vec3 hitVec = hit.getLocation();
        if (side == baseFace.getOpposite())
        {
            double offX = MathUtils.fractionInDir(hitVec, xFront);
            double offY = MathUtils.fractionInDir(hitVec, yFront);
            return offX > .5D && offY > .5D;
        }
        else if (side == xFront || side == yFront)
        {
            double offY = (MathUtils.fractionInDir(hitVec, baseFace.getOpposite()) - .5D) * 2D;
            double offXZ = (MathUtils.fractionInDir(hitVec, side == xFront ? yFront : xFront) - .5D) * 2D;
            return offXZ >= 0D && offY >= (1D - offXZ);
        }
        return false;
    }
}
