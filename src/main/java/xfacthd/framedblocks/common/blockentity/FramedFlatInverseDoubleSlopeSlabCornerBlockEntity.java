package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedFlatInverseDoubleSlopeSlabCornerBlockEntity extends FramedDoubleBlockEntity
{
    public FramedFlatInverseDoubleSlopeSlabCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedFlatInverseDoubleSlopeSlabCorner.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        if (hit.getDirection() == Direction.DOWN)
        {
            return top;
        }
        else if (hit.getDirection() == Direction.UP)
        {
            return !top;
        }
        return (Mth.frac(hit.getLocation().y()) >= .5F) != top;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        return top ? DoubleSoundMode.FIRST : DoubleSoundMode.SECOND;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == facing || side == facing.getCounterClockWise())
        {
            return getCamoStateTwo();
        }
        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return getCamoState();
        }

        boolean top = getBlockState().getValue(FramedProperties.TOP);
        return top == (side == Direction.UP) ? getCamoState() : getCamoStateTwo();
    }

    @Override
    public boolean isSolidSide(Direction side) { return false; }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        return getBlockPair(state.getValue(FramedProperties.FACING_HOR), state.getValue(FramedProperties.TOP));
    }

    public static Tuple<BlockState, BlockState> getBlockPair(Direction facing, boolean top)
    {
        return new Tuple<>(
                FBContent.blockFramedFlatInnerSlopeSlabCorner.get()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.TOP_HALF, top)
                        .setValue(FramedProperties.TOP, !top),
                FBContent.blockFramedFlatSlopeSlabCorner.get()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.TOP_HALF, !top)
                        .setValue(FramedProperties.TOP, top)
        );
    }
}
