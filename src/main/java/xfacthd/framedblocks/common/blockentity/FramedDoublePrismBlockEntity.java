package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
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

        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
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
        else if (getBlockState().getValue(BlockStateProperties.FACING) == Direction.DOWN)
        {
            return DoubleSoundMode.SECOND;
        }
        return DoubleSoundMode.FIRST;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        if (side == facing)
        {
            return getCamoStateTwo();
        }
        if (isDoubleSide(side))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return getCamoState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        if (side == facing)
        {
            //noinspection ConstantConditions
            return getCamoStateTwo().isSolidRender(level, worldPosition);
        }
        if (isDoubleSide(side))
        {
            return false;
        }
        //noinspection ConstantConditions
        return getCamoState().isSolidRender(level, worldPosition);
    }

    protected boolean isDoubleSide(Direction side)
    {
        return side.getAxis() == getBlockState().getValue(BlockStateProperties.AXIS);
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        return getBlockPair(
                state.getValue(BlockStateProperties.FACING),
                state.getValue(BlockStateProperties.AXIS)
        );
    }

    public static Tuple<BlockState, BlockState> getBlockPair(Direction facing, Direction.Axis axis)
    {
        return new Tuple<>(
                FBContent.blockFramedInnerPrism.get()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.FACING, facing)
                        .setValue(BlockStateProperties.AXIS, axis),
                FBContent.blockFramedPrism.get()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.FACING, facing.getOpposite())
                        .setValue(BlockStateProperties.AXIS, axis)
        );
    }
}
