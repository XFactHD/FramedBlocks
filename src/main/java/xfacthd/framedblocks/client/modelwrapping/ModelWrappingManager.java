package xfacthd.framedblocks.client.modelwrapping;

import com.google.common.base.Stopwatch;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.model.wrapping.ModelAccessor;
import xfacthd.framedblocks.api.model.wrapping.RegisterModelWrappersEvent;

import java.util.*;

@SuppressWarnings("deprecation")
public final class ModelWrappingManager
{
    private static final Map<ResourceLocation, ModelWrappingHandler> HANDLERS = new IdentityHashMap<>();
    private static boolean locked = true;

    public static void handleAll(Map<ResourceLocation, BakedModel> models)
    {
        Stopwatch stopwatch = Stopwatch.createStarted();

        ModelAccessor accessor = models::get;
        ModelCounter counter = new ModelCounter();

        for (Map.Entry<ResourceLocation, ModelWrappingHandler> entry : HANDLERS.entrySet())
        {
            ResourceLocation blockId = entry.getKey();
            ModelWrappingHandler handler = entry.getValue();
            Block block = handler.getBlock();

            for (BlockState state : block.getStateDefinition().getPossibleStates())
            {
                ResourceLocation location = StateLocationCache.getLocationFromState(state, blockId);
                BakedModel baseModel = models.get(location);
                BakedModel replacement = handler.wrapBlockModel(baseModel, state, accessor, counter);
                models.put(location, replacement);
            }

            if (handler.handlesItemModel())
            {
                ModelResourceLocation itemId = new ModelResourceLocation(blockId, "inventory");
                BakedModel itemModel = handler.replaceItemModel(accessor, counter);
                models.put(itemId, itemModel);
            }
        }

        stopwatch.stop();
        FramedBlocks.LOGGER.debug(
                "Wrapped {} unique models ({} total) for {} blocks in {}",
                counter.getDistinctCount(), counter.getTotalCount(), HANDLERS.size(), stopwatch
        );
    }

    @SuppressWarnings("unused") // To be used in case on-demand model baking ever becomes a thing
    public static BakedModel handle(ResourceLocation id, BakedModel model, ModelAccessor modelAccessor)
    {
        if (id instanceof ModelResourceLocation modelId)
        {
            ResourceLocation blockId = new ResourceLocation(modelId.getNamespace(), modelId.getPath());
            ModelWrappingHandler handler = HANDLERS.get(blockId);
            if (handler == null)
            {
                return model;
            }

            if (!modelId.getVariant().equals("inventory"))
            {
                Block block = BuiltInRegistries.BLOCK.get(blockId);
                BlockState state = StateLocationCache.getStateFromLocation(blockId, block, modelId);
                return handler.wrapBlockModel(model, state, modelAccessor, null);
            }
            else if (handler.handlesItemModel())
            {
                return handler.replaceItemModel(modelAccessor, null);
            }
        }
        return model;
    }

    public static void fireRegistration()
    {
        Stopwatch stopwatch = Stopwatch.createStarted();

        locked = false;
        ModLoader.get().postEvent(new RegisterModelWrappersEvent());
        locked = true;

        stopwatch.stop();
        FramedBlocks.LOGGER.debug("Registered model wrappers for {} blocks in {}", HANDLERS.size(), stopwatch);
    }

    public static void register(RegistryObject<Block> block, ModelWrappingHandler handler)
    {
        if (locked)
        {
            throw new IllegalStateException("ModelWrappingHandler registration is locked");
        }

        ModelWrappingHandler oldHandler = HANDLERS.put(block.getId(), handler);
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
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        ModelWrappingHandler handler = HANDLERS.get(blockId);
        if (handler == null)
        {
            throw new NullPointerException("No ModelWrappingHandler registered for block '" + blockId + "'");
        }
        return handler;
    }



    private ModelWrappingManager() { }
}
