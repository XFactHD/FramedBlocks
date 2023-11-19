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

public class FramedDividedPanelBlockEntity extends FramedDoubleBlockEntity
{
    private final boolean vertical;

    public FramedDividedPanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DIVIDED_PANEL.value(), pos, state);
        this.vertical = state.getBlock() == FBContent.BLOCK_FRAMED_DIVIDED_PANEL_VERT.value();
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction face = hit.getDirection();
        if (vertical)
        {
            Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
            if (face == dir.getClockWise())
            {
                return true;
            }
            if (face == dir.getCounterClockWise())
            {
                return false;
            }

            double xz = Utils.fractionInDir(hit.getLocation(), dir.getClockWise());
            return xz > .5D;
        }
        else
        {
            return switch (face)
            {
                case UP -> true;
                case DOWN -> false;
                default ->
                {
                    Vec3 frac = Utils.fraction(hit.getLocation());
                    yield frac.y > .5D;
                }
            };
        }
    }
}
