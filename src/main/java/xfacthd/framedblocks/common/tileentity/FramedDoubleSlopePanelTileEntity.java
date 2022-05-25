package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.Rotation;
import xfacthd.framedblocks.common.util.DoubleSoundMode;
import xfacthd.framedblocks.common.util.Utils;

public class FramedDoubleSlopePanelTileEntity extends FramedDoubleTileEntity
{
    public FramedDoubleSlopePanelTileEntity()
    {
        super(FBContent.blockEntityTypeFramedDoubleSlopePanel.get());
    }

    @Override
    protected boolean hitSecondary(BlockRayTraceResult hit)
    {
        Direction face = hit.getDirection();
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);

        if (face == facing) { return false; }
        if (face == facing.getOpposite()) { return true; }

        Direction orientation = getBlockState().getValue(PropertyHolder.ROTATION).withFacing(facing);
        if (face == orientation) { return true; }
        if (face == orientation.getOpposite()) { return false; }

        Vector3d vec = Utils.fraction(hit.getLocation());

        double hor = Utils.isX(facing) ? vec.x() : vec.z();
        if (!Utils.isPositive(facing))
        {
            hor = 1D - hor;
        }
        if (!getBlockState().getValue(PropertyHolder.FRONT))
        {
            hor -= .5D;
        }

        double vert = Utils.isY(orientation) ? vec.y() : (Utils.isX(facing) ? vec.z() : vec.x());
        if (orientation == Direction.DOWN || (!Utils.isY(orientation) && !Utils.isPositive(orientation)))
        {
            vert = 1F - vert;
        }
        return (hor * 2D) < vert;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);

        if (side == facing) { return getCamoState(); }
        if (side == facing.getOpposite()) { return getCamoStateTwo(); }

        Direction orientation = getBlockState().getValue(PropertyHolder.ROTATION).withFacing(facing);
        if (side == orientation) { return getCamoStateTwo(); }
        if (side == orientation.getOpposite()) { return getCamoState(); }

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.EITHER; }
}
