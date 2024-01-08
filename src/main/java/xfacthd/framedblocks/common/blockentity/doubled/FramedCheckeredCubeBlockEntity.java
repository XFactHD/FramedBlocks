package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedCheckeredCubeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedCheckeredCubeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_CHECKERED_CUBE.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction face = hit.getDirection();
        Vec3 hitVec = Utils.fraction(hit.getLocation());
        if (Utils.isY(face))
        {
            boolean down = face == Direction.DOWN;
            return ((hitVec.x > .5) == (hitVec.z > .5)) ^ down;
        }
        else
        {
            boolean neg = !Utils.isPositive(face);
            double xz = Utils.isX(face) ? hitVec.z : hitVec.x;
            return ((xz > .5) == (hitVec.y > .5)) ^ neg;
        }
    }
}
