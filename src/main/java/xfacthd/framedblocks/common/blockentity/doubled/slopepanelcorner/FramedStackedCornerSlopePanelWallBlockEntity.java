package xfacthd.framedblocks.common.blockentity.doubled.slopepanelcorner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedStackedCornerSlopePanelWallBlockEntity extends FramedDoubleBlockEntity
{
    public FramedStackedCornerSlopePanelWallBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_STACKED_CORNER_SLOPE_PANEL_WALL.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction side = hit.getDirection();
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = getBlockState().getValue(PropertyHolder.ROTATION);
        Direction rotDir = rot.withFacing(dir);
        Direction perpRotDir = rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);

        if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
        {
            return true;
        }

        Vec3 hitVec = hit.getLocation();
        if (side.getAxis() == dir.getAxis())
        {
            double xz1 = Utils.fractionInDir(hitVec, rotDir);
            double xz2 = Utils.fractionInDir(hitVec, perpRotDir);
            return xz1 < .5 || xz2 < .5;
        }

        Direction fracDir;
        if (Utils.isY(side))
        {
            fracDir = Utils.isY(rotDir) ? perpRotDir : rotDir;
        }
        else
        {
            fracDir = Utils.isY(rotDir) ? rotDir : perpRotDir;
        }
        return Utils.fractionInDir(hitVec, fracDir) < .5;
    }
}
