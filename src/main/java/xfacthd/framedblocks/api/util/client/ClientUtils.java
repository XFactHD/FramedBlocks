package xfacthd.framedblocks.api.util.client;

import com.google.common.base.Suppliers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.RenderProperties;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.util.FramedConstants;

import java.util.*;
import java.util.function.*;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, value = Dist.CLIENT)
public final class ClientUtils
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Replace the {@link BakedModel}s for all {@link BlockState}s of the given {@link Block} via the given block model
     * factory
     * @param block The block whose models are to be replaced
     * @param models The location->model map given by the {@link net.minecraftforge.client.event.ModelBakeEvent}
     * @param blockModelGen The block model factory
     * @param ignoredProps The list of {@link Property}s to ignore, allows for deduplication of models when certain
     *                     properties don't influence the model (i.e. waterlogging).
     */
    public static void replaceModels(RegistryObject<Block> block, Map<ResourceLocation, BakedModel> models,
                                     BiFunction<BlockState, BakedModel, BakedModel> blockModelGen,
                                     @Nullable List<Property<?>> ignoredProps)
    {
        replaceModels(block, models, blockModelGen, null, ignoredProps);
    }

    /**
     * Replace the {@link BakedModel}s for all {@link BlockState}s of the given {@link Block} via the given block model
     * factory
     * @param block The block whose models are to be replaced
     * @param models The location->model map given by the {@link net.minecraftforge.client.event.ModelBakeEvent}
     * @param blockModelGen The block model factory
     * @param itemModelSource The {@link BlockState} whose model should be used as the item model
     * @param ignoredProps The list of {@link Property}s to ignore, allows for deduplication of models when certain
     *                     properties don't influence the model (i.e. waterlogging).
     */
    public static void replaceModels(RegistryObject<Block> block, Map<ResourceLocation, BakedModel> models,
                                     BiFunction<BlockState, BakedModel, BakedModel> blockModelGen,
                                     @Nullable BlockState itemModelSource,
                                     @Nullable List<Property<?>> ignoredProps)
    {
        replaceModelsSpecial(block, models, blockModelGen, itemModelSource, testState -> ignoreProps(testState, ignoredProps));
    }

    /**
     * Replace the {@link BakedModel}s for all {@link BlockState}s of the given {@link Block} via the given block model
     * factory
     * @param block The block whose models are to be replaced
     * @param models The location->model map given by the {@link net.minecraftforge.client.event.ModelBakeEvent}
     * @param blockModelGen The block model factory
     * @param stateMerger Custom BlockState merging function, allows for fine-grained deduplication of models when certain
     *                    properties or specific value ranges of a property don't influence the model (i.e. redstone power
     *                    of weighted pressure plates).
     */
    public static void replaceModelsSpecial(RegistryObject<Block> block, Map<ResourceLocation, BakedModel> models,
                                     BiFunction<BlockState, BakedModel, BakedModel> blockModelGen,
                                     Function<BlockState, BlockState> stateMerger)
    {
        replaceModelsSpecial(block, models, blockModelGen, null, stateMerger);
    }

    /**
     * Replace the {@link BakedModel}s for all {@link BlockState}s of the given {@link Block} via the given block model
     * factory
     * @param block The block whose models are to be replaced
     * @param models The location->model map given by the {@link net.minecraftforge.client.event.ModelBakeEvent}
     * @param blockModelGen The block model factory
     * @param itemModelSource The {@link BlockState} whose model should be used as the item model
     * @param stateMerger Custom BlockState merging function, allows for fine-grained deduplication of models when certain
     *                    properties or specific value ranges of a property don't influence the model (i.e. redstone power
     *                    of weighted pressure plates).
     */
    public static void replaceModelsSpecial(RegistryObject<Block> block, Map<ResourceLocation, BakedModel> models,
                                     BiFunction<BlockState, BakedModel, BakedModel> blockModelGen,
                                     @Nullable BlockState itemModelSource,
                                     Function<BlockState, BlockState> stateMerger)
    {
        Map<BlockState, BakedModel> visitedStates = new HashMap<>();

        for (BlockState state : block.get().getStateDefinition().getPossibleStates())
        {
            ResourceLocation location = BlockModelShaper.stateToModelLocation(state);
            BakedModel baseModel = models.get(location);
            BakedModel replacement = visitedStates.computeIfAbsent(
                    stateMerger.apply(state),
                    key -> blockModelGen.apply(key, baseModel)
            );
            models.put(location, replacement);
        }

        if (itemModelSource != null)
        {
            ResourceLocation location = new ModelResourceLocation(block.getId(), "inventory");
            BakedModel replacement = models.get(BlockModelShaper.stateToModelLocation(itemModelSource));
            models.put(location, replacement);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static BlockState ignoreProps(BlockState state, @Nullable List<Property<?>> ignoredProps)
    {
        if (ignoredProps == null || ignoredProps.isEmpty()) { return state; }

        BlockState defaultState = state.getBlock().defaultBlockState();
        for (Property prop : ignoredProps)
        {
            if (!state.hasProperty(prop))
            {
                LOGGER.warn("Found invalid ignored property {} for block {}!", prop, state.getBlock());
                continue;
            }
            state = state.setValue(prop, defaultState.getValue(prop));
        }

        return state;
    }

    public static BlockEntity getBlockEntitySafe(BlockGetter blockGetter, BlockPos pos)
    {
        if (blockGetter instanceof RenderChunkRegion renderChunk)
        {
            return renderChunk.getBlockEntity(pos);
        }
        return null;
    }

    public static final Supplier<Boolean> OPTIFINE_LOADED = Suppliers.memoize(() ->
    {
        try
        {
            Class.forName("net.optifine.Config");
            return true;
        }
        catch (ClassNotFoundException ignored)
        {
            return false;
        }
    });

    public static void enqueueClientTask(Runnable task) { Minecraft.getInstance().tell(task); }

    public static int getBlockColor(BlockAndTintGetter level, BlockPos pos, BlockState state, int tintIdx)
    {
        return Minecraft.getInstance().getBlockColors().getColor(state, level, pos, tintIdx);
    }

    public static int getFluidColor(BlockAndTintGetter level, BlockPos pos, FluidState fluid)
    {
        return RenderProperties.get(fluid).getColorTint(fluid, level, pos);
    }

    private static final List<ClientTask> tasks = new ArrayList<>();

    public static void enqueueClientTask(long delay, Runnable task)
    {
        //noinspection ConstantConditions
        long time = Minecraft.getInstance().level.getGameTime() + delay;
        tasks.add(new ClientTask(time, task));
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || tasks.isEmpty()) { return; }

        Iterator<ClientTask> it = tasks.iterator();
        while (it.hasNext())
        {
            ClientTask task = it.next();
            //noinspection ConstantConditions
            if (Minecraft.getInstance().level.getGameTime() >= task.time)
            {
                task.task.run();
                it.remove();
            }
        }
    }

    private record ClientTask(long time, Runnable task) { }



    private ClientUtils() { }
}