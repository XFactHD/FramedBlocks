package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedDividedSlabBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDividedSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DIVIDED_SLAB.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction face = hit.getDirection();
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (face == dir.getOpposite())
        {
            return false;
        }
        if (face == dir)
        {
            return true;
        }

        double xz = Utils.fractionInDir(hit.getLocation(), dir);
        return xz > .5D;
    }
}
