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
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedExtendedDoubleSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    public FramedExtendedDoubleSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction face = hit.getDirection();
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (face == facing)
        {
            return false;
        }
        if (face == facing.getOpposite())
        {
            return true;
        }

        Direction orientation = getBlockState().getValue(PropertyHolder.ROTATION).withFacing(facing);
        if (face == orientation.getOpposite())
        {
            return false;
        }

        Vec3 vec = Utils.fraction(hit.getLocation());

        double hor = Utils.isX(facing) ? vec.x() : vec.z();
        if (Utils.isPositive(facing))
        {
            hor = 1D - hor;
        }
        if (face == orientation)
        {
            return hor > .5D;
        }
        hor -= .5D;

        double vert = Utils.isY(orientation) ? vec.y() : (Utils.isX(facing) ? vec.z() : vec.x());
        if (orientation == Direction.UP || (!Utils.isY(orientation) && Utils.isPositive(orientation)))
        {
            vert = 1F - vert;
        }
        return (hor * 2D) >= vert;
    }

    @Override
    protected DoubleBlockTopInteractionMode calculateTopInteractionMode()
    {
        HorizontalRotation rot = getBlockState().getValue(PropertyHolder.ROTATION);
        return rot == HorizontalRotation.DOWN ? DoubleBlockTopInteractionMode.FIRST : DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing)
        {
            return this::getCamo;
        }
        if (side == facing.getOpposite())
        {
            return this::getCamoTwo;
        }

        Direction orientation = getBlockState().getValue(PropertyHolder.ROTATION).withFacing(facing);
        if (side == orientation.getOpposite())
        {
            return this::getCamo;
        }
        else if (side.getAxis() != facing.getAxis())
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
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing)
        {
            return SolidityCheck.FIRST;
        }
        else if (side == facing.getOpposite())
        {
            return SolidityCheck.SECOND;
        }

        Direction orientation = getBlockState().getValue(PropertyHolder.ROTATION).withFacing(facing);
        if (side == orientation.getOpposite())
        {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.BOTH;
    }
}
