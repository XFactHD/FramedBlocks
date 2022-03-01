package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.DoubleSoundMode;
import xfacthd.framedblocks.common.util.Utils;

public class FramedDoubleSlopeSlabTileEntity extends FramedDoubleTileEntity
{
    public FramedDoubleSlopeSlabTileEntity() { super(FBContent.tileTypeFramedDoubleSlopeSlab.get()); }

    @Override
    protected boolean hitSecondary(BlockRayTraceResult hit)
    {
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);
        Direction side = hit.getDirection();

        if (side == facing.getOpposite() || side == Direction.UP) { return true; }
        if (side == facing || side == Direction.DOWN) { return false; }

        Vector3d vec = Utils.fraction(hit.getLocation());

        double hor = Utils.isX(facing) ? vec.x() : vec.z();
        if (!Utils.isPositive(facing))
        {
            hor = 1D - hor;
        }

        double y = vec.y();
        if (getBlockState().getValue(PropertyHolder.TOP_HALF))
        {
            y -= .5;
        }
        return (y * 2D) >= hor;
    }

    @Override
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.SECOND; }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);

        if (side == Direction.UP || side == facing.getOpposite())
        {
            return getCamoStateTwo();
        }
        else if (side == Direction.DOWN || side == facing)
        {
            return getCamoState();
        }

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        boolean topHalf = getBlockState().getValue(PropertyHolder.TOP_HALF);
        if (topHalf && side == Direction.UP)
        {
            //noinspection ConstantConditions
            return getCamoStateTwo().isSolidRender(level, worldPosition);
        }
        else if (!topHalf && side == Direction.DOWN)
        {
            //noinspection ConstantConditions
            return getCamoState().isSolidRender(level, worldPosition);
        }
        return false;
    }
}