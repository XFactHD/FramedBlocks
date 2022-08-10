package xfacthd.framedblocks.client.model;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.model.BakedModelProxy;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.FramedBlockData;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.ModelCache;
import xfacthd.framedblocks.api.util.client.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedFlowerPotBlock;
import xfacthd.framedblocks.common.blockentity.FramedFlowerPotBlockEntity;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;
import xfacthd.framedblocks.common.data.PropertyHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class FramedFlowerPotModel extends BakedModelProxy
{
    private static BakedModel hangingPotModel;

    private final Cache<Block, PotModel> CACHE_BY_PLANT = Caffeine.newBuilder().build();
    private final boolean hanging;

    public FramedFlowerPotModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel);
        this.hanging = state.getValue(PropertyHolder.HANGING) && SupplementariesCompat.isLoaded();
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
    {
        return getOrCreatePotModel(state, data).getRenderTypes(state, rand, data);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, RenderType layer)
    {
        return getOrCreatePotModel(state, extraData).getQuads(state, side, rand, extraData, layer);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@Nonnull ModelData data)
    {
        return getOrCreatePotModel(FBContent.blockFramedFlowerPot.get().defaultBlockState(), data).getParticleIcon(data);
    }

    private PotModel getOrCreatePotModel(BlockState state, ModelData extraData)
    {
        Block flower = Optional.ofNullable(extraData.get(FramedFlowerPotBlockEntity.FLOWER_BLOCK)).orElse(Blocks.AIR);
        return CACHE_BY_PLANT.get(flower, block ->
                hanging ? new HangingPotModel(state, baseModel, flower) : new PotModel(state, baseModel, flower)
        );
    }

    @Nonnull
    @Override
    public ModelData getModelData(@Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData)
    {
        if (level.getBlockEntity(pos) instanceof FramedFlowerPotBlockEntity be)
        {
            return be.getModelData();
        }
        return tileData;
    }

    public static void cacheHangingModel(Map<ResourceLocation, BakedModel> registry)
    {
        hangingPotModel = registry.get(SupplementariesCompat.HANGING_MODEL_LOCATION);
    }



    private static class PotModel extends FramedBlockModel
    {
        private static final ResourceLocation POT_TEXTURE = new ResourceLocation("minecraft:block/flower_pot");
        private static final ResourceLocation DIRT_TEXTURE = new ResourceLocation("minecraft:block/dirt");

        private final Block flower;

        public PotModel(BlockState state, BakedModel baseModel, Block flower)
        {
            super(state, baseModel);
            this.flower = flower;
        }

        @Override
        protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
        {
            if (quad.getDirection() == Direction.DOWN)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(5F/16F, 5F/16F, 11F/16F, 11F/16F))
                        .export(quadMap.get(Direction.DOWN));
            }
            else if (quad.getDirection() == Direction.UP)
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(5F/16F, 5F/16F, 11F/16F, 6F/16F))
                        .apply(Modifiers.setPosition(6F/16F))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(5F/16F, 10F/16F, 11F/16F, 11F/16F))
                        .apply(Modifiers.setPosition(6F/16F))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(5F/16F, 6F/16F, 6F/16F, 10F/16F))
                        .apply(Modifiers.setPosition(6F/16F))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(10F/16F, 6F/16F, 11F/16F, 10F/16F))
                        .apply(Modifiers.setPosition(6F/16F))
                        .export(quadMap.get(null));
            }
            else if (!Utils.isY(quad.getDirection()))
            {
                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSide(5F/16F, 0, 11F/16F, 6F/16F))
                        .apply(Modifiers.setPosition(11F/16F))
                        .export(quadMap.get(null));

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSide(6F/16F, 1F/16F, 10F/16F, 6F/16F))
                        .apply(Modifiers.setPosition(6F/16F))
                        .export(quadMap.get(null));
            }
        }

        @Override
        protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
        {
            return ChunkRenderTypeSet.union(
                    ModelCache.getRenderTypes(Blocks.DIRT.defaultBlockState(), rand, ModelData.EMPTY),
                    ModelCache.getRenderTypes(flower.defaultBlockState(), rand, ModelData.EMPTY)
            );
        }

        @Override
        protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, RandomSource rand, ModelData data, RenderType layer)
        {
            BlockState potState = FramedFlowerPotBlock.getFlowerPotState(flower);
            BakedModel potModel = ModelCache.getModel(potState);
            if (!potState.isAir() && potModel.getRenderTypes(potState, rand, ModelData.EMPTY).contains(layer))
            {
                Arrays.stream(Direction.values())
                        .map(dir -> Pair.of(dir, getFilteredPlantQuads(potState, potModel, dir, rand, layer)))
                        .forEach(pair -> quadMap.get(pair.getFirst()).addAll(pair.getSecond()));

                quadMap.get(null).addAll(getFilteredPlantQuads(potState, potModel, null, rand, layer));
            }

            BakedModel dirtModel = ModelCache.getModel(Blocks.DIRT.defaultBlockState());
            if (dirtModel.getRenderTypes(Blocks.DIRT.defaultBlockState(), rand, ModelData.EMPTY).contains(layer))
            {
                dirtModel.getQuads(Blocks.DIRT.defaultBlockState(), Direction.UP, rand, ModelData.EMPTY, layer).forEach(q ->
                    QuadModifier.geometry(q)
                            .apply(Modifiers.cutTopBottom(6F/16F, 6F/16F, 10F/16F, 10F/16F))
                            .apply(Modifiers.setPosition(4F/16F))
                            .export(quadMap.get(null))
                );

                FramedBlockData fbData = data.get(FramedBlockData.PROPERTY);
                if (fbData != null && !fbData.getCamoState().canOcclude())
                {
                    dirtModel.getQuads(Blocks.DIRT.defaultBlockState(), Direction.DOWN, rand, ModelData.EMPTY, layer).forEach(q ->
                        QuadModifier.geometry(q)
                                .apply(Modifiers.cutTopBottom(6F/16F, 6F/16F, 10F/16F, 10F/16F))
                                .apply(Modifiers.setPosition(15F/16F))
                                .export(quadMap.get(null))
                    );

                    Direction.Plane.HORIZONTAL.stream()
                            .flatMap(face -> dirtModel.getQuads(Blocks.AIR.defaultBlockState(), face, rand, ModelData.EMPTY, layer).stream())
                            .forEach(q -> QuadModifier.geometry(q)
                                        .apply(Modifiers.cutSide(6F/16F, 1F/16F, 10F/16F, 4F/16F))
                                        .apply(Modifiers.setPosition(10F/16F))
                                        .export(quadMap.get(null))
                            );
                }
            }
        }

        private static List<BakedQuad> getFilteredPlantQuads(BlockState potState, BakedModel potModel, Direction face, RandomSource rand, RenderType layer)
        {
            return potModel.getQuads(potState, face, rand, ModelData.EMPTY, layer)
                    .stream()
                    .filter(q -> !q.getSprite().getName().equals(POT_TEXTURE))
                    .filter(q -> !q.getSprite().getName().equals(DIRT_TEXTURE))
                    .map(ModelUtils::invertTintIndex)
                    .collect(Collectors.toList());
        }
    }

    private static class HangingPotModel extends PotModel
    {
        public HangingPotModel(BlockState state, BakedModel baseModel, Block flower)
        {
            super(state, baseModel, flower);
        }

        @Override
        protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
        {
            return ChunkRenderTypeSet.union(
                    super.getAdditionalRenderTypes(rand, extraData),
                    ModelUtils.CUTOUT
            );
        }

        @Override
        protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, RandomSource rand, ModelData data, RenderType layer)
        {
            super.getAdditionalQuads(quadMap, state, rand, data, layer);

            if (layer == RenderType.cutout())
            {
                quadMap.get(null).addAll(hangingPotModel.getQuads(
                        null, null, rand, data, null
                ));

                for (Direction side : Direction.values())
                {
                    quadMap.get(side).addAll(hangingPotModel.getQuads(
                            null, side, rand, data, null
                    ));
                }
            }
        }
    }
}