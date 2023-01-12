package xfacthd.framedblocks.api.shapes;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface ShapeGenerator
{
    ShapeGenerator EMPTY = states -> NoShapeProvider.INSTANCE;

    ShapeProvider generate(ImmutableList<BlockState> states);

    static ShapeGenerator singleShape(VoxelShape shape)
    {
        return states -> new SingleShapeProvider(states, shape);
    }
}