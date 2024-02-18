package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedDoubleHalfStairsBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleHalfStairsBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DOUBLE_HALF_STAIRS.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        Direction face = hit.getDirection();
        Vec3 hitVec = hit.getLocation();
        if (face == dir || face == dirTwo)
        {
            return false;
        }
        else if (face == dir.getOpposite())
        {
            return Utils.fractionInDir(hitVec, dirTwo.getOpposite()) > .5;
        }
        else if (face == dirTwo.getOpposite())
        {
            return Utils.fractionInDir(hitVec, dir.getOpposite()) > .5;
        }
        else
        {
            boolean overHalfHor = Utils.fractionInDir(hitVec, dir.getOpposite()) > .5;
            boolean overHalfVert = Utils.fractionInDir(hitVec, dirTwo.getOpposite()) > .5;
            return overHalfHor && overHalfVert;
        }
    }
}
