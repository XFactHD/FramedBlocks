package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.cache.QuadCacheKey;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.model.util.ModelCache;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.common.block.interactive.FramedFlowerPotBlock;
import xfacthd.framedblocks.common.blockentity.special.FramedFlowerPotBlockEntity;
import xfacthd.framedblocks.common.compat.supplementaries.SupplementariesCompat;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;

public class FramedFlowerPotGeometry extends Geometry
{
    private static final ResourceLocation POT_TEXTURE = new ResourceLocation("minecraft:block/flower_pot");
    private static final ResourceLocation DIRT_TEXTURE = new ResourceLocation("minecraft:block/dirt");

    private final boolean hanging;
    private final BakedModel hangingPotModel;

    public FramedFlowerPotGeometry(GeometryFactory.Context ctx)
    {
        this.hanging = SupplementariesCompat.isLoaded() && ctx.state().getValue(PropertyHolder.HANGING);
        this.hangingPotModel = hanging ? ctx.modelLookup().get(SupplementariesCompat.HANGING_MODEL_LOCATION) : null;
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
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
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData data)
    {
        BlockState potState = FramedFlowerPotBlock.getFlowerPotState(getFlowerBlock(data));
        return ChunkRenderTypeSet.union(
                ModelCache.getRenderTypes(Blocks.DIRT.defaultBlockState(), rand, ModelData.EMPTY),
                !potState.isAir() ? ModelCache.getRenderTypes(potState, rand, ModelData.EMPTY) : ChunkRenderTypeSet.none(),
                hanging ? ModelUtils.CUTOUT : ChunkRenderTypeSet.none()
        );
    }

    @Override
    public void getAdditionalQuads(QuadMap quadMap, RandomSource rand, ModelData data, RenderType layer)
    {
        BlockState potState = FramedFlowerPotBlock.getFlowerPotState(getFlowerBlock(data));
        if (!potState.isAir())
        {
            addPlantQuads(quadMap, potState, rand, layer);
        }
        addDirtQuads(quadMap, rand, data, layer);

        if (hanging && layer == RenderType.cutout())
        {
            Utils.forAllDirections(dir ->
                    Utils.copyAll(hangingPotModel.getQuads(null, dir, rand, data, null), quadMap.get(dir))
            );
        }
    }

    @Override
    public QuadCacheKey makeCacheKey(BlockState state, Object ctCtx, ModelData data)
    {
        return new FlowerPotQuadCacheKey(state, ctCtx, getFlowerBlock(data));
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }

    private static void addPlantQuads(QuadMap quadMap, BlockState potState, RandomSource rand, RenderType layer)
    {
        BakedModel potModel = ModelCache.getModel(potState);

        if (potModel.getRenderTypes(potState, rand, ModelData.EMPTY).contains(layer))
        {
            Utils.forAllDirections(dir ->
            {
                List<BakedQuad> outQuads = quadMap.get(dir);
                for (BakedQuad quad : potModel.getQuads(potState, dir, rand, ModelData.EMPTY, layer))
                {
                    if (!ClientUtils.isTexture(quad, POT_TEXTURE) && !ClientUtils.isTexture(quad, DIRT_TEXTURE))
                    {
                        outQuads.add(ModelUtils.invertTintIndex(quad));
                    }
                }
            });
        }
    }

    private static void addDirtQuads(QuadMap quadMap, RandomSource rand, ModelData data, RenderType layer)
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
                for (BakedQuad quad : dirtModel.getQuads(Blocks.DIRT.defaultBlockState(), Direction.DOWN, rand, ModelData.EMPTY, layer))
                {
                    QuadModifier.geometry(quad)
                            .apply(Modifiers.cutTopBottom(6F / 16F, 6F / 16F, 10F / 16F, 10F / 16F))
                            .apply(Modifiers.setPosition(15F / 16F))
                            .export(quadMap.get(null));
                }

                Utils.forHorizontalDirections(dir ->
                {
                    for (BakedQuad quad : dirtModel.getQuads(Blocks.AIR.defaultBlockState(), dir, rand, ModelData.EMPTY, layer))
                    {
                        QuadModifier.geometry(quad)
                                .apply(Modifiers.cutSide(6F / 16F, 1F / 16F, 10F / 16F, 4F / 16F))
                                .apply(Modifiers.setPosition(10F / 16F))
                                .export(quadMap.get(null));
                    }
                });
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
