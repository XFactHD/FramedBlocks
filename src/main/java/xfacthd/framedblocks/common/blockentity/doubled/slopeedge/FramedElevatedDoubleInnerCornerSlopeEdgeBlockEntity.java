package xfacthd.framedblocks.common.blockentity.doubled.slopeedge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

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
            double offX = Utils.fractionInDir(hitVec, xFront);
            double offY = Utils.fractionInDir(hitVec, yFront);
            return offX > .5D && offY > .5D;
        }
        else if (side == xFront || side == yFront)
        {
            double offY = (Utils.fractionInDir(hitVec, baseFace.getOpposite()) - .5D) * 2D;
            double offXZ = (Utils.fractionInDir(hitVec, side == xFront ? yFront : xFront) - .5D) * 2D;
            return offXZ >= 0D && offY >= (1D - offXZ);
        }
        return false;
    }
}
