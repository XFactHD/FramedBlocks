package xfacthd.framedblocks.common.blockentity.doubled.slab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;

public class FramedCheckeredSlabBlockEntity extends FramedDoubleBlockEntity
{
    public FramedCheckeredSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_CHECKERED_SLAB.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction face = hit.getDirection();
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Vec3 hitVec = Utils.fraction(hit.getLocation());
        if (Utils.isY(face))
        {
            return ((hitVec.x > .5) == (hitVec.z > .5)) ^ (!top);
        }
        else
        {
            boolean neg = !Utils.isPositive(face);
            double xz = Utils.isX(face) ? hitVec.z : hitVec.x;
            return ((xz > .5) == top) ^ neg;
        }
    }
}
