package xfacthd.framedblocks.common.data.shapes.slab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class CheckeredPanelSegmentShapes
{
    public static ShapeProvider generate(ImmutableList<BlockState> states)
    {
        VoxelShape shapeFirst = ShapeUtils.orUnoptimized(
                Block.box(0, 0, 0,  8,  8,  8),
                Block.box(8, 8, 0, 16, 16,  8)
        );
        VoxelShape shapeSecond = ShapeUtils.orUnoptimized(
                Block.box(0, 8, 0,  8, 16,  8),
                Block.box(8, 0, 0, 16,  8,  8)
        );

        VoxelShape[] shapes = new VoxelShape[8];
        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            int idx = dir.get2DDataValue();
            boolean x = Utils.isX(dir);
            shapes[idx] = ShapeUtils.rotateShape(Direction.NORTH, dir, x ? shapeSecond : shapeFirst);
            shapes[idx + 4] = ShapeUtils.rotateShape(Direction.NORTH, dir, x ? shapeFirst : shapeSecond);
        }

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean second = state.getValue(PropertyHolder.SECOND);
            int idx = dir.get2DDataValue() + (second ? 4 : 0);
            builder.put(state, shapes[idx]);
        }
        return ShapeProvider.of(builder.build());
    }



    private CheckeredPanelSegmentShapes() { }
}
