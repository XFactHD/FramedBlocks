package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDoublePrismBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoublePrismBlockEntity(BlockPos pos, BlockState state)
    {
        this(FBContent.blockEntityTypeFramedDoublePrism.get(), pos, state);
    }

    protected FramedDoublePrismBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction side = hit.getDirection();

        Direction facing = getFacing(getBlockState());
        if (side == facing) { return true; }
        if (side == facing.getOpposite()) { return false; }
        if (!isDoubleSide(side) && side.getAxis() != facing.getAxis())
        {
            return false;
        }

        if (isDoubleSide(side))
        {
            Direction horDir = side.getClockWise(facing.getAxis());
            double hor = Utils.fractionInDir(hit.getLocation(), horDir);
            hor = Math.abs(hor - .5);

            double vert = Utils.fractionInDir(hit.getLocation(), facing) - .5;

            return vert > hor;
        }

        return false;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        if (isDoubleSide(Direction.UP))
        {
            return DoubleSoundMode.EITHER;
        }
        else if (getFacing(getBlockState()) == Direction.DOWN)
        {
            return DoubleSoundMode.SECOND;
        }
        return DoubleSoundMode.FIRST;
    }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        Direction facing = getFacing(getBlockState());
        if (side == facing)
        {
            return getCamoTwo();
        }
        if (isDoubleSide(side))
        {
            return EmptyCamoContainer.EMPTY;
        }
        return getCamo();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction facing = getFacing(getBlockState());
        if (side == facing)
        {
            //noinspection ConstantConditions
            return getCamoTwo().getState().isSolidRender(level, worldPosition);
        }
        if (isDoubleSide(side))
        {
            return false;
        }
        //noinspection ConstantConditions
        return getCamo().getState().isSolidRender(level, worldPosition);
    }

    protected boolean isDoubleSide(Direction side)
    {
        return side.getAxis() == getBlockState().getValue(PropertyHolder.FACING_AXIS).axis();
    }

    protected Direction getFacing(BlockState state)
    {
        return state.getValue(PropertyHolder.FACING_AXIS).direction();
    }
}
