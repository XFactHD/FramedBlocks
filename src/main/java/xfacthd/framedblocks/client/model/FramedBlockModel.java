package xfacthd.framedblocks.client.model;

import com.google.common.collect.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.data.BlockType;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("deprecation")
public class FramedBlockModel extends BakedModelProxy
{
    private final Table<BlockState, RenderType, List<BakedQuad>> quadCacheTable = HashBasedTable.create();
    private final Map<BlockState, IBakedModel> modelCache = new HashMap<>();
    private final HashMap<BlockState, TextureAtlasSprite> particleCache = new HashMap<>();
    private final BlockType type;
    private ImmutableList<BakedQuad> baseQuads = null;

    public FramedBlockModel(BlockType type, IBakedModel baseModel)
    {
        super(baseModel);
        this.type = type;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData)
    {
        if (baseQuads == null) { baseQuads = prepareBaseQuads(state, rand); }

        RenderType layer = MinecraftForgeClient.getRenderLayer();
        if (extraData instanceof FramedBlockData)
        {
            BlockState camoState = extraData.getData(FramedBlockData.CAMO);
            if (camoState != null && !camoState.isAir())
            {
                if (RenderTypeLookup.canRenderInLayer(camoState, layer))
                {
                    return getCamoQuads(state, camoState, side, rand, extraData, layer);
                }
                return Collections.emptyList();
            }
        }

        return layer == RenderType.getCutoutMipped() ? baseQuads : Collections.emptyList();
    }

    protected List<BakedQuad> getCamoQuads(BlockState state, BlockState camoState, Direction side, Random rand, IModelData extraData, RenderType layer)
    {
        if (side == null)
        {
            synchronized (quadCacheTable)
            {
                if (!quadCacheTable.contains(camoState, layer))
                {
                    quadCacheTable.put(camoState, layer, makeQuads(state, camoState, rand));
                }
                return quadCacheTable.get(camoState, layer);
            }
        }
        else if (type.getCtmPredicate().test(state, side))
        {
            synchronized (modelCache)
            {
                if (!modelCache.containsKey(camoState))
                {
                    modelCache.put(camoState, getCamoModel(camoState, false));
                }
                IBakedModel model = modelCache.get(camoState);
                IModelData data = getCamoData(model, camoState, extraData);
                return model.getQuads(camoState, side, rand, data);
            }
        }
        return Collections.emptyList();
    }

    private static IModelData getCamoData(IBakedModel model, BlockState state, IModelData data)
    {
        World world = data.getData(FramedBlockData.WORLD);
        BlockPos pos = data.getData(FramedBlockData.POS);

        if (world == null || pos == null || pos.equals(BlockPos.ZERO)) { return data; }

        return model.getModelData(world, pos, state, data);
    }

    private List<BakedQuad> makeQuads(BlockState state, BlockState camoState, Random rand)
    {
        IBakedModel camoModel = getCamoModel(camoState, true);
        ImmutableList.Builder<BakedQuad> listBuilder = ImmutableList.builder();

        for (BakedQuad quad : baseQuads)
        {
            if (type.getCtmPredicate().test(state, quad.getFace())) { continue; }

            List<BakedQuad> camoQuads = getSideQuads(camoModel, camoState, quad.getFace(), rand);
            for (BakedQuad camoQuad : camoQuads)
            {
                listBuilder.add(buildQuad(quad, camoQuad));
            }
        }

        return listBuilder.build();
    }

    protected BakedQuad buildQuad(BakedQuad baseQuad, BakedQuad camoQuad)
    {
        BakedQuad quadCopy = new BakedQuad(
                Arrays.copyOf(baseQuad.getVertexData(), baseQuad.getVertexData().length),
                camoQuad.getTintIndex(),
                baseQuad.getFace(),
                camoQuad.getSprite(),
                baseQuad.applyDiffuseLighting()
        );

        ModelUtils.modifyQuad(quadCopy, ((pos, color, uv, light, normal) ->
                setTextureUV(uv, baseQuad.getSprite(), camoQuad.getSprite())
        ));

        return quadCopy;
    }

    private void setTextureUV(float[][] uv, TextureAtlasSprite baseSprite, TextureAtlasSprite camoSprite)
    {
        for (int vert = 0; vert < 4; vert++)
        {
            uv[vert][0] = camoSprite.getInterpolatedU(uninterpolateU(baseSprite, uv[vert][0]));
            uv[vert][1] = camoSprite.getInterpolatedV(uninterpolateV(baseSprite, uv[vert][1]));
        }
    }

    private static float uninterpolateU(TextureAtlasSprite sprite, float u)
    {
        float h = sprite.getMaxU() - sprite.getMinU();
        return (u - sprite.getMinU()) / h * 16.0F;
    }

    private static float uninterpolateV(TextureAtlasSprite sprite, float v)
    {
        float w = sprite.getMaxV() - sprite.getMinV();
        return (v - sprite.getMinV()) / w * 16.0F;
    }

    protected ImmutableList<BakedQuad> prepareBaseQuads(BlockState state, Random rand)
    {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        builder.addAll(getAllQuads(baseModel, state, rand));
        return builder.build();
    }

    //Only used by item models
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        if (baseQuads == null) { baseQuads = prepareBaseQuads(state, rand); }
        return baseQuads;
    }

    @Override
    public TextureAtlasSprite getParticleTexture(IModelData data)
    {
        if (data instanceof FramedBlockData)
        {
            BlockState camoState = data.getData(FramedBlockData.CAMO);
            if (camoState != null && !camoState.isAir())
            {
                synchronized (particleCache)
                {
                    return particleCache.computeIfAbsent(camoState, state ->
                            getCamoModel(camoState, false).getParticleTexture()
                    );
                }
            }
        }
        return baseModel.getParticleTexture();
    }

    /*
     * Static helpers
     */

    private static Field ctmParent = null;
    protected static IBakedModel getCamoModel(BlockState camoState, boolean needCtmParent)
    {
        IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(camoState);
        if (needCtmParent && model.getClass().getName().equals("team.chisel.ctm.client.model.ModelBakedCTM"))
        {
            try
            {
                if (ctmParent == null)
                {
                    ctmParent = model.getClass().getSuperclass().getDeclaredField("parent");
                    ctmParent.setAccessible(true);
                }

                model = (IBakedModel) ctmParent.get(model);
            }
            catch (IllegalAccessException | NoSuchFieldException e)
            {
                FramedBlocks.LOGGER.error("Failed to retrieve parent model from CTM model!", e);
            }
        }
        return model;
    }

    protected static List<BakedQuad> getSideQuads(IBakedModel model, BlockState state, Direction side, Random rand)
    {
        return getSideQuads(model, state, side, rand, EmptyModelData.INSTANCE);
    }

    @SuppressWarnings("SameParameterValue")
    protected static List<BakedQuad> getSideQuads(IBakedModel model, BlockState state, Direction side, Random rand, IModelData data)
    {
        return model.getQuads(state, side, rand, data);
    }

    protected static List<BakedQuad> getAllQuads(IBakedModel model, BlockState state, Random rand)
    {
        return getAllQuads(model, state, rand, EmptyModelData.INSTANCE);
    }

    public static List<BakedQuad> getAllQuads(IBakedModel model, BlockState state, Random rand, IModelData modelData)
    {
        List<BakedQuad> quads = new ArrayList<>(model.getQuads(state, null, rand, modelData));
        for (Direction dir : Direction.values())
        {
            quads.addAll(model.getQuads(state, dir, rand, modelData));
        }
        return quads;
    }
}