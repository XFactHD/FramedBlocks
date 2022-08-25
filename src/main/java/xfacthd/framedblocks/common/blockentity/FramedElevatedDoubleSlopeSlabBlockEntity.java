package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
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

public class FramedElevatedDoubleSlopeSlabBlockEntity extends FramedDoubleBlockEntity
{
    public FramedElevatedDoubleSlopeSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedElevatedDoubleSlopeSlab.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction side = hit.getDirection();

        if (side == Direction.UP) { return !top; }
        if (side == Direction.DOWN || side == facing) { return top; }

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
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.SECOND; }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        if (side == Direction.UP)
        {
            return top ? getCamo() : getCamoTwo();
        }
        else if (side == Direction.DOWN || side == facing)
        {
            return top ? getCamoTwo() : getCamo();
        }
        else if (side == facing.getOpposite())
        {
            return getCamoTwo();
        }

        return EmptyCamoContainer.EMPTY;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        if (side == Direction.UP)
        {
            //noinspection ConstantConditions
            return getCamoTwo().getState().isSolidRender(level, worldPosition);
        }
        else if (side == Direction.DOWN)
        {
            //noinspection ConstantConditions
            return getCamo().getState().isSolidRender(level, worldPosition);
        }

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing)
        {
            boolean top = getBlockState().getValue(FramedProperties.TOP);
            CamoContainer camo = top ? getCamoTwo() : getCamo();
            //noinspection ConstantConditions
            return camo.getState().isSolidRender(level, worldPosition);
        }
        return false;
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        return getBlockPair(state.getValue(FramedProperties.FACING_HOR), state.getValue(FramedProperties.TOP));
    }

    public static Tuple<BlockState, BlockState> getBlockPair(Direction facing, boolean top)
    {
        return new Tuple<>(
                FBContent.blockFramedElevatedSlopeSlab.get().defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.TOP, top),
                FBContent.blockFramedSlopeSlab.get().defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.TOP_HALF, !top)
                        .setValue(FramedProperties.TOP, !top)
        );
    }
}
