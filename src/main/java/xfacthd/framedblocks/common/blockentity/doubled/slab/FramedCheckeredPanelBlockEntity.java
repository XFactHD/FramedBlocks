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

public class FramedCheckeredPanelBlockEntity extends FramedDoubleBlockEntity
{
    public FramedCheckeredPanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_CHECKERED_PANEL.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction face = hit.getDirection();
        if (Utils.isY(face))
        {
            Vec3 hitVec = Utils.fraction(hit.getLocation());
            boolean down = face == Direction.DOWN;
            return ((hitVec.x > .5) == (hitVec.z > .5)) ^ down;
        }
        else
        {
            Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
            double y = Utils.fractionInDir(hit.getLocation(), Direction.UP);
            if (face.getAxis() == dir.getAxis())
            {
                Direction coordDir = Utils.isX(dir) ? dir.getCounterClockWise() : dir.getClockWise();
                double xz = Utils.fractionInDir(hit.getLocation(), coordDir);
                return (y > .5) ^ (xz > .5);
            }

            boolean posDir = Utils.isPositive(dir);
            boolean posFace = Utils.isPositive(face);
            return (y > .5) ^ (posFace ^ posDir);
        }
    }
}
