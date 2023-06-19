package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDoubleSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DOUBLE_SLOPE_PANEL.get(), pos, state);
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
        if (face == orientation)
        {
            return true;
        }
        if (face == orientation.getOpposite())
        {
            return false;
        }

        Vec3 vec = Utils.fraction(hit.getLocation());

        double hor = Utils.isX(facing) ? vec.x() : vec.z();
        if (!Utils.isPositive(facing))
        {
            hor = 1D - hor;
        }
        if (!getBlockState().getValue(PropertyHolder.FRONT))
        {
            hor -= .5D;
        }

        double vert = Utils.isY(orientation) ? vec.y() : (Utils.isX(facing) ? vec.z() : vec.x());
        if (orientation == Direction.DOWN || (!Utils.isY(orientation) && !Utils.isPositive(orientation)))
        {
            vert = 1F - vert;
        }
        return (hor * 2D) < vert;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean front = getBlockState().getValue(PropertyHolder.FRONT);

        if (side == facing)
        {
            return front ? EMPTY_GETTER : this::getCamo;
        }
        else if (side == facing.getOpposite())
        {
            return front ? this::getCamoTwo : EMPTY_GETTER;
        }

        if ((!front && edge == facing) || (front && edge == facing.getOpposite()))
        {
            HorizontalRotation rot = getBlockState().getValue(PropertyHolder.ROTATION);
            Direction orientation = rot.withFacing(facing);
            Direction perpOrientation = rot.rotate(Rotation.CLOCKWISE_90).withFacing(facing);
            if (side == orientation || (side.getAxis() == perpOrientation.getAxis() && front))
            {
                return this::getCamoTwo;
            }
            else if (side == orientation.getOpposite() || (side.getAxis() == perpOrientation.getAxis()))
            {
                return this::getCamo;
            }
        }

        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean front = getBlockState().getValue(PropertyHolder.FRONT);

        if (!front && side == facing)
        {
            return SolidityCheck.FIRST;
        }
        else if (front && side == facing.getOpposite())
        {
            return SolidityCheck.SECOND;
        }
        return SolidityCheck.NONE;
    }

    @Override
    protected DoubleSoundMode calculateSoundMode()
    {
        return switch (getBlockState().getValue(PropertyHolder.ROTATION))
        {
            case UP -> DoubleSoundMode.SECOND;
            case DOWN -> DoubleSoundMode.FIRST;
            case LEFT, RIGHT -> DoubleSoundMode.EITHER;
        };
    }
}
