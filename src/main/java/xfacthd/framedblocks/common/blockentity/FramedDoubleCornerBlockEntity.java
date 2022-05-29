package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.data.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDoubleCornerBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeDoubleFramedCorner.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        CornerType type = getBlockState().getValue(PropertyHolder.CORNER_TYPE);
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);
        Direction side = hit.getDirection();

        Vec3 vec = Utils.fraction(hit.getLocation());

        if (type.isHorizontal())
        {
            if (side == facing || (!type.isTop() && side == Direction.DOWN) || (type.isTop() && side == Direction.UP) ||
                (!type.isRight() && side == facing.getCounterClockWise()) || (type.isRight() && side == facing.getClockWise())
            ) { return false; }

            if (side == facing.getOpposite()) { return true; }

            if (Utils.isY(side))
            {
                boolean secondary;
                if (type.isRight())
                {
                    secondary = Utils.isX(facing) ? vec.x() >= (1D - vec.z()) : vec.z() >= vec.x();
                }
                else
                {
                   secondary = Utils.isX(facing) ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());
                }

                if (Utils.isPositive(facing)) { secondary = !secondary; }
                return secondary;
            }
            else if (side == facing.getClockWise() || side == facing.getCounterClockWise())
            {
                double hor = Utils.isX(facing) ? vec.x() : vec.z();
                if (!Utils.isPositive(facing))
                {
                    hor = 1D - hor;
                }

                boolean secondary;
                if (type.isTop())
                {
                    secondary = vec.y() <= (1D - hor);
                }
                else
                {
                    secondary = vec.y() >= hor;
                }

                return secondary;
            }
        }
        else if (type == CornerType.TOP)
        {
            if (side == facing || side == Direction.UP || side == facing.getCounterClockWise()) { return false; }
            if (side == Direction.DOWN) { return true; }

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
        }
        else if (type == CornerType.BOTTOM)
        {
            if (side == facing || side == Direction.DOWN || side == facing.getCounterClockWise()) { return false; }
            if (side == Direction.UP) { return true; }

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
        }
        return false;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        CornerType type = getBlockState().getValue(PropertyHolder.CORNER_TYPE);
        if (type == CornerType.BOTTOM)
        {
            return DoubleSoundMode.SECOND;
        }
        else if (type.isTop())
        {
            return DoubleSoundMode.FIRST;
        }
        return DoubleSoundMode.EITHER;
    }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        CornerType type = getBlockState().getValue(PropertyHolder.CORNER_TYPE);
        Direction dir = getBlockState().getValue(PropertyHolder.FACING_HOR);

        if (type.isHorizontal())
        {
            if (side == dir || (!type.isTop() && side == Direction.DOWN) || (type.isTop() && side == Direction.UP) ||
                (!type.isRight() && side == dir.getCounterClockWise()) || (type.isRight() && side == dir.getClockWise())
            )
            {
                return getCamo();
            }

            if (side == dir.getOpposite() || (!type.isTop() && side == Direction.UP) || (type.isTop() && side == Direction.DOWN) ||
                (!type.isRight() && side == dir.getClockWise()) || (type.isRight() && side == dir.getCounterClockWise())
            )
            {
                return getCamoTwo();
            }
        }
        else if (type == CornerType.TOP)
        {
            if (side == dir || side == Direction.UP || side == dir.getCounterClockWise()) { return getCamo(); }
            if (side == dir.getOpposite() || side == Direction.DOWN || side == dir.getClockWise()) { return getCamoTwo(); }
        }
        else if (type == CornerType.BOTTOM)
        {
            if (side == dir || side == Direction.DOWN || side == dir.getCounterClockWise()) { return getCamo(); }
            if (side == dir.getOpposite() || side == Direction.UP || side == dir.getClockWise()) { return getCamoTwo(); }
        }

        return EmptyCamoContainer.EMPTY;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        CornerType type = getBlockState().getValue(PropertyHolder.CORNER_TYPE);
        Direction dir = getBlockState().getValue(PropertyHolder.FACING_HOR);

        if (type.isHorizontal())
        {
            if ((!type.isRight() && side == dir.getCounterClockWise()) || (type.isRight() && side == dir.getClockWise()) ||
                (!type.isTop() && side == Direction.DOWN) || (type.isTop() && side == Direction.UP) ||
                side == dir || side == dir.getOpposite()
            )
            {
                //noinspection ConstantConditions
                return getCamo(side).getState().isSolidRender(level, worldPosition);
            }
        }
        else if (side == dir || side == dir.getCounterClockWise() || Utils.isY(side))
        {
            //noinspection ConstantConditions
            return getCamo(side).getState().isSolidRender(level, worldPosition);
        }

        //noinspection ConstantConditions
        return getCamo().getState().isSolidRender(level, worldPosition) && getCamoTwo().getState().isSolidRender(level, worldPosition);
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        return getBlockPair(state.getValue(PropertyHolder.CORNER_TYPE), state.getValue(FramedProperties.FACING_HOR));
    }

    public static Tuple<BlockState, BlockState> getBlockPair(CornerType type, Direction facing)
    {
        return new Tuple<>(
                FBContent.blockFramedInnerCornerSlope.get().defaultBlockState()
                        .setValue(PropertyHolder.CORNER_TYPE, type)
                        .setValue(FramedProperties.FACING_HOR, facing),
                FBContent.blockFramedCornerSlope.get().defaultBlockState()
                        .setValue(PropertyHolder.CORNER_TYPE, type.verticalOpposite())
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
        );
    }
}