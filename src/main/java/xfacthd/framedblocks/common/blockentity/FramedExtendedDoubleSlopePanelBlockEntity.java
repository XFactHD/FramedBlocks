package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.data.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedExtendedDoubleSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    public FramedExtendedDoubleSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedExtendedDoubleSlopePanel.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction face = hit.getDirection();
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (face == facing) { return false; }
        if (face == facing.getOpposite()) { return true; }

        Direction orientation = getBlockState().getValue(PropertyHolder.ROTATION).withFacing(facing);
        if (face == orientation.getOpposite()) { return false; }

        Vec3 vec = Utils.fraction(hit.getLocation());

        double hor = Utils.isX(facing) ? vec.x() : vec.z();
        if (Utils.isPositive(facing))
        {
            hor = 1D - hor;
        }
        if (face == orientation)
        {
            return hor > .5D;
        }
        hor -= .5D;

        double vert = Utils.isY(orientation) ? vec.y() : (Utils.isX(facing) ? vec.z() : vec.x());
        if (orientation == Direction.UP || (!Utils.isY(orientation) && Utils.isPositive(orientation)))
        {
            vert = 1F - vert;
        }
        return (hor * 2D) >= vert;
    }

    @Override
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.EITHER; }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing) { return getCamo(); }
        if (side == facing.getOpposite()) { return getCamoTwo(); }

        Direction orientation = getBlockState().getValue(PropertyHolder.ROTATION).withFacing(facing);
        if (side == orientation) { return getCamoTwo(); }
        if (side == orientation.getOpposite()) { return getCamo(); }

        return EmptyCamoContainer.EMPTY;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side.getAxis() == facing.getAxis())
        {
            //noinspection ConstantConditions
            return getCamo(side).getState().isSolidRender(level, worldPosition);
        }

        Direction orientation = getBlockState().getValue(PropertyHolder.ROTATION).withFacing(facing);
        //noinspection ConstantConditions
        return side == orientation.getOpposite() && getCamo().getState().isSolidRender(level, worldPosition);
    }
}
