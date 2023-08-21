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

public class FramedExtendedDoubleCornerSlopePanelWallBlockEntity extends FramedDoubleBlockEntity
{
    public FramedExtendedDoubleCornerSlopePanelWallBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_EXTENDED_DOUBLE_CORNER_SLOPE_PANEL_WALL.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction side = hit.getDirection();
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == dir)
        {
            return false;
        }

        HorizontalRotation rot = getBlockState().getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(dir);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
        {
            return true;
        }

        Vec3 hitVec = hit.getLocation();
        if (side == dir.getOpposite())
        {
            double xz1 = Utils.fractionInDir(hitVec, rotDir);
            double xz2 = Utils.fractionInDir(hitVec, perpRotDir);
            return xz1 < .5 || xz2 < .5;
        }

        double xzDir = Utils.fractionInDir(hitVec, dir.getOpposite());
        double xzPerp;
        if (Utils.isY(side))
        {
            xzPerp = Utils.fractionInDir(hitVec, Utils.isY(rotDir) ? perpRotDir : rotDir);
        }
        else
        {
            xzPerp = Utils.fractionInDir(hitVec, Utils.isY(rotDir) ? rotDir : perpRotDir);
        }

        if (xzPerp > .5)
        {
            return false;
        }
        return xzDir > (xzPerp * 2D);
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == dir)
        {
            return this::getCamo;
        }

        HorizontalRotation rot = getBlockState().getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(dir);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
        {
            return this::getCamoTwo;
        }
        else if (side == dir.getOpposite() && (edge == rotDir.getOpposite() || edge == perpRotDir.getOpposite()))
        {
            return this::getCamoTwo;
        }
        else if (side == rotDir)
        {
            if (edge == dir || edge == perpRotDir)
            {
                return this::getCamo;
            }
            else if (edge == perpRotDir.getOpposite())
            {
                return this::getCamoTwo;
            }
        }
        else if (side == perpRotDir)
        {
            if (edge == dir || edge == rotDir)
            {
                return this::getCamo;
            }
            else if (edge == rotDir.getOpposite())
            {
                return this::getCamoTwo;
            }
        }
        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == dir)
        {
            return SolidityCheck.FIRST;
        }

        HorizontalRotation rot = getBlockState().getValue(PropertyHolder.ROTATION);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
        if (side == dir.getOpposite() || side == rot.withFacing(dir) || side == perpRotDir)
        {
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.SECOND;
    }
}
