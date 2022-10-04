package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
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
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        if (side == Direction.UP) { return top ? getCamoState() : getCamoStateTwo(); }
        if (side == Direction.DOWN) { return top ? getCamoStateTwo() : getCamoState(); }

        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return getCamoStateTwo();
        }
        else if (side == facing || side == facing.getCounterClockWise())
        {
            return getCamoState();
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
            BlockState camo = top ? getCamoState() : getCamoStateTwo();
            //noinspection ConstantConditions
            return camo.isSolidRender(level, worldPosition);
        }
        else if (!topHalf && side == Direction.DOWN)
        {
            BlockState camo = top ? getCamoStateTwo() : getCamoState();
            //noinspection ConstantConditions
            return camo.isSolidRender(level, worldPosition);
        }
        return false;
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        return getBlockPair(
                state.getValue(FramedProperties.FACING_HOR),
                state.getValue(PropertyHolder.TOP_HALF),
                state.getValue(FramedProperties.TOP)
        );
    }

    public static Tuple<BlockState, BlockState> getBlockPair(Direction facing, boolean topHalf, boolean top)
    {
        return new Tuple<>(
                FBContent.blockFramedFlatInnerSlopeSlabCorner.get()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.TOP_HALF, topHalf)
                        .setValue(FramedProperties.TOP, top),
                FBContent.blockFramedFlatSlopeSlabCorner.get()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.TOP_HALF, topHalf)
                        .setValue(FramedProperties.TOP, !top)
        );
    }
}
