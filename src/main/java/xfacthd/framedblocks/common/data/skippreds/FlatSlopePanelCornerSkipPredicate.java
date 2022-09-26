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

public final class FlatSlopePanelCornerSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean front = state.getValue(PropertyHolder.FRONT);

        if (side == dir)
        {
            return !front && SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        HorizontalRotation perpRot = rot.rotate(Rotation.COUNTERCLOCKWISE_90);
        Direction rotDir = rot.withFacing(dir);
        Direction perpRotDir = perpRot.withFacing(dir);

        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType type)
        {
            return switch (type)
            {
                case FRAMED_FLAT_SLOPE_PANEL_CORNER -> testAgainstFlatSlopePanelCorner(
                        level, pos, dir, front, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        level, pos, dir, front, rot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        level, pos, dir, front, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(
                        level, pos, dir, front, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(
                        level, pos, dir, front, rot, perpRot, rotDir, perpRotDir, adjState, side
                );
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(
                        level, pos, dir, front, rot, rotDir, perpRotDir, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstFlatSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir != dir || adjFront != front) { return false; }

        if (side == rotDir.getOpposite() && adjRot == perpRot)
        {
            return SideSkipPredicate.compareState(level, pos, side, rotDir.getOpposite(), rotDir);
        }
        else if (side == perpRotDir.getOpposite() && adjRot == rot.rotate(Rotation.CLOCKWISE_90))
        {
            return SideSkipPredicate.compareState(level, pos, side, perpRotDir.getOpposite(), perpRotDir);
        }

        return false;
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir == dir && adjFront == front && adjRot == rot && (side == rotDir.getOpposite() || side == perpRotDir.getOpposite()))
        {
            Direction camoSide = side == rotDir.getOpposite() ? rotDir.getOpposite() : perpRotDir.getOpposite();
            return SideSkipPredicate.compareState(level, pos, side, camoSide, camoSide.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir != dir || adjFront != front) { return false; }

        if (side == rotDir.getOpposite() && adjRot == perpRot)
        {
            return SideSkipPredicate.compareState(level, pos, side, rotDir.getOpposite(), adjRot.withFacing(adjDir));
        }
        else if (side == perpRotDir.getOpposite() && adjRot == rot)
        {
            return SideSkipPredicate.compareState(level, pos, side, perpRotDir.getOpposite(), adjRot.withFacing(adjDir));
        }

        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (side == rotDir.getOpposite())
        {
            if (adjDir == dir && adjFront == front && perpRot.isSameDir(dir, adjRot, adjDir))
            {
                Direction camoSide = adjRot.withFacing(adjDir).getOpposite();
                return SideSkipPredicate.compareState(level, pos, side, rotDir.getOpposite(), camoSide);
            }
            else if (adjDir == dir.getOpposite() && adjFront != front &&  perpRot.isSameDir(dir, adjRot.getOpposite(), adjDir))
            {
                Direction camoSide = adjRot.getOpposite().withFacing(adjDir).getOpposite();
                return SideSkipPredicate.compareState(level, pos, side, rotDir.getOpposite(), camoSide);
            }
        }
        else if (side == perpRotDir.getOpposite())
        {
            if (adjDir == dir && adjFront == front && rot.isSameDir(dir, adjRot, adjDir))
            {
                Direction camoSide = adjRot.withFacing(adjDir).getOpposite();
                return SideSkipPredicate.compareState(level, pos, side, perpRotDir.getOpposite(), camoSide);
            }
            else if (adjDir == dir.getOpposite() && adjFront != front && rot.isSameDir(dir, adjRot.getOpposite(), adjDir))
            {
                Direction camoSide = adjRot.getOpposite().withFacing(adjDir).getOpposite();
                return SideSkipPredicate.compareState(level, pos, side, perpRotDir.getOpposite(), camoSide);
            }
        }

        return false;
    }

    private static boolean testAgainstInverseDoubleSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot, HorizontalRotation perpRot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        if (!front) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (side == rotDir.getOpposite())
        {
            if (adjDir == dir && perpRot.isSameDir(dir, adjRot, adjDir))
            {
                Direction camoSide = adjRot.withFacing(adjDir).getOpposite();
                return SideSkipPredicate.compareState(level, pos, side, rotDir, camoSide);
            }
            else if (adjDir == dir.getOpposite() && perpRot.isSameDir(dir, adjRot.getOpposite(), adjDir))
            {
                Direction camoSide = adjRot.withFacing(adjDir);
                return SideSkipPredicate.compareState(level, pos, side, rotDir, camoSide);
            }
        }
        else if (side == perpRotDir.getOpposite())
        {
            if (adjDir == dir && rot.isSameDir(dir, adjRot, adjDir))
            {
                Direction camoSide = adjRot.withFacing(adjDir).getOpposite();
                return SideSkipPredicate.compareState(level, pos, side, rotDir, camoSide);
            }
            else if (adjDir == dir.getOpposite() && rot.isSameDir(dir, adjRot.getOpposite(), adjDir))
            {
                Direction camoSide = adjRot.withFacing(adjDir);
                return SideSkipPredicate.compareState(level, pos, side, rotDir, camoSide);
            }
        }

        return false;
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(
            BlockGetter level, BlockPos pos, Direction dir, boolean front, HorizontalRotation rot,
            Direction rotDir, Direction perpRotDir, BlockState adjState, Direction side
    )
    {
        if (front) { return false; }

        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (adjDir != dir.getOpposite()) { return false; }

        if (side == rotDir.getOpposite() && rot.rotate(Rotation.CLOCKWISE_90).isSameDir(dir, adjRot, adjDir))
        {
            return SideSkipPredicate.compareState(level, pos, side, perpRotDir, dir);
        }
        else if (side == perpRotDir.getOpposite() && rot.isSameDir(dir, adjRot.getOpposite(), adjDir))
        {
            return SideSkipPredicate.compareState(level, pos, side, rotDir, dir);
        }

        return false;
    }
}
