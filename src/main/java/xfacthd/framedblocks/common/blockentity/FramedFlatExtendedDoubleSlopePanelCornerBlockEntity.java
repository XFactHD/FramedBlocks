package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedFlatExtendedDoubleSlopePanelCornerBlockEntity extends FramedDoubleBlockEntity
{
    private final boolean isInner;

    public FramedFlatExtendedDoubleSlopePanelCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedFlatExtendedDoubleSlopePanelCorner.get(), pos, state);
        this.isInner = getBlockType() == BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER;
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

        if (isInner && (side == rotDir.getOpposite() || side == perpRotDir.getOpposite()))
        {
            return false;
        }

        Vec3 vec = Utils.fraction(hit.getLocation());

        double hor = Utils.isX(facing) ? vec.x() : vec.z();
        if (!Utils.isPositive(facing))
        {
            hor = 1D - hor;
        }

        Direction perpDir;
        if (isInner)
        {
            perpDir = side == rotDir ? perpRotDir : rotDir;
        }
        else
        {
            perpDir = side == rotDir.getOpposite() ? perpRotDir.getOpposite() : rotDir.getOpposite();
        }

        double perpHor = Utils.isY(perpDir) ? vec.y() : (Utils.isX(facing) ? vec.z() : vec.x());
        if ((perpDir == Direction.DOWN || (!Utils.isY(perpDir) && !Utils.isPositive(perpDir))) == isInner)
        {
            perpHor = 1F - perpHor;
        }
        return (hor * 2D) < perpHor;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        if (isInner)
        {
            HorizontalRotation rotation = getBlockState().getValue(PropertyHolder.ROTATION);
            if (rotation == HorizontalRotation.UP || rotation == HorizontalRotation.RIGHT)
            {
                return DoubleSoundMode.FIRST;
            }
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

        if (side == facing)
        {
            //noinspection ConstantConditions
            return getCamo().getState().isSolidRender(level, worldPosition);
        }
        if (side == facing.getOpposite())
        {
            //noinspection ConstantConditions
            return getCamoTwo().getState().isSolidRender(level, worldPosition);
        }

        if (isInner)
        {
            HorizontalRotation rotation = getBlockState().getValue(PropertyHolder.ROTATION);
            Direction rotDir = rotation.withFacing(facing);
            Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(facing);
            if (side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
            {
                //noinspection ConstantConditions
                return getCamo().getState().isSolidRender(level, worldPosition);
            }
        }

        return false;
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        if (((IFramedBlock) state.getBlock()).getBlockType() == BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER)
        {
            return getInnerBlockPair(state.getValue(FramedProperties.FACING_HOR), state.getValue(PropertyHolder.ROTATION));
        }
        else
        {
            return getBlockPair(state.getValue(FramedProperties.FACING_HOR), state.getValue(PropertyHolder.ROTATION));
        }
    }

    public static Tuple<BlockState, BlockState> getBlockPair(Direction facing, HorizontalRotation rotation)
    {
        HorizontalRotation backRot = rotation.rotate(rotation.isVertical() ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90);
        return new Tuple<>(
                FBContent.blockFramedFlatExtendedSlopePanelCorner.get()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.ROTATION, rotation),
                FBContent.blockFramedFlatInnerSlopePanelCorner.get()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.ROTATION, backRot)
        );
    }

    public static Tuple<BlockState, BlockState> getInnerBlockPair(Direction facing, HorizontalRotation rotation)
    {
        HorizontalRotation backRot = rotation.rotate(rotation.isVertical() ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90);
        return new Tuple<>(
                FBContent.blockFramedFlatExtendedInnerSlopePanelCorner.get()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.ROTATION, rotation),
                FBContent.blockFramedFlatSlopePanelCorner.get()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing.getOpposite())
                        .setValue(PropertyHolder.ROTATION, backRot)
        );
    }
}
