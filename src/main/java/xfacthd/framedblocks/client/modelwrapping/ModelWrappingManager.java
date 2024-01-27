package xfacthd.framedblocks.client.modelwrapping;

import com.google.common.base.Stopwatch;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModLoader;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.model.wrapping.*;
import xfacthd.framedblocks.api.util.TestProperties;
import xfacthd.framedblocks.api.util.Utils;

import java.util.*;

public final class ModelWrappingManager
{
    private static final Map<ResourceKey<Block>, ModelWrappingHandler> HANDLERS = new IdentityHashMap<>();
    private static boolean locked = true;

    public static void handleAll(Map<ResourceLocation, BakedModel> models, TextureLookup textureLookup)
    {
        Stopwatch stopwatch = Stopwatch.createStarted();

        ModelAccessor accessor = models::get;
        ModelCounter counter = new ModelCounter();

        for (Map.Entry<ResourceKey<Block>, ModelWrappingHandler> entry : HANDLERS.entrySet())
        {
            ResourceLocation blockId = entry.getKey().location();
            ModelWrappingHandler handler = entry.getValue();
            Block block = handler.getBlock();

            for (BlockState state : block.getStateDefinition().getPossibleStates())
            {
                ResourceLocation location = StateLocationCache.getLocationFromState(state, blockId);
                BakedModel baseModel = models.get(location);
                BakedModel replacement = handler.wrapBlockModel(baseModel, state, accessor, textureLookup, counter);
                models.put(location, replacement);
            }

            if (handler.handlesItemModel())
            {
                ModelResourceLocation itemId = new ModelResourceLocation(blockId, "inventory");
                BakedModel itemModel = handler.replaceItemModel(accessor, textureLookup, counter);
                models.put(itemId, itemModel);
            }
        }

        stopwatch.stop();
        FramedBlocks.LOGGER.debug(
                "Wrapped {} unique models ({} total) for {} blocks in {}",
                counter.getDistinctCount(), counter.getTotalCount(), HANDLERS.size(), stopwatch
        );
    }

    public static BakedModel handle(ResourceLocation id, BakedModel model, ModelAccessor modelAccessor, TextureLookup textureLookup)
    {
        if (id instanceof ModelResourceLocation modelId)
        {
            ResourceKey<Block> blockId = ResourceKey.create(
                    Registries.BLOCK,
                    new ResourceLocation(modelId.getNamespace(), modelId.getPath())
            );
            ModelWrappingHandler handler = HANDLERS.get(blockId);
            if (handler == null)
            {
                return model;
            }

            if (!modelId.getVariant().equals("inventory"))
            {
                Block block = BuiltInRegistries.BLOCK.get(blockId);
                BlockState state = StateLocationCache.getStateFromLocation(blockId.location(), block, modelId);
                return handler.wrapBlockModel(model, state, modelAccessor, textureLookup, null);
            }
            else if (handler.handlesItemModel())
            {
                return handler.replaceItemModel(modelAccessor, textureLookup, null);
            }
        }
        return model;
    }

    public static void fireRegistration()
    {
        Stopwatch stopwatch = Stopwatch.createStarted();

        locked = false;
        if (TestProperties.ENABLE_STATE_MERGER_DEBUG_LOGGING)
        {
            FramedBlocks.LOGGER.info("=============== Model Wrapper Registration Start ===============");
            FramedBlocks.LOGGER.info("\"%-70s | %-150s | %-150s\"".formatted(
                    "Block", "Unhandled properties", "Handled or ignored properties"
            ));
        }
        ModLoader.get().postEvent(new RegisterModelWrappersEvent());
        if (TestProperties.ENABLE_STATE_MERGER_DEBUG_LOGGING)
        {
            FramedBlocks.LOGGER.info("=============== Model Wrapper Registration End =================");
        }
        locked = true;

        stopwatch.stop();
        FramedBlocks.LOGGER.debug("Registered model wrappers for {} blocks in {}", HANDLERS.size(), stopwatch);
    }

    public static void register(Holder<Block> block, ModelWrappingHandler handler)
    {
        if (locked)
        {
            throw new IllegalStateException("ModelWrappingHandler registration is locked");
        }

        ModelWrappingHandler oldHandler = HANDLERS.put(Utils.getKeyOrThrow(block), handler);
        if (oldHandler != null)
        {
            throw new IllegalStateException("ModelWrappingHandler for '" + block + "' already registered");
        }
    }

    public static void reset()
    {
        HANDLERS.values().forEach(ModelWrappingHandler::reset);
    }

    public static ModelWrappingHandler getHandler(Block block)
    {
        ResourceKey<Block> blockId = BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow();
        ModelWrappingHandler handler = HANDLERS.get(blockId);
        if (handler == null)
        {
            throw new NullPointerException("No ModelWrappingHandler registered for block '" + blockId + "'");
        }
        return handler;
    }



    private ModelWrappingManager() { }
}
