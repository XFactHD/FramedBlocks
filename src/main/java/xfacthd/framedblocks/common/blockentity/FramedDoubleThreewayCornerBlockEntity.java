package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDoubleThreewayCornerBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleThreewayCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeDoubleFramedThreewayCorner.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);
        boolean top = getBlockState().getValue(PropertyHolder.TOP);
        Direction side = hit.getDirection();

        Vec3 vec = Utils.fraction(hit.getLocation());

        if (top)
        {
            if (side == facing || side == Direction.UP || side == facing.getCounterClockWise()) { return false; }

            if (side == facing.getClockWise())
            {
                double hor = Utils.isX(facing) ? vec.x() : vec.z();
                if (!Utils.isPositive(facing))
                {
                    hor = 1D - hor;
                }
                return vec.y() <= (1D - hor);
            }
            else if (side == facing.getOpposite())
            {
                Direction dir = facing.getCounterClockWise();
                double hor = Utils.isX(dir) ? vec.x() : vec.z();
                if (!Utils.isPositive(dir))
                {
                    hor = 1D - hor;
                }
                return vec.y() <= (1D - hor);
            }
            else if (side == Direction.DOWN)
            {
                boolean secondary = Utils.isX(facing) ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());
                if (Utils.isPositive(facing)) { secondary = !secondary; }
                return secondary;
            }
        }
        else
        {
            if (side == facing || side == Direction.DOWN || side == facing.getCounterClockWise()) { return false; }

            if (side == facing.getClockWise())
            {
                double hor = Utils.isX(facing) ? vec.x() : vec.z();
                if (!Utils.isPositive(facing))
                {
                    hor = 1D - hor;
                }
                return vec.y() >= hor;
            }
            else if (side == facing.getOpposite())
            {
                Direction dir = facing.getCounterClockWise();
                double hor = Utils.isX(dir) ? vec.x() : vec.z();
                if (!Utils.isPositive(dir))
                {
                    hor = 1D - hor;
                }
                return vec.y() >= hor;
            }
            else if (side == Direction.UP)
            {
                boolean secondary = Utils.isX(facing) ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());
                if (Utils.isPositive(facing)) { secondary = !secondary; }
                return secondary;
            }
        }
        return false;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        return getBlockState().getValue(PropertyHolder.TOP) ? DoubleSoundMode.FIRST : DoubleSoundMode.EITHER;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction dir = getBlockState().getValue(PropertyHolder.FACING_HOR);
        boolean top = getBlockState().getValue(PropertyHolder.TOP);

        if (top)
        {
            if (side == dir || side == Direction.UP || side == dir.getCounterClockWise()) { return getCamoState(); }
            if (side == dir.getOpposite() || side == Direction.DOWN || side == dir.getClockWise()) { return getCamoStateTwo(); }
        }
        else
        {
            if (side == dir || side == Direction.DOWN || side == dir.getCounterClockWise()) { return getCamoState(); }
            if (side == dir.getOpposite() || side == Direction.UP || side == dir.getClockWise()) { return getCamoStateTwo(); }
        }

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction dir = getBlockState().getValue(PropertyHolder.FACING_HOR);
        boolean top = getBlockState().getValue(PropertyHolder.TOP);

        if (side == dir || side == dir.getCounterClockWise() || (side == Direction.DOWN && !top) || (side == Direction.UP && top))
        {
            //noinspection ConstantConditions
            return getCamoState(side).isSolidRender(level, worldPosition);
        }
        //noinspection ConstantConditions
        return getCamoState().isSolidRender(level, worldPosition) && getCamoStateTwo().isSolidRender(level, worldPosition);
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        if (getBlock().getBlockType() == BlockType.FRAMED_DOUBLE_PRISM_CORNER)
        {
            return getPrismBlockPair(
                    state.getValue(FramedProperties.FACING_HOR),
                    state.getValue(FramedProperties.TOP),
                    state.getValue(FramedProperties.OFFSET)
            );
        }
        else
        {
            return getThreewayBlockPair(
                    state.getValue(FramedProperties.FACING_HOR),
                    state.getValue(FramedProperties.TOP)
            );
        }
    }

    public static Tuple<BlockState, BlockState> getPrismBlockPair(Direction facing, boolean top, boolean offset)
    {
        return new Tuple<>(
                FBContent.blockFramedInnerPrismCorner.get().defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.TOP, top)
                        .setValue(FramedProperties.OFFSET, offset),
                FBContent.blockFramedPrismCorner.get().defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.TOP, !top)
                        .setValue(FramedProperties.OFFSET, !offset)
        );
    }

    public static Tuple<BlockState, BlockState> getThreewayBlockPair(Direction facing, boolean top)
    {
        return new Tuple<>(
                FBContent.blockFramedInnerThreewayCorner.get().defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.TOP, top),
                FBContent.blockFramedThreewayCorner.get().defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.TOP, !top)
        );
    }
}