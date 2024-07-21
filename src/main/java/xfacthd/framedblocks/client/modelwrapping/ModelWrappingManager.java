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
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.config.DevToolsConfig;

import java.util.*;

public final class ModelWrappingManager
{
    private static final Map<ResourceKey<Block>, ModelWrappingHandler> HANDLERS = new IdentityHashMap<>();
    private static boolean locked = true;

    public static void handleAll(Map<ModelResourceLocation, BakedModel> models, TextureLookup textureLookup)
    {
        Stopwatch stopwatch = Stopwatch.createStarted();

        ModelLookup accessor = models::get;
        ModelCounter counter = new ModelCounter();

        for (Map.Entry<ResourceKey<Block>, ModelWrappingHandler> entry : HANDLERS.entrySet())
        {
            ResourceLocation blockId = entry.getKey().location();
            ModelWrappingHandler handler = entry.getValue();
            Block block = handler.getBlock();

            for (BlockState state : block.getStateDefinition().getPossibleStates())
            {
                ModelResourceLocation location = StateLocationCache.getLocationFromState(state, blockId);
                BakedModel baseModel = models.get(location);
                BakedModel replacement = handler.wrapBlockModel(baseModel, state, accessor, textureLookup, counter);
                models.put(location, replacement);
            }

            if (handler.handlesItemModel())
            {
                ModelResourceLocation itemId = ModelResourceLocation.inventory(blockId);
                BakedModel itemModel = handler.replaceItemModel(accessor, textureLookup, counter);
                models.put(itemId, itemModel);
            }
        }

        stopwatch.stop();
        FramedBlocks.LOGGER.debug(
                "Wrapped {} unique block models ({} total) and {} item models for {} blocks in {}",
                counter.getDistinctCount(), counter.getTotalCount(), counter.getItemCount(), HANDLERS.size(), stopwatch
        );
    }

    public static BakedModel handle(ModelResourceLocation id, BakedModel model, ModelLookup modelLookup, TextureLookup textureLookup)
    {
        ResourceKey<Block> blockId = ResourceKey.create(Registries.BLOCK, id.id());
        ModelWrappingHandler handler = HANDLERS.get(blockId);
        if (handler == null)
        {
            return model;
        }

        if (!id.getVariant().equals("inventory"))
        {
            Block block = BuiltInRegistries.BLOCK.get(blockId);
            BlockState state = StateLocationCache.getStateFromLocation(blockId.location(), block, id);
            return handler.wrapBlockModel(model, state, modelLookup, textureLookup, null);
        }
        else if (handler.handlesItemModel())
        {
            return handler.replaceItemModel(modelLookup, textureLookup, null);
        }
        return model;
    }

    public static void fireRegistration()
    {
        Stopwatch stopwatch = Stopwatch.createStarted();

        locked = false;
        boolean debugLogging = DevToolsConfig.VIEW.isStateMergerDebugLoggingEnabled();
        if (debugLogging)
        {
            FramedBlocks.LOGGER.info("=============== Model Wrapper Registration Start ===============");
            FramedBlocks.LOGGER.info("\"%-70s | %-150s | %-150s\"".formatted(
                    "Block", "Unhandled properties", "Handled or ignored properties"
            ));
        }
        ModLoader.postEvent(new RegisterModelWrappersEvent());
        if (debugLogging)
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
