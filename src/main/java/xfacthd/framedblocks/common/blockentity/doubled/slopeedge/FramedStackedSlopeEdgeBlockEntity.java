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
import xfacthd.framedblocks.common.data.property.SlopeType;

public class FramedStackedSlopeEdgeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedStackedSlopeEdgeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_STACKED_SLOPE_EDGE.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction side = hit.getDirection();

        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == dir)
        {
            return false;
        }

        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        Direction dirTwo = switch (type)
        {
            case BOTTOM -> Direction.DOWN;
            case HORIZONTAL -> dir.getCounterClockWise();
            case TOP -> Direction.UP;
        };
        if (side == dirTwo)
        {
            return false;
        }

        Vec3 hitVec = hit.getLocation();
        if (side == dir.getOpposite())
        {
            return Utils.fractionInDir(hitVec, dirTwo.getOpposite()) > .5;
        }
        else if (side == dirTwo.getOpposite())
        {
            return Utils.fractionInDir(hitVec, dir.getOpposite()) > .5;
        }

        double par = Utils.fractionInDir(hitVec, dir.getOpposite()) - .5;
        double perp = Utils.fractionInDir(hitVec, dirTwo.getOpposite()) - .5;
        return par > 0D && perp > 0D;
    }
}
