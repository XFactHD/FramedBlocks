package xfacthd.framedblocks.api.util.client;

import com.google.common.base.Preconditions;
import com.google.common.cache.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;

import java.time.Duration;

public final class ModelCache
{
    public static final Duration DEFAULT_CACHE_DURATION = Duration.ofMinutes(10);
    private static final LoadingCache<BlockState, BakedModel> modelCache = CacheBuilder.newBuilder()
            .expireAfterAccess(DEFAULT_CACHE_DURATION)
            .build(new ModelCacheLoader());
    private static ModelBakery modelBakery = null;

    public static void clear(ModelBakery bakery)
    {
        modelCache.invalidateAll();
        modelBakery = bakery;
    }

    public static BakedModel getModel(BlockState state) { return modelCache.getUnchecked(state); }

    public static ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data)
    {
        if (state.getBlock() instanceof LiquidBlock)
        {
            return ChunkRenderTypeSet.of(ItemBlockRenderTypes.getRenderLayer(state.getFluidState()));
        }
        return modelCache.getUnchecked(state).getRenderTypes(state, random, data);
    }

    public static ChunkRenderTypeSet getCamoRenderTypes(BlockState state, RandomSource random, ModelData data)
    {
        if (state.getBlock() instanceof LiquidBlock)
        {
            return ChunkRenderTypeSet.of(ItemBlockRenderTypes.getRenderLayer(state.getFluidState()));
        }
        BakedModel model = modelCache.getUnchecked(state);
        data = ModelUtils.getCamoModelData(model, state, data);
        return model.getRenderTypes(state, random, data);
    }

    public static ModelBakery getModelBakery()
    {
        Preconditions.checkNotNull(modelBakery, "ModelBakery requested before first resource reload");
        return modelBakery;
    }



    private static class ModelCacheLoader extends CacheLoader<BlockState, BakedModel>
    {
        @Override
        public BakedModel load(BlockState key)
        {
            if (key.getBlock() instanceof LiquidBlock fluid)
            {
                return FramedBlocksClientAPI.getInstance().createFluidModel(fluid.getFluid());
            }
            return Minecraft.getInstance().getBlockRenderer().getBlockModel(key);
        }
    }

    private ModelCache() { }
}
