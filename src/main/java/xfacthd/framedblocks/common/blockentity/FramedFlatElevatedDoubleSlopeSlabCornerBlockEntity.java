package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedFlatElevatedDoubleSlopeSlabCornerBlockEntity extends FramedDoubleBlockEntity
{
    private final boolean isInner;

    public FramedFlatElevatedDoubleSlopeSlabCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedFlatElevatedDoubleSlopeSlabCorner.get(), pos, state);
        this.isInner = getBlockType() == BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER;
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction side = hit.getDirection();
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        if (side == Direction.UP) { return !top; }
        if (side == Direction.DOWN) { return top; }

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (isInner && (side == facing || side == facing.getCounterClockWise()))
        {
            return false;
        }
        else
        {
            Vec3 vec = Utils.fraction(hit.getLocation());
            if (!isInner && (side == facing.getOpposite() || side == facing.getClockWise()))
            {
                return (vec.y() >= .5D) != top;
            }
            else
            {
                Direction perpDir;
                if (isInner)
                {
                    perpDir = side == facing.getClockWise() ? facing : facing.getCounterClockWise();
                }
                else
                {
                    perpDir = side == facing ? facing.getCounterClockWise() : facing;
                }

                double hor = Utils.isX(perpDir) ? vec.x() : vec.z();
                if (!Utils.isPositive(perpDir))
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
        }
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        return getBlockState().getValue(FramedProperties.TOP) ? DoubleSoundMode.FIRST : DoubleSoundMode.SECOND;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        if (Utils.isY(side))
        {
            boolean top = getBlockState().getValue(FramedProperties.TOP);

            if (side == Direction.UP) { return top ? getCamoState() : getCamoStateTwo(); }
            if (side == Direction.DOWN) { return top ? getCamoStateTwo() : getCamoState(); }
        }

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing || side == facing.getCounterClockWise())
        {
            return getCamoState();
        }
        else if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return getCamoStateTwo();
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        if (Utils.isY(side))
        {
            boolean top = getBlockState().getValue(FramedProperties.TOP);
            BlockState camo = top == (side == Direction.UP) ? getCamoState() : getCamoStateTwo();
            //noinspection ConstantConditions
            return camo.isSolidRender(level, worldPosition);
        }
        if (isInner)
        {
            Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
            if (side == facing || side == facing.getCounterClockWise())
            {
                //noinspection ConstantConditions
                return getCamoState().isSolidRender(level, worldPosition);
            }
        }
        return false;
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        if (((IFramedBlock) state.getBlock()).getBlockType() == BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER)
        {
            return getBlockPairInner(
                    state.getValue(FramedProperties.FACING_HOR),
                    state.getValue(FramedProperties.TOP)
            );
        }
        else
        {
            return getBlockPair(
                    state.getValue(FramedProperties.FACING_HOR),
                    state.getValue(FramedProperties.TOP)
            );
        }
    }

    public static Tuple<BlockState, BlockState> getBlockPair(Direction facing, boolean top)
    {
        BlockState defStateOne = FBContent.blockFramedFlatElevatedSlopeSlabCorner.get().defaultBlockState();
        BlockState defStateTwo = FBContent.blockFramedFlatInnerSlopeSlabCorner.get().defaultBlockState();

        return new Tuple<>(
                defStateOne.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.TOP, top),
                defStateTwo.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.TOP, !top)
                        .setValue(PropertyHolder.TOP_HALF, !top)
        );
    }

    public static Tuple<BlockState, BlockState> getBlockPairInner(Direction facing, boolean top)
    {
        BlockState defStateOne = FBContent.blockFramedFlatElevatedInnerSlopeSlabCorner.get().defaultBlockState();
        BlockState defStateTwo = FBContent.blockFramedFlatSlopeSlabCorner.get().defaultBlockState();

        return new Tuple<>(
                defStateOne.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(FramedProperties.TOP, top),
                defStateTwo.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(FramedProperties.TOP, !top)
                        .setValue(PropertyHolder.TOP_HALF, !top)
        );
    }
}
