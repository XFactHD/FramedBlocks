package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.Utils;

public class FramedDoublePanelTileEntity extends FramedDoubleTileEntity
{
    public FramedDoublePanelTileEntity() { super(FBContent.tileTypeDoubleFramedPanel.get()); }

    @Override
    protected boolean hitSecondary(BlockRayTraceResult hit)
    {
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_NE);
        Direction side = hit.getDirection();
        Vector3d vec = Utils.fraction(hit.getLocation());

        if (side == facing) { return false; }
        if (side == facing.getOpposite()) { return true; }

        if (facing == Direction.NORTH)
        {
            return vec.z() > .5F;
        }
        else
        {
            return vec.x() <= .5F;
        }
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_NE);
        if (side == facing) { return getCamoState(); }
        if (side == facing.getOpposite()) { return getCamoStateTwo(); }
        return Blocks.AIR.defaultBlockState();
    }
}