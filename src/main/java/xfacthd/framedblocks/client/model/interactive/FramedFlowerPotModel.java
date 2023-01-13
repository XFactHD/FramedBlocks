package xfacthd.framedblocks.client.model.interactive;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.model.util.ModelCache;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.common.block.interactive.FramedFlowerPotBlock;
import xfacthd.framedblocks.common.blockentity.FramedFlowerPotBlockEntity;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.*;
import java.util.stream.Collectors;

public class FramedFlowerPotModel extends FramedBlockModel
{
    private static final ResourceLocation POT_TEXTURE = new ResourceLocation("minecraft:block/flower_pot");
    private static final ResourceLocation DIRT_TEXTURE = new ResourceLocation("minecraft:block/dirt");

    private final boolean hanging;
    private final BakedModel hangingPotModel;

    public FramedFlowerPotModel(BlockState state, BakedModel baseModel, Map<ResourceLocation, BakedModel> registry)
    {
        super(state, baseModel);
        this.hanging = SupplementariesCompat.isLoaded() && state.getValue(PropertyHolder.HANGING);
        this.hangingPotModel = hanging ? registry.get(SupplementariesCompat.HANGING_MODEL_LOCATION) : null;
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
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData data)
    {
        Block flower = getFlowerBlock(data);
        return ChunkRenderTypeSet.union(
                ModelCache.getRenderTypes(Blocks.DIRT.defaultBlockState(), rand, ModelData.EMPTY),
                ModelCache.getRenderTypes(flower.defaultBlockState(), rand, ModelData.EMPTY),
                hanging ? ModelUtils.CUTOUT : ChunkRenderTypeSet.none()
        );
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, RandomSource rand, ModelData data, RenderType layer)
    {
        BlockState potState = FramedFlowerPotBlock.getFlowerPotState(getFlowerBlock(data));
        if (!potState.isAir())
        {
            addPlantQuads(quadMap, potState, rand, layer);
        }
        addDirtQuads(quadMap, rand, data, layer);

        if (hanging && layer == RenderType.cutout())
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

    @Override
    protected QuadCacheKey makeCacheKey(BlockState state, Object ctCtx, ModelData data)
    {
        return new FlowerPotQuadCacheKey(state, ctCtx, getFlowerBlock(data));
    }

    private static void addPlantQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState potState, RandomSource rand, RenderType layer)
    {
        BakedModel potModel = ModelCache.getModel(potState);

        if (potModel.getRenderTypes(potState, rand, ModelData.EMPTY).contains(layer))
        {
            Arrays.stream(Direction.values())
                    .map(dir -> Pair.of(dir, getFilteredPlantQuads(potState, potModel, dir, rand, layer)))
                    .forEach(pair -> quadMap.get(pair.getFirst()).addAll(pair.getSecond()));

            quadMap.get(null).addAll(getFilteredPlantQuads(potState, potModel, null, rand, layer));
        }
    }

    private static List<BakedQuad> getFilteredPlantQuads(BlockState potState, BakedModel potModel, Direction face, RandomSource rand, RenderType layer)
    {
        return potModel.getQuads(potState, face, rand, ModelData.EMPTY, layer)
                .stream()
                .filter(q -> !ClientUtils.isTexture(q, POT_TEXTURE))
                .filter(q -> !ClientUtils.isTexture(q, DIRT_TEXTURE))
                .map(ModelUtils::invertTintIndex)
                .collect(Collectors.toList());
    }

    private static void addDirtQuads(Map<Direction, List<BakedQuad>> quadMap, RandomSource rand, ModelData data, RenderType layer)
    {
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

    private static Block getFlowerBlock(ModelData data)
    {
        Block flower = data.get(FramedFlowerPotBlockEntity.FLOWER_BLOCK);
        return flower != null ? flower : Blocks.AIR;
    }

    private record FlowerPotQuadCacheKey(BlockState state, Object ctCtx, Block flower) implements QuadCacheKey { }
}
