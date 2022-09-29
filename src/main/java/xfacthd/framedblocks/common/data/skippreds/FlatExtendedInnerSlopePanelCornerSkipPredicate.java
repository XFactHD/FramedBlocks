package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public final class FlatExtendedInnerSlopePanelCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        HorizontalRotation perpRot = rot.rotate(Rotation.COUNTERCLOCKWISE_90);
        Direction rotDir = rot.withFacing(dir);
        Direction perpRotDir = perpRot.withFacing(dir);

        if (side == dir || side == rotDir.getOpposite() || side == perpRotDir.getOpposite())
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedInnerSlopePanelCorner(
                        level, pos, dir, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        level, pos, dir, rot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        level, pos, dir, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(
                        level, pos, dir, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(
                        level, pos, dir, rot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedInnerDoubleSlopePanelCorner(
                        level, pos, dir, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir != dir) { return false; }

        if (side == rotDir && adjRot == perpRot)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (side == perpRotDir && adjRot == rot.rotate(Rotation.CLOCKWISE_90))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir == dir && adjRot == rot && (side == rotDir || side == perpRotDir))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir != dir) { return false; }

        if (side == rotDir && adjRot == perpRot)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (side == perpRotDir && adjRot == rot)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir != dir) { return false; }

        if (side == rotDir && adjRot == perpRot)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (side == perpRotDir && adjRot == rot)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir == dir && adjRot == rot && (side == rotDir || side == perpRotDir))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }

    private static boolean testAgainstFlatExtendedInnerDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir != dir) { return false; }

        if (side == rotDir && adjRot == perpRot)
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }
        else if (side == perpRotDir && adjRot == rot.rotate(Rotation.CLOCKWISE_90))
        {
            return SideSkipPredicate.compareState(level, pos, side, dir, dir);
        }

        return false;
    }
}
