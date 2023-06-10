package xfacthd.framedblocks.api.shapes;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.BiConsumer;

public final class SingleShapeProvider implements ShapeProvider
{
    private final List<BlockState> states;
    private final VoxelShape shape;

    public SingleShapeProvider(List<BlockState> states, VoxelShape shape)
    {
        this.states = states;
        this.shape = shape;
    }

    @Override
    public VoxelShape get(BlockState state)
    {
        return shape;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public void forEach(BiConsumer<BlockState, VoxelShape> consumer)
    {
        states.forEach(state -> consumer.accept(state, shape));
    }
}
