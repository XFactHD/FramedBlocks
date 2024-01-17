package xfacthd.framedblocks.common.blockentity.doubled.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;

public class FramedDoubleThreewayCornerPillarBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleThreewayCornerPillarBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DOUBLE_THREEWAY_CORNER_PILLAR.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        Direction face = hit.getDirection();
        Vec3 hitVec = hit.getLocation();
        if (Utils.isY(face))
        {
            boolean overHalfPar = Utils.fractionInDir(hitVec, dir.getOpposite()) > .5;
            boolean overHalfPerp = Utils.fractionInDir(hitVec, dir.getClockWise()) > .5;
            return face == dirTwo ? (overHalfPar && overHalfPerp) : (overHalfPar || overHalfPerp);
        }
        else if (face.getAxis() == dir.getAxis())
        {
            boolean overHalfHor = Utils.fractionInDir(hitVec, dir.getClockWise()) > .5;
            boolean overHalfVert = Utils.fractionInDir(hitVec, dirTwo.getOpposite()) > .5;
            return face == dir ? (overHalfHor && overHalfVert) : (overHalfHor || overHalfVert);
        }
        else
        {
            boolean overHalfHor = Utils.fractionInDir(hitVec, dir.getOpposite()) > .5;
            boolean overHalfVert = Utils.fractionInDir(hitVec, dirTwo.getOpposite()) > .5;
            return face == dir.getCounterClockWise() ? (overHalfHor && overHalfVert) : (overHalfHor || overHalfVert);
        }
    }
}
