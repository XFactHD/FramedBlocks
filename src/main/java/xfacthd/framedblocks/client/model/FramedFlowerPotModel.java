package xfacthd.framedblocks.client.model;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedFlowerPotBlock;
import xfacthd.framedblocks.common.tileentity.FramedFlowerPotTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class FramedFlowerPotModel extends BakedModelProxy
{
    private final Map<Block, PotModel> CACHE_BY_PLANT = new HashMap<>();

    public FramedFlowerPotModel(@SuppressWarnings("unused") BlockState state, IBakedModel baseModel) { super(baseModel); }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        return getOrCreatePotModel(state, extraData).getQuads(state, side, rand, extraData);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data)
    {
        return getOrCreatePotModel(FBContent.blockFramedFlowerPot.get().getDefaultState(), data).getParticleTexture(data);
    }

    private PotModel getOrCreatePotModel(BlockState state, IModelData extraData)
    {
        Block flower = Optional.ofNullable(extraData.getData(FramedFlowerPotTileEntity.FLOWER_BLOCK)).orElse(Blocks.AIR);
        return CACHE_BY_PLANT.computeIfAbsent(flower, block -> new PotModel(state, baseModel, flower));
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof FramedFlowerPotTileEntity)
        {
            return te.getModelData();
        }
        return tileData;
    }

    private static class PotModel extends FramedBlockModel
    {
        private final Block flower;

        public PotModel(BlockState state, IBakedModel baseModel, Block flower)
        {
            super(state, baseModel);
            this.flower = flower;
        }

        @Override
        protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
        {
            if (quad.getFace() == Direction.DOWN)
            {
                BakedQuad botQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(botQuad, 5F/16F, 5F/16F, 11F/16F, 11F/16F))
                {
                    quadMap.get(Direction.DOWN).add(botQuad);
                }
            }
            else if (quad.getFace() == Direction.UP)
            {
                BakedQuad topQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topQuad, 5F/16F, 5F/16F, 11F/16F, 6F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topQuad, 6F/16F);
                    quadMap.get(null).add(topQuad);
                }

                topQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topQuad, 5F/16F, 10F/16F, 11F/16F, 11F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topQuad, 6F/16F);
                    quadMap.get(null).add(topQuad);
                }

                topQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topQuad, 5F/16F, 6F/16F, 6F/16F, 10F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topQuad, 6F/16F);
                    quadMap.get(null).add(topQuad);
                }

                topQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topQuad, 10F/16F, 6F/16F, 11F/16F, 10F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topQuad, 6F/16F);
                    quadMap.get(null).add(topQuad);
                }
            }
            else if (quad.getFace().getAxis() != Direction.Axis.Y)
            {
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(sideQuad, 5F/16F, 0, 11F/16F, 6F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 11F/16F);
                    quadMap.get(null).add(sideQuad);
                }

                sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(sideQuad, 6F/16F, 1F/16F, 10F/16F, 6F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 6F/16F);
                    quadMap.get(null).add(sideQuad);
                }
            }
        }

        @Override
        protected boolean hasAdditionalQuadsInLayer(RenderType layer)
        {
            return RenderTypeLookup.canRenderInLayer(Blocks.DIRT.getDefaultState(), layer) ||
                   RenderTypeLookup.canRenderInLayer(flower.getDefaultState(), layer);
        }

        @Override
        @SuppressWarnings("deprecation")
        protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, Random rand, IModelData data, RenderType layer)
        {
            BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
            BlockState potState = FramedFlowerPotBlock.getFlowerPotState(flower);
            if (!potState.isAir() && RenderTypeLookup.canRenderInLayer(flower.getDefaultState(), layer))
            {
                IBakedModel potModel = dispatcher.getModelForState(potState);

                Arrays.stream(Direction.values())
                        .map(dir -> Pair.of(dir, getFilteredPlantQuads(potState, potModel, dir, rand)))
                        .forEach(pair -> quadMap.get(pair.getFirst()).addAll(pair.getSecond()));

                quadMap.get(null).addAll(getFilteredPlantQuads(potState, potModel, null, rand));
            }

            if (RenderTypeLookup.canRenderInLayer(Blocks.DIRT.getDefaultState(), layer))
            {
                IBakedModel dirtModel = dispatcher.getModelForState(Blocks.DIRT.getDefaultState());
                dirtModel.getQuads(Blocks.DIRT.getDefaultState(), Direction.UP, rand, EmptyModelData.INSTANCE).forEach(q ->
                {
                    BakedQuad topQuad = ModelUtils.duplicateQuad(q);
                    if (BakedQuadTransformer.createTopBottomQuad(topQuad, 6F / 16F, 6F / 16F, 10F / 16F, 10F / 16F))
                    {
                        BakedQuadTransformer.setQuadPosInFacingDir(topQuad, 4F / 16F);
                        quadMap.get(null).add(topQuad);
                    }
                });

                BlockState camoState = data instanceof FramedBlockData ? ((FramedBlockData) data).getCamoState() : Blocks.AIR.getDefaultState();
                if (!camoState.isSolid())
                {
                    dirtModel.getQuads(Blocks.DIRT.getDefaultState(), Direction.DOWN, rand, EmptyModelData.INSTANCE).forEach(q ->
                    {
                        BakedQuad botQuad = ModelUtils.duplicateQuad(q);
                        if (BakedQuadTransformer.createTopBottomQuad(botQuad, 6F / 16F, 6F / 16F, 10F / 16F, 10F / 16F))
                        {
                            BakedQuadTransformer.setQuadPosInFacingDir(botQuad, 15F / 16F);
                            quadMap.get(null).add(botQuad);
                        }
                    });

                    Direction.Plane.HORIZONTAL.getDirectionValues()
                            .flatMap(face -> dirtModel.getQuads(Blocks.AIR.getDefaultState(), face, rand, EmptyModelData.INSTANCE).stream())
                            .forEach(q ->
                            {
                                BakedQuad sideQuad = ModelUtils.duplicateQuad(q);
                                if (BakedQuadTransformer.createSideQuad(sideQuad, 6F / 16F, 1F / 16F, 10F / 16F, 4F / 16F))
                                {
                                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 10F / 16F);
                                    quadMap.get(null).add(sideQuad);
                                }
                            });
                }
            }
        }

        private List<BakedQuad> getFilteredPlantQuads(BlockState potState, IBakedModel potModel, Direction face, Random rand)
        {
            return potModel.getQuads(potState, face, rand, EmptyModelData.INSTANCE)
                    .stream()
                    .filter(q -> !q.getSprite().getName().getPath().equals("block/flower_pot"))
                    .filter(q -> !q.getSprite().getName().getPath().equals("block/dirt"))
                    .collect(Collectors.toList());
        }
    }
}