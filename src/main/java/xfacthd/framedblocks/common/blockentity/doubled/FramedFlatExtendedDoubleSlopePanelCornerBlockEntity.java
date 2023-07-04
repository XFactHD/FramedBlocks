package xfacthd.framedblocks.common.blockentity.doubled;

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
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedFlatExtendedDoubleSlopePanelCornerBlockEntity extends FramedDoubleBlockEntity
{
    private final boolean isInner;

    public FramedFlatExtendedDoubleSlopePanelCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_FLAT_EXTENDED_DOUBLE_SLOPE_PANEL_CORNER.get(), pos, state);
        this.isInner = getBlockType() == BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER;
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction side = hit.getDirection();

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing)
        {
            return false;
        }
        if (side == facing.getOpposite())
        {
            return true;
        }

        HorizontalRotation rotation = getBlockState().getValue(PropertyHolder.ROTATION);
        Direction rotDir = rotation.withFacing(facing);
        Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);

        if (isInner && (side == rotDir.getOpposite() || side == perpRotDir.getOpposite()))
        {
            return false;
        }

        Vec3 hitVec = hit.getLocation();
        double hor = Utils.fractionInDir(hitVec, facing.getOpposite());
        if (!isInner && (side == rotDir || side == perpRotDir))
        {
            return hor > .5;
        }

        Direction perpDir;
        if (isInner)
        {
            perpDir = side == rotDir ? perpRotDir.getOpposite() : rotDir.getOpposite();
        }
        else
        {
            perpDir = side == rotDir.getOpposite() ? perpRotDir.getOpposite() : rotDir.getOpposite();
        }

        double perpHor = Utils.fractionInDir(hitVec, perpDir);
        return ((hor - .5) * 2D) > perpHor;
    }

    @Override
    protected DoubleBlockTopInteractionMode calculateTopInteractionMode()
    {
        if (isInner)
        {
            HorizontalRotation rotation = getBlockState().getValue(PropertyHolder.ROTATION);
            if (rotation == HorizontalRotation.UP || rotation == HorizontalRotation.RIGHT)
            {
                return DoubleBlockTopInteractionMode.FIRST;
            }
        }
        return DoubleBlockTopInteractionMode.EITHER;
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

        HorizontalRotation rotation = getBlockState().getValue(PropertyHolder.ROTATION);
        Direction rotDir = rotation.withFacing(facing);
        Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);
        if (isInner && (side == rotDir.getOpposite() || side == perpRotDir.getOpposite()))
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

        if (isInner)
        {
            HorizontalRotation rotation = getBlockState().getValue(PropertyHolder.ROTATION);
            Direction rotDir = rotation.withFacing(facing);
            Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);
            if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
            {
                return SolidityCheck.FIRST;
            }
        }
        return SolidityCheck.BOTH;
    }
}
