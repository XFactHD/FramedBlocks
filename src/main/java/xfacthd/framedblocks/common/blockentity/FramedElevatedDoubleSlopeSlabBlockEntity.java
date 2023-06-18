package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedElevatedDoubleSlopeSlabBlockEntity extends FramedDoubleBlockEntity
{
    public FramedElevatedDoubleSlopeSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction side = hit.getDirection();

        if (side == Direction.UP)
        {
            return !top;
        }
        if (side == Direction.DOWN || side == facing)
        {
            return top;
        }

        Vec3 vec = Utils.fraction(hit.getLocation());
        if (side == facing.getOpposite())
        {
            return (vec.y() >= .5D) != top;
        }

        double hor = Utils.isX(facing) ? vec.x() : vec.z();
        if (!Utils.isPositive(facing))
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

    @Override
    public DoubleSoundMode getSoundMode()
    {
        return DoubleSoundMode.SECOND;
    }

    @Override
    public CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == dirTwo || side == facing)
        {
            return this::getCamo;
        }
        else if (side == dirTwo.getOpposite())
        {
            return this::getCamoTwo;
        }
        else if (!Utils.isY(side))
        {
            if (edge == dirTwo)
            {
                return this::getCamo;
            }
            else if (edge == dirTwo.getOpposite())
            {
                return this::getCamoTwo;
            }
        }

        return EMPTY_GETTER;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        if (side == Direction.UP)
        {
            return getCamoTwo().isSolid(level, worldPosition);
        }
        else if (side == Direction.DOWN)
        {
            return getCamo().isSolid(level, worldPosition);
        }

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing)
        {
            boolean top = getBlockState().getValue(FramedProperties.TOP);
            CamoContainer camo = top ? getCamoTwo() : getCamo();
            return camo.isSolid(level, worldPosition);
        }
        return false;
    }
}
