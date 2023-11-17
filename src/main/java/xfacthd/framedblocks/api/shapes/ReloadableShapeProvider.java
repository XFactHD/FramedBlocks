package xfacthd.framedblocks.api.shapes;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.ApiStatus;
import xfacthd.framedblocks.api.internal.InternalAPI;

import java.util.function.BiConsumer;

public final class ReloadableShapeProvider implements ShapeProvider
{
    private final ShapeGenerator generator;
    private final ImmutableList<BlockState> states;
    private ShapeProvider wrapped;

    public ReloadableShapeProvider(ShapeGenerator generator, ImmutableList<BlockState> states)
    {
        Preconditions.checkState(!FMLEnvironment.production, "Reloading shapes is not supported in production");
        this.generator = generator;
        this.states = states;
        this.wrapped = generator.generate(states);
        InternalAPI.INSTANCE.registerReloadableShapeProvider(this);
    }

    @Override
    public VoxelShape get(BlockState state)
    {
        return wrapped.get(state);
    }

    @Override
    public boolean isEmpty()
    {
        return wrapped.isEmpty();
    }

    @Override
    public void forEach(BiConsumer<BlockState, VoxelShape> consumer)
    {
        wrapped.forEach(consumer);
    }

    @ApiStatus.Internal
    public void reload()
    {
        wrapped = generator.generate(states);
    }
}
