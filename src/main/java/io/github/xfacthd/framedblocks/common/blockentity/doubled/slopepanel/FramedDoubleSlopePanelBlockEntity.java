package io.github.xfacthd.framedblocks.common.blockentity.doubled.slopepanel;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FramedDoubleSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DOUBLE_SLOPE_PANEL.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
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

        Vec3 vec = MathUtils.fraction(hit.getLocation());

        double hor = DirUtils.isX(facing) ? vec.x() : vec.z();
        if (!DirUtils.isPositive(facing))
        {
            hor = 1D - hor;
        }
        if (!getBlockState().getValue(PropertyHolder.FRONT))
        {
            hor -= .5D;
        }

        double vert = DirUtils.isY(orientation) ? vec.y() : (DirUtils.isX(facing) ? vec.z() : vec.x());
        if (orientation == Direction.DOWN || (!DirUtils.isY(orientation) && !DirUtils.isPositive(orientation)))
        {
            vert = 1F - vert;
        }
        return (hor * 2D) < vert;
    }
}
