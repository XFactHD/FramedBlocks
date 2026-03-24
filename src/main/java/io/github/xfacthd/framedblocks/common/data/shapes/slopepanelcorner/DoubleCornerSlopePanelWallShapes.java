package io.github.xfacthd.framedblocks.common.data.shapes.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.CommonShapes;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class DoubleCornerSlopePanelWallShapes
{
    public static ShapeContainer generateSmall(List<BlockState> states)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

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
            map.put(state, CommonShapes.SLAB_EDGE.get(key));
        }

        return ShapeContainer.of(map);
    }

    public static ShapeContainer generateLarge(List<BlockState> states)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

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
            map.put(state, shapes[idx]);
        }

        return ShapeContainer.of(map);
    }

    private DoubleCornerSlopePanelWallShapes() { }
}
