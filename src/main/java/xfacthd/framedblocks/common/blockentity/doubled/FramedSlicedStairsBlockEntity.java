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

public class FramedSlicedStairsBlockEntity extends FramedDoubleBlockEntity
{
    private final boolean panel;

    public FramedSlicedStairsBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_SLICED_STAIRS.get(), pos, state);
        this.panel = state.is(FBContent.BLOCK_FRAMED_SLICED_STAIRS_PANEL.get());
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        Direction face = hit.getDirection();
        Vec3 hitVec = hit.getLocation();
        if (panel)
        {
            if (face == dir)
            {
                return false;
            }
            else if (face == dir.getOpposite())
            {
                return Utils.fractionInDir(hitVec, dirTwo) > .5;
            }
            return Utils.fractionInDir(hitVec, dir.getOpposite()) > .5;
        }
        else
        {
            if (face == dirTwo)
            {
                return false;
            }
            else if (face == dirTwo.getOpposite())
            {
                return Utils.fractionInDir(hitVec, dir) > .5;
            }
            return Utils.fractionInDir(hitVec, dirTwo.getOpposite()) > .5;
        }
    }
}
