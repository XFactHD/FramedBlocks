package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDoubleSlopePanelBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleSlopePanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedDoubleSlopePanel.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction face = hit.getDirection();
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (face == facing) { return false; }
        if (face == facing.getOpposite()) { return true; }

        Direction orientation = getBlockState().getValue(PropertyHolder.ROTATION).withFacing(facing);
        if (face == orientation) { return true; }
        if (face == orientation.getOpposite()) { return false; }

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
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == facing) { return getCamoState(); }
        if (side == facing.getOpposite()) { return getCamoStateTwo(); }

        Direction orientation = getBlockState().getValue(PropertyHolder.ROTATION).withFacing(facing);
        if (side == orientation) { return getCamoStateTwo(); }
        if (side == orientation.getOpposite()) { return getCamoState(); }

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean front = getBlockState().getValue(PropertyHolder.FRONT);

        if ((!front && side == facing) || (front && side == facing.getOpposite()))
        {
            BlockState camo = front ? getCamoStateTwo() : getCamoState();
            //noinspection ConstantConditions
            return camo.isSolidRender(level, worldPosition);
        }

        return false;
    }

    @Override
    public DoubleSoundMode getSoundMode() { return DoubleSoundMode.EITHER; }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        return getBlockPair(
                state.getValue(FramedProperties.FACING_HOR),
                state.getValue(PropertyHolder.ROTATION),
                state.getValue(PropertyHolder.FRONT)
        );
    }

    public static Tuple<BlockState, BlockState> getBlockPair(Direction facing, HorizontalRotation rotation, boolean front)
    {
        BlockState defState = FBContent.blockFramedSlopePanel.get().defaultBlockState();
        return new Tuple<>(
                defState.setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.ROTATION, rotation)
                        .setValue(PropertyHolder.FRONT, front),
                defState.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.ROTATION, rotation.isVertical() ? rotation.getOpposite() : rotation)
                        .setValue(PropertyHolder.FRONT, !front)
        );
    }
}
