package xfacthd.framedblocks.api.shapes;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.function.BiConsumer;

public interface ShapeProvider
{
    VoxelShape get(BlockState state);

    boolean isEmpty();

    void forEach(BiConsumer<BlockState, VoxelShape> consumer);

    static ShapeProvider of(Map<BlockState, VoxelShape> shapes)
    {
        return new MapBackedShapeProvider(shapes);
    }
}
