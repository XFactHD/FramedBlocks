package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedInverseDoubleSlopeSlabTileEntity extends FramedDoubleTileEntity
{
    public FramedInverseDoubleSlopeSlabTileEntity() { super(FBContent.tileTypeFramedInverseDoubleSlopeSlab.get()); }

    @Override
    protected boolean hitSecondary(BlockRayTraceResult hit)
    {
        return hit.getDirection() == Direction.UP || MathHelper.frac(hit.getLocation().y()) >= .5F;
    }

    @Override
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.SECOND; }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);

        if (side == Direction.UP || side == facing)
        {
            return getCamoStateTwo();
        }
        if (side == Direction.DOWN || side == facing.getOpposite())
        {
            return getCamoState();
        }

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side) { return false; }
}