package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDividedSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDividedSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedDividedSlope.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction dir;
        if (getBlockState().getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            dir = Direction.UP;
        }
        else
        {
            Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
            dir = facing.getClockWise();
        }

        Direction side = hit.getDirection();
        if (side == dir) { return true; }
        if (side == dir.getOpposite()) { return false; }

        return Utils.fractionInDir(hit.getLocation(), dir) > .5;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        if (getBlockState().getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return DoubleSoundMode.SECOND;
        }
        return DoubleSoundMode.EITHER;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        if (type == SlopeType.HORIZONTAL)
        {
            if (side == Direction.UP)
            {
                return getCamoStateTwo();
            }
            if (side == Direction.DOWN)
            {
                return getCamoState();
            }
        }
        else
        {
            Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
            if (side == facing.getClockWise())
            {
                return getCamoStateTwo();
            }
            if (side == facing.getCounterClockWise())
            {
                return getCamoState();
            }
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        Direction secDir = switch (type)
        {
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case HORIZONTAL -> facing.getCounterClockWise();
        };

        if (side == facing || side == secDir)
        {
            //noinspection ConstantConditions
            return getCamoState().isSolidRender(level, worldPosition) && getCamoStateTwo().isSolidRender(level, worldPosition);
        }

        return false;
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        return getBlockPair(
                state.getValue(PropertyHolder.SLOPE_TYPE),
                state.getValue(FramedProperties.FACING_HOR)
        );
    }

    public static Tuple<BlockState, BlockState> getBlockPair(SlopeType type, Direction facing)
    {
        if (type == SlopeType.HORIZONTAL)
        {
            BlockState defState = FBContent.blockFramedVerticalHalfSlope.get().defaultBlockState();
            return new Tuple<>(
                    defState.setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, false),
                    defState.setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, true)
            );
        }
        else
        {
            BlockState defState = FBContent.blockFramedHalfSlope.get().defaultBlockState();
            boolean top = type == SlopeType.TOP;
            return new Tuple<>(
                    defState.setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(PropertyHolder.RIGHT, false),
                    defState.setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(PropertyHolder.RIGHT, true)
            );
        }
    }
}
