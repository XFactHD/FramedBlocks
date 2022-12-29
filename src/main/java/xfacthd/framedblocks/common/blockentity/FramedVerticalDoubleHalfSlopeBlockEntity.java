package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedVerticalDoubleHalfSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedVerticalDoubleHalfSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedVerticalDoubleHalfSlope.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        Direction side = hit.getDirection();

        if (side == facing || side == facing.getCounterClockWise())
        {
            return false;
        }
        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return true;
        }

        Vec3 vec = Utils.fraction(hit.getLocation());
        boolean secondary = Utils.isX(facing) ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());

        if (Utils.isPositive(facing))
        {
            secondary = !secondary;
        }
        return secondary;
    }

    @Override
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.EITHER; }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == facing || side == facing.getCounterClockWise())
        {
            return getCamoState();
        }
        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return getCamoStateTwo();
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        if ((!top && side == Direction.DOWN) || (top && side == Direction.UP))
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
                state.getValue(FramedProperties.FACING_HOR),
                state.getValue(FramedProperties.TOP)
        );
    }

    public static Tuple<BlockState, BlockState> getBlockPair(Direction facing, boolean top)
    {
        BlockState defState = FBContent.blockFramedVerticalHalfSlope.get().defaultBlockState();
        return new Tuple<>(
                defState.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.TOP, top),
                defState.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.TOP, top)
        );
    }
}
