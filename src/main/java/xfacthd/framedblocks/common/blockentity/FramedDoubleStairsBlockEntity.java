package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDoubleStairsBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleStairsBlockEntity(BlockPos worldPosition, BlockState blockState)
    {
        super(FBContent.BE_TYPE_FRAMED_DOUBLE_STAIRS.get(), worldPosition, blockState);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction side = hit.getDirection();

        if (side == facing || (top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            return false;
        }

        Vec3 vec = Utils.fraction(hit.getLocation());

        if (side == facing.getOpposite())
        {
            return (vec.y > .5) ^ top;
        }
        else if ((!top && side == Direction.UP) || (top && side == Direction.DOWN))
        {
            double xz = Utils.isX(facing) ? vec.x : vec.z;
            boolean positive = Utils.isPositive(facing);
            return xz > .5 != positive;
        }
        else
        {
            if (vec.y > .5 == top)
            {
                return false;
            }
            double xz = Utils.isX(facing) ? vec.x : vec.z;
            boolean positive = Utils.isPositive(facing);
            return xz > .5 != positive;
        }
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        return top ? DoubleSoundMode.FIRST : DoubleSoundMode.EITHER;
    }

    @Override
    public CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == facing || side == dirTwo)
        {
            return this::getCamo;
        }
        else if (side == facing.getOpposite())
        {
            if (edge == dirTwo)
            {
                return this::getCamo;
            }
            else if (edge == dirTwo.getOpposite())
            {
                return this::getCamoTwo;
            }
        }
        else if (side == dirTwo.getOpposite())
        {
            if (edge == facing)
            {
                return this::getCamo;
            }
            else if (edge == facing.getOpposite())
            {
                return this::getCamoTwo;
            }
        }

        return EMPTY_GETTER;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        if (side == facing || (top && side == Direction.UP) || (!top && side == Direction.DOWN))
        {
            return getCamo().isSolid(level, worldPosition);
        }
        return false;
    }
}
