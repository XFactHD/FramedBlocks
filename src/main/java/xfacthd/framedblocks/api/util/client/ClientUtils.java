package xfacthd.framedblocks.api.util.client;

import com.google.common.base.Suppliers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;

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
                                     BlockState itemModelSource,
                                     @Nullable List<Property<?>> ignoredProps)
    {
        Map<BlockState, BakedModel> visitedStates = new HashMap<>();

        for (BlockState state : block.get().getStateDefinition().getPossibleStates())
        {
            ResourceLocation location = BlockModelShaper.stateToModelLocation(state);
            BakedModel baseModel = models.get(location);
            BakedModel replacement = visitedStates.computeIfAbsent(
                    ignoreProps(state, ignoredProps),
                    key -> blockModelGen.apply(key, baseModel)
            );
            models.put(location, replacement);
        }

        if (itemModelSource != null)
        {
            //noinspection ConstantConditions
            ResourceLocation location = new ModelResourceLocation(block.get().getRegistryName(), "inventory");
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



    private ClientUtils() { }
}