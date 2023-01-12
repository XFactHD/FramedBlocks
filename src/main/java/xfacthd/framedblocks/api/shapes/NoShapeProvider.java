package xfacthd.framedblocks.api.shapes;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.BiConsumer;

public final class NoShapeProvider implements ShapeProvider
{
    public static final NoShapeProvider INSTANCE = new NoShapeProvider();

    private NoShapeProvider() { }

    @Override
    public VoxelShape get(BlockState state)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() { return true; }

    @Override
    public void forEach(BiConsumer<BlockState, VoxelShape> consumer)
    {
        throw new UnsupportedOperationException();
    }
}
