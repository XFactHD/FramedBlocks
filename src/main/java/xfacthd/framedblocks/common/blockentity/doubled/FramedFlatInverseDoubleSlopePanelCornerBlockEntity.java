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
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedFlatInverseDoubleSlopePanelCornerBlockEntity extends FramedDoubleBlockEntity
{
    public FramedFlatInverseDoubleSlopePanelCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_FLAT_INVERSE_DOUBLE_SLOPE_PANEL_CORNER.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction side = hit.getDirection();

        if (side == facing)
        {
            return false;
        }
        if (side == facing.getOpposite())
        {
            return true;
        }

        Vec3 vec = Utils.fraction(hit.getLocation());
        double hor = Utils.isX(facing) ? vec.x() : vec.z();
        if (!Utils.isPositive(facing))
        {
            hor = 1D - hor;
        }
        return hor < .5D;
    }

    @Override
    protected DoubleBlockTopInteractionMode calculateTopInteractionMode()
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (edge == facing)
        {
            HorizontalRotation rotation = getBlockState().getValue(PropertyHolder.ROTATION);
            Direction rotDir = rotation.withFacing(facing);
            Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);

            if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
            {
                return this::getCamo;
            }
        }

        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        return SolidityCheck.NONE;
    }
}
