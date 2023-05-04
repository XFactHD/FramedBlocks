package xfacthd.framedblocks.api.shapes;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public final class ReloadableShapeProvider implements ShapeProvider
{
    private static final List<ReloadableShapeProvider> PROVIDERS = new ArrayList<>();

    private final ShapeGenerator generator;
    private final ImmutableList<BlockState> states;
    private ShapeProvider wrapped;

    public ReloadableShapeProvider(ShapeGenerator generator, ImmutableList<BlockState> states)
    {
        Preconditions.checkState(!FMLEnvironment.production, "Reloading shapes is not supported in production");
        this.generator = generator;
        this.states = states;
        this.wrapped = generator.generate(states);
        PROVIDERS.add(this);
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

    private void reload()
    {
        wrapped = generator.generate(states);
    }



    @ApiStatus.Internal
    public static final class Reloader implements ResourceManagerReloadListener
    {
        private static final Logger LOGGER = LogUtils.getLogger();
        public static final Reloader INSTANCE = new Reloader();

        private Reloader() { }

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager)
        {
            PROVIDERS.forEach(ReloadableShapeProvider::reload);
            LOGGER.info("{} reloadable shape providers reloaded", PROVIDERS.size());
        }
    }
}
