package xfacthd.framedblocks.common.data.shapes.slopepanelcorner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public final class DoubleCornerSlopePanelWallShapes
{
    public static ShapeProvider generateSmall(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            CommonShapes.DirBoolKey key = switch (rot)
            {
                case UP ->    new CommonShapes.DirBoolKey(dir.getCounterClockWise(), true);
                case DOWN ->  new CommonShapes.DirBoolKey(dir.getClockWise(), false);
                case RIGHT -> new CommonShapes.DirBoolKey(dir.getClockWise(), true);
                case LEFT ->  new CommonShapes.DirBoolKey(dir.getCounterClockWise(), false);
            };
            builder.put(state, CommonShapes.SLAB_EDGE.get(key));
        }

        return ShapeProvider.of(builder.build());
    }

    public static ShapeProvider generateLarge(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape[] shapes = new VoxelShape[4 * 4];
        for (HorizontalRotation rot : HorizontalRotation.values())
        {
            VoxelShape preShape = Shapes.joinUnoptimized(rot.getCornerShape(), Shapes.block(), BooleanOp.NOT_SAME);
            ShapeUtils.makeHorizontalRotations(preShape, Direction.NORTH, shapes, rot.ordinal() << 2);
        }

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            int idx = dir.get2DDataValue() | (rot.ordinal() << 2);
            builder.put(state, shapes[idx]);
        }

        return ShapeProvider.of(builder.build());
    }
}
