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

public class FramedExtendedInnerDoubleCornerSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    public FramedExtendedInnerDoubleCornerSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_EXTENDED_INNER_DOUBLE_CORNER_SLOPE_PANEL.get(), pos, state);
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
            return false;
        }

        Vec3 hitVec = hit.getLocation();
        if ((!top && side == Direction.UP) || (top && side == Direction.DOWN))
        {
            double xz1 = Utils.fractionInDir(hitVec, facing);
            double xz2 = Utils.fractionInDir(hitVec, facing.getCounterClockWise());
            return xz1 > .5 && xz2 > .5;
        }

        double xz = Utils.fractionInDir(hitVec, side == facing ? facing.getCounterClockWise() : facing);
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
        if (side == facing.getOpposite() || side == facing.getClockWise() || side == dirTwo)
        {
            return this::getCamo;
        }
        else if (side == dirTwo.getOpposite() && (edge == facing.getOpposite() || edge == facing.getClockWise()))
        {
            return this::getCamo;
        }
        else if (side == facing)
        {
            if (edge == dirTwo || edge == facing.getClockWise())
            {
                return this::getCamo;
            }
            else if (edge == facing.getCounterClockWise())
            {
                return this::getCamoTwo;
            }
        }
        else if (side == facing.getCounterClockWise())
        {
            if (edge == dirTwo || edge == facing.getOpposite())
            {
                return this::getCamo;
            }
            else if (edge == facing)
            {
                return this::getCamoTwo;
            }
        }
        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        boolean primaryYFace = false;
        if (Utils.isY(side))
        {
            boolean top = getBlockState().getValue(FramedProperties.TOP);
            primaryYFace = top ? (side == Direction.UP) : (side == Direction.DOWN);
        }

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (primaryYFace || side == facing.getOpposite() || side == facing.getClockWise())
        {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.BOTH;
    }
}
