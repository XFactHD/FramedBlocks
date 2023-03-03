package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedStackedSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    private final boolean corner;
    private final boolean innerCorner;

    public FramedStackedSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedStackedSlopePanel.get(), pos, state);
        BlockType type = (BlockType) getBlockType();
        this.corner = type != BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER;
        this.innerCorner = type == BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER;
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction side = hit.getDirection();
        Vec3 vec = Utils.fraction(hit.getLocation());

        if (side == facing) { return false; }
        if (side == facing.getOpposite()) { return true; }

        return Utils.fractionInDir(vec, facing.getOpposite()) > .5F;
    }

    @Override
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.EITHER; }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing)
        {
            return getCamoState();
        }
        if (side == facing.getOpposite())
        {
            return getCamoStateTwo();
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing || side == facing.getOpposite())
        {
            //noinspection ConstantConditions
            return getCamoState(side).isSolidRender(level, worldPosition);
        }

        HorizontalRotation rot = getBlockState().getValue(PropertyHolder.ROTATION);
        HorizontalRotation rotCw = rot.rotate(Rotation.COUNTERCLOCKWISE_90);
        if ((rot.withFacing(facing) == side.getOpposite() && !corner) || rotCw.withFacing(facing) == side.getOpposite() && innerCorner)
        {
            //noinspection ConstantConditions
            return getCamoState().isSolidRender(level, worldPosition) && getCamoStateTwo().isSolidRender(level, worldPosition);
        }

        return false;
    }
}