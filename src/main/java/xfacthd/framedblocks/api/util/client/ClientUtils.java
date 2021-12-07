package xfacthd.framedblocks.api.util.client;

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
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.fmllegacy.RegistryObject;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ClientUtils
{
    public static void replaceModels(RegistryObject<Block> block, Map<ResourceLocation, BakedModel> models,
                                     BiFunction<BlockState, BakedModel, BakedModel> blockModelGen)
    {
        replaceModels(block, models, blockModelGen, model -> model);
    }

    public static void replaceModels(RegistryObject<Block> block, Map<ResourceLocation, BakedModel> models,
                                     BiFunction<BlockState, BakedModel, BakedModel> blockModelGen,
                                     Function<BakedModel, BakedModel> itemModelGen)
    {
        for (BlockState state : block.get().getStateDefinition().getPossibleStates())
        {
            ResourceLocation location = BlockModelShaper.stateToModelLocation(state);
            BakedModel baseModel = models.get(location);
            BakedModel replacement = blockModelGen.apply(state, baseModel);
            models.put(location, replacement);
        }

        //noinspection ConstantConditions
        ResourceLocation location = new ModelResourceLocation(block.get().getRegistryName(), "inventory");
        BakedModel replacement = itemModelGen.apply(models.get(location));
        models.put(location, replacement);
    }

    public static BlockEntity getBlockEntitySafe(BlockGetter blockGetter, BlockPos pos)
    {
        if (blockGetter instanceof RenderChunkRegion renderChunk)
        {
            return renderChunk.getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
        }
        return null;
    }
}