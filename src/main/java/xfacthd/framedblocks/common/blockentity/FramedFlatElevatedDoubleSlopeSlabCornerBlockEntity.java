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
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedFlatElevatedDoubleSlopeSlabCornerBlockEntity extends FramedDoubleBlockEntity
{
    private final boolean isInner;

    public FramedFlatElevatedDoubleSlopeSlabCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedFlatElevatedDoubleSlopeSlabCorner.get(), pos, state);
        this.isInner = getBlockType() == BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER;
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction side = hit.getDirection();
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        if (side == Direction.UP) { return !top; }
        if (side == Direction.DOWN) { return top; }

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (isInner && (side == facing || side == facing.getCounterClockWise()))
        {
            return false;
        }
        else
        {
            Vec3 vec = Utils.fraction(hit.getLocation());
            if (!isInner && (side == facing.getOpposite() || side == facing.getClockWise()))
            {
                return (vec.y() >= .5D) != top;
            }
            else
            {
                Direction perpDir;
                if (isInner)
                {
                    perpDir = side == facing.getClockWise() ? facing : facing.getCounterClockWise();
                }
                else
                {
                    perpDir = side == facing ? facing.getCounterClockWise() : facing;
                }

                double hor = Utils.isX(perpDir) ? vec.x() : vec.z();
                if (!Utils.isPositive(perpDir))
                {
                    hor = 1D - hor;
                }

                double y = vec.y();
                if (top)
                {
                    y = 1D - y;
                }
                y -= .5D;
                return (y * 2D) >= hor;
            }
        }
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        return getBlockState().getValue(FramedProperties.TOP) ? DoubleSoundMode.FIRST : DoubleSoundMode.SECOND;
    }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        if (Utils.isY(side))
        {
            boolean top = getBlockState().getValue(FramedProperties.TOP);

            if (side == Direction.UP) { return top ? getCamo() : getCamoTwo(); }
            if (side == Direction.DOWN) { return top ? getCamoTwo() : getCamo(); }
        }

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing || side == facing.getCounterClockWise())
        {
            return getCamo();
        }
        else if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return getCamoTwo();
        }
        return EmptyCamoContainer.EMPTY;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        if (Utils.isY(side))
        {
            boolean top = getBlockState().getValue(FramedProperties.TOP);
            CamoContainer camo = top == (side == Direction.UP) ? getCamo() : getCamoTwo();
            //noinspection ConstantConditions
            return camo.getState().isSolidRender(level, worldPosition);
        }
        if (isInner)
        {
            Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
            if (side == facing || side == facing.getCounterClockWise())
            {
                //noinspection ConstantConditions
                return getCamo().getState().isSolidRender(level, worldPosition);
            }
        }
        return false;
    }
}
