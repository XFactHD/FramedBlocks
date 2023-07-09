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

public class FramedInverseDoubleSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    public FramedInverseDoubleSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_INVERSE_DOUBLE_SLOPE_PANEL.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction side = hit.getDirection();
        Vec3 vec = Utils.fraction(hit.getLocation());

        if (side == facing)
        {
            return false;
        }
        if (side == facing.getOpposite())
        {
            return true;
        }

        boolean second;
        if (Utils.isZ(facing))
        {
            second = vec.z() > .5F;
        }
        else
        {
            second = vec.x() <= .5F;
        }

        if (Utils.isPositive(facing) == Utils.isZ(facing))
        {
            second = !second;
        }

        return second;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction rotation = getBlockState().getValue(PropertyHolder.ROTATION).withFacing(facing);

        if (side == rotation && edge == facing)
        {
            return this::getCamo;
        }
        else if (side == rotation.getOpposite() && edge == facing.getOpposite())
        {
            return this::getCamoTwo;
        }

        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        return SolidityCheck.NONE;
    }
}
