package xfacthd.framedblocks.client.model;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.api.model.BakedModelProxy;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.FramedBlockData;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedFlowerPotBlock;
import xfacthd.framedblocks.common.blockentity.FramedFlowerPotBlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class FramedFlowerPotModel extends BakedModelProxy
{
    private final Map<Block, PotModel> CACHE_BY_PLANT = new HashMap<>();

    public FramedFlowerPotModel(@SuppressWarnings("unused") BlockState state, BakedModel baseModel) { super(baseModel); }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        return getOrCreatePotModel(state, extraData).getQuads(state, side, rand, extraData);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@Nonnull IModelData data)
    {
        return getOrCreatePotModel(FBContent.blockFramedFlowerPot.get().defaultBlockState(), data).getParticleIcon(data);
    }

    private PotModel getOrCreatePotModel(BlockState state, IModelData extraData)
    {
        Block flower = extraData.getData(FramedFlowerPotBlockEntity.FLOWER_BLOCK);
        Block finalFlower = flower != null ? flower : Blocks.AIR;
        synchronized (CACHE_BY_PLANT)
        {
            return CACHE_BY_PLANT.computeIfAbsent(flower, block -> new PotModel(state, baseModel, finalFlower));
        }
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData)
    {
        if (level.getBlockEntity(pos) instanceof FramedFlowerPotBlockEntity be)
        {
            return be.getModelData();
        }
        return tileData;
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
        protected boolean hasAdditionalQuadsInLayer(RenderType layer)
        {
            return ItemBlockRenderTypes.canRenderInLayer(Blocks.DIRT.defaultBlockState(), layer) ||
                   ItemBlockRenderTypes.canRenderInLayer(flower.defaultBlockState(), layer);
        }

        @Override
        protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, Random rand, IModelData data, RenderType layer)
        {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            BlockState potState = FramedFlowerPotBlock.getFlowerPotState(flower);
            if (!potState.isAir() && ItemBlockRenderTypes.canRenderInLayer(flower.defaultBlockState(), layer))
            {
                BakedModel potModel = dispatcher.getBlockModel(potState);

                Arrays.stream(Direction.values())
                        .map(dir -> Pair.of(dir, getFilteredPlantQuads(potState, potModel, dir, rand)))
                        .forEach(pair -> quadMap.get(pair.getFirst()).addAll(pair.getSecond()));

                quadMap.get(null).addAll(getFilteredPlantQuads(potState, potModel, null, rand));
            }

            if (ItemBlockRenderTypes.canRenderInLayer(Blocks.DIRT.defaultBlockState(), layer))
            {
                BakedModel dirtModel = dispatcher.getBlockModel(Blocks.DIRT.defaultBlockState());
                dirtModel.getQuads(Blocks.DIRT.defaultBlockState(), Direction.UP, rand, EmptyModelData.INSTANCE).forEach(q ->
                        QuadModifier.geometry(q)
                                .apply(Modifiers.cutTopBottom(6F/16F, 6F/16F, 10F/16F, 10F/16F))
                                .apply(Modifiers.setPosition(4F/16F))
                                .export(quadMap.get(null))
                );

                if (data instanceof FramedBlockData framedData && !framedData.getCamoState().canOcclude())
                {
                    dirtModel.getQuads(Blocks.DIRT.defaultBlockState(), Direction.DOWN, rand, EmptyModelData.INSTANCE).forEach(q ->
                            QuadModifier.geometry(q)
                                    .apply(Modifiers.cutTopBottom(6F/16F, 6F/16F, 10F/16F, 10F/16F))
                                    .apply(Modifiers.setPosition(15F/16F))
                                    .export(quadMap.get(null))
                    );

                    Direction.Plane.HORIZONTAL.stream()
                            .flatMap(face -> dirtModel.getQuads(Blocks.AIR.defaultBlockState(), face, rand, EmptyModelData.INSTANCE).stream())
                            .forEach(q -> QuadModifier.geometry(q)
                                    .apply(Modifiers.cutSide(6F/16F, 1F/16F, 10F/16F, 4F/16F))
                                    .apply(Modifiers.setPosition(10F/16F))
                                    .export(quadMap.get(null))
                            );
                }
            }
        }

        private static List<BakedQuad> getFilteredPlantQuads(BlockState potState, BakedModel potModel, Direction face, Random rand)
        {
            return potModel.getQuads(potState, face, rand, EmptyModelData.INSTANCE)
                    .stream()
                    .filter(q -> !q.getSprite().getName().equals(POT_TEXTURE))
                    .filter(q -> !q.getSprite().getName().equals(DIRT_TEXTURE))
                    .map(ModelUtils::invertTintIndex)
                    .collect(Collectors.toList());
        }
    }
}