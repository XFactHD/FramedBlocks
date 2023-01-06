package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedFlatDoubleSlopePanelCornerBlockEntity extends FramedDoubleBlockEntity
{
    public FramedFlatDoubleSlopePanelCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedFlatDoubleSlopePanelCorner.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction side = hit.getDirection();

        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing) { return false; }
        if (side == facing.getOpposite()) { return true; }

        HorizontalRotation rotation = getBlockState().getValue(PropertyHolder.ROTATION);
        Direction rotDir = rotation.withFacing(facing);
        Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);
        if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
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

        Direction perpDir = side == rotDir ? perpRotDir : rotDir;
        double vert = Utils.isY(perpDir) ? vec.y() : (Utils.isX(facing) ? vec.z() : vec.x());
        if (perpDir == Direction.DOWN || (!Utils.isY(perpDir) && !Utils.isPositive(perpDir)))
        {
            vert = 1F - vert;
        }
        return (hor * 2D) < vert;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        HorizontalRotation rotation = getBlockState().getValue(PropertyHolder.ROTATION);
        if (rotation == HorizontalRotation.UP || rotation == HorizontalRotation.RIGHT)
        {
            return DoubleSoundMode.FIRST;
        }
        return DoubleSoundMode.EITHER;
    }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing) { return getCamo(); }
        if (side == facing.getOpposite()) { return getCamoTwo(); }

        HorizontalRotation rotation = getBlockState().getValue(PropertyHolder.ROTATION);
        Direction rotDir = rotation.withFacing(facing);
        Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);
        if (side == rotDir || side == perpRotDir)
        {
            return getCamoTwo();
        }
        return getCamo();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean front = getBlockState().getValue(PropertyHolder.FRONT);
        if (side == facing && !front)
        {
            //noinspection ConstantConditions
            return getCamo().getState().isSolidRender(level, worldPosition);
        }
        if (side == facing.getOpposite() && front)
        {
            //noinspection ConstantConditions
            return getCamoTwo().getState().isSolidRender(level, worldPosition);
        }

        return false;
    }
}
