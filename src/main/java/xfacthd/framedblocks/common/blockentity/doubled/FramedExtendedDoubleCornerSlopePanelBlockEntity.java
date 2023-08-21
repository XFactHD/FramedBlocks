package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedExtendedDoubleCornerSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    public FramedExtendedDoubleCornerSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction side = hit.getDirection();
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        if ((!top && side == Direction.DOWN) || (top && side == Direction.UP))
        {
            return false;
        }

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return true;
        }

        Vec3 hitVec = hit.getLocation();
        if ((!top && side == Direction.UP) || (top && side == Direction.DOWN))
        {
            double xz1 = Utils.fractionInDir(hitVec, facing.getOpposite());
            double xz2 = Utils.fractionInDir(hitVec, facing.getClockWise());
            return xz1 > .5 || xz2 > .5;
        }

        double xz = Utils.fractionInDir(hitVec, side == facing ? facing.getClockWise() : facing.getOpposite());
        if (xz < .5)
        {
            return false;
        }

        double y = Utils.fractionInDir(hitVec, top ? Direction.UP : Direction.DOWN);
        return ((xz - .5) * 2D) > y;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;
        if (side == dirTwo)
        {
            return this::getCamo;
        }
        else if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return this::getCamoTwo;
        }
        else if (side == facing)
        {
            if (edge == facing.getCounterClockWise() || edge == dirTwo)
            {
                return this::getCamo;
            }
            else if (edge == facing.getClockWise())
            {
                return this::getCamoTwo;
            }
        }
        else if (side == facing.getCounterClockWise())
        {
            if (edge == facing || edge == dirTwo)
            {
                return this::getCamo;
            }
            else if (edge == facing.getOpposite())
            {
                return this::getCamoTwo;
            }
        }
        else if (side == dirTwo.getOpposite() && (edge == facing.getClockWise() || edge == facing.getOpposite()))
        {
            return this::getCamoTwo;
        }
        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        if (Utils.isY(side))
        {
            boolean top = getBlockState().getValue(FramedProperties.TOP);
            if (top ? (side == Direction.UP) : (side == Direction.DOWN))
            {
                return SolidityCheck.FIRST;
            }
        }

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing.getOpposite() || side == facing.getClockWise())
        {
            return SolidityCheck.SECOND;
        }
        return SolidityCheck.BOTH;
    }
}
