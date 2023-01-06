package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedFlatDoubleSlopeSlabCornerBlockEntity extends FramedDoubleBlockEntity
{
    public FramedFlatDoubleSlopeSlabCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedFlatDoubleSlopeSlabCorner.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction side = hit.getDirection();

        if (side == Direction.UP) { return !top; }
        if (side == Direction.DOWN) { return top; }
        if (side == facing || side == facing.getCounterClockWise()) { return false; }

        Vec3 vec = Utils.fraction(hit.getLocation());

        Direction perpDir = side == facing.getClockWise() ? facing : facing.getCounterClockWise();
        double hor = Utils.isX(perpDir) ? vec.x() : vec.z();
        if (!Utils.isPositive(perpDir))
        {
            hor = 1D - hor;
        }

        double y = vec.y();
        if (getBlockState().getValue(PropertyHolder.TOP_HALF))
        {
            y -= .5;
        }
        if (top)
        {
            y = .5 - y;
        }
        return (y * 2D) >= hor;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        return getBlockState().getValue(FramedProperties.TOP) ? DoubleSoundMode.FIRST : DoubleSoundMode.SECOND;
    }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        if (side == Direction.UP) { return top ? getCamo() : getCamoTwo(); }
        if (side == Direction.DOWN) { return top ? getCamoTwo() : getCamo(); }

        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return getCamoTwo();
        }
        else if (side == facing || side == facing.getCounterClockWise())
        {
            return getCamo();
        }

        throw new IllegalStateException("This should not be possible!");
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        boolean topHalf = getBlockState().getValue(PropertyHolder.TOP_HALF);
        if (topHalf && side == Direction.UP)
        {
            CamoContainer camo = top ? getCamo() : getCamoTwo();
            //noinspection ConstantConditions
            return camo.getState().isSolidRender(level, worldPosition);
        }
        else if (!topHalf && side == Direction.DOWN)
        {
            CamoContainer camo = top ? getCamoTwo() : getCamo();
            //noinspection ConstantConditions
            return camo.getState().isSolidRender(level, worldPosition);
        }
        return false;
    }
}
