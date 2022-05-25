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

public class FramedInverseDoubleSlopePanelTileEntity extends FramedDoubleTileEntity
{
    public FramedInverseDoubleSlopePanelTileEntity()
    {
        super(FBContent.blockEntityTypeFramedInverseDoubleSlopePanel.get());
    }

    @Override
    protected boolean hitSecondary(BlockRayTraceResult hit)
    {
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);
        Direction side = hit.getDirection();
        Vector3d vec = Utils.fraction(hit.getLocation());

        if (side == facing) { return false; }
        if (side == facing.getOpposite()) { return true; }

        boolean second;
        if (Utils.isZ(facing))
        {
            second = vec.z() > .5F;
        }
        else
        {
            second = vec.x() <= .5F;
        }

        if (Utils.isPositive(facing) == Utils.isZ(facing))
        {
            second = !second;
        }

        return second;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);

        if (side == facing) { return getCamoState(); }
        if (side == facing.getOpposite()) { return getCamoStateTwo(); }

        Direction rotation = getBlockState().getValue(PropertyHolder.ROTATION).withFacing(facing);
        if (side == rotation) { return getCamoState(); }
        if (side == rotation.getOpposite()) { return getCamoStateTwo(); }

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.EITHER; }
}
