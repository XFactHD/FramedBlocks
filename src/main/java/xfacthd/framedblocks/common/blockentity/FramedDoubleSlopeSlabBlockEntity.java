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

public class FramedDoubleSlopeSlabBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleSlopeSlabBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedDoubleSlopeSlab.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction side = hit.getDirection();

        if (side == facing.getOpposite() || side == Direction.UP) { return true; }
        if (side == facing || side == Direction.DOWN) { return false; }

        Vec3 vec = Utils.fraction(hit.getLocation());

        double hor = Utils.isX(facing) ? vec.x() : vec.z();
        if (!Utils.isPositive(facing))
        {
            hor = 1D - hor;
        }

        double y = vec.y();
        if (getBlockState().getValue(PropertyHolder.TOP_HALF))
        {
            y -= .5;
        }
        return (y * 2D) >= hor;
    }

    @Override
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.SECOND; }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == Direction.UP || side == facing.getOpposite())
        {
            return getCamoTwo();
        }
        else if (side == Direction.DOWN || side == facing)
        {
            return getCamo();
        }

        return EmptyCamoContainer.EMPTY;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        boolean topHalf = getBlockState().getValue(PropertyHolder.TOP_HALF);
        if (topHalf && side == Direction.UP)
        {
            //noinspection ConstantConditions
            return getCamoTwo().getState().isSolidRender(level, worldPosition);
        }
        else if (!topHalf && side == Direction.DOWN)
        {
            //noinspection ConstantConditions
            return getCamo().getState().isSolidRender(level, worldPosition);
        }
        return false;
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        return getBlockPair(state.getValue(FramedProperties.FACING_HOR), state.getValue(PropertyHolder.TOP_HALF));
    }

    public static Tuple<BlockState, BlockState> getBlockPair(Direction facing, boolean topHalf)
    {
        BlockState defState = FBContent.blockFramedSlopeSlab.get().defaultBlockState();
        return new Tuple<>(
                defState.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.TOP_HALF, topHalf)
                        .setValue(FramedProperties.TOP, false),
                defState.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.TOP_HALF, topHalf)
                        .setValue(FramedProperties.TOP, true)
        );
    }
}