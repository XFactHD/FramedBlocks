package xfacthd.framedblocks.client.model;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.block.*;
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
import net.minecraftforge.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.client.util.FramedBlockData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public abstract class FramedBlockModel extends BakedModelProxy
{
    private static final boolean FORCE_NODATA = true;

    private final Table<BlockState, RenderType, Map<Direction, List<BakedQuad>>> quadCacheTable = HashBasedTable.create();
    private final Map<BlockState, IBakedModel> modelCache = new HashMap<>();
    private final BlockState state;
    private final BlockType type;

    public FramedBlockModel(BlockState state, IBakedModel baseModel)
    {
        super(baseModel);
        this.state = state;
        this.type = ((IFramedBlock)state.getBlock()).getBlockType();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData)
    {
        RenderType layer = MinecraftForgeClient.getRenderLayer();
        BlockState camoState = Blocks.AIR.getDefaultState();

        if (extraData instanceof FramedBlockData)
        {
            FramedBlockData data = (FramedBlockData) extraData;
            if (side != null && ((IFramedBlock)state.getBlock()).isSideHidden(
                    data.getWorld(),
                    data.getPos(),
                    state,
                    side
            )) { return Collections.emptyList(); }

            camoState = data.getCamoState();
            boolean canRender = camoState != null && camoState.getBlock() instanceof FlowingFluidBlock ?
                    RenderTypeLookup.canRenderInLayer(camoState.getFluidState(), layer) :
                    camoState != null && RenderTypeLookup.canRenderInLayer(camoState, layer);
            if (camoState != null && !camoState.isAir() && canRender)
            {
                return getCamoQuads(state, camoState, side, rand, extraData, layer);
            }
        }

        if (layer == null) { layer = RenderType.getCutout(); }
        if ((camoState == null || camoState.isAir()) && layer == RenderType.getCutout())
        {
            return getCamoQuads(state, FBContent.blockFramedCube.get().getDefaultState(), side, rand, extraData, layer);
        }

        return Collections.emptyList();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        if (state == null) { state = this.state; }
        return getCamoQuads(state, FBContent.blockFramedCube.get().getDefaultState(), side, rand, EmptyModelData.INSTANCE, RenderType.getCutout());
    }

    private List<BakedQuad> getCamoQuads(BlockState state, BlockState camoState, Direction side, Random rand, IModelData extraData, RenderType layer)
    {
        if (type.getCtmPredicate().test(state, side))
        {
            synchronized (modelCache)
            {
                if (!modelCache.containsKey(camoState))
                {
                    modelCache.put(camoState, getCamoModel(camoState));
                }
                IBakedModel model = modelCache.get(camoState);
                IModelData data = getCamoData(model, camoState, extraData);
                return model.getQuads(camoState, side, rand, data);
            }
        }
        else
        {
            synchronized (quadCacheTable)
            {
                if (!quadCacheTable.contains(camoState, layer))
                {
                    quadCacheTable.put(camoState, layer, makeQuads(state, camoState, rand, extraData));
                }
                return quadCacheTable.get(camoState, layer).get(side);
            }
        }
    }

    private Map<Direction, List<BakedQuad>> makeQuads(BlockState state, BlockState camoState, Random rand, IModelData data)
    {
        Map<Direction, List<BakedQuad>> quadMap = new Object2ObjectArrayMap<>(7);
        quadMap.put(null, new ArrayList<>());
        for (Direction dir : Direction.values()) { quadMap.put(dir, new ArrayList<>()); }

        IBakedModel camoModel = getCamoModel(camoState);
        List<BakedQuad> quads =
                getAllQuads(camoModel, camoState, rand, getCamoData(camoModel, camoState, data))
                .stream()
                .filter(q -> !type.getCtmPredicate().test(state, q.getFace()))
                .collect(Collectors.toList());

        for (BakedQuad quad : quads)
        {
            transformQuad(quadMap, quad);
        }
        postProcessQuads(quadMap);
        getAdditionalQuads(quadMap, state, rand, data);

        return quadMap;
    }

    protected abstract void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad);

    protected void postProcessQuads(Map<Direction, List<BakedQuad>> quadMap) {}

    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, Random rand, IModelData data) {}

    @Override
    public TextureAtlasSprite getParticleTexture(IModelData data)
    {
        if (data instanceof FramedBlockData)
        {
            BlockState camoState = data.getData(FramedBlockData.CAMO);
            if (camoState != null && !camoState.isAir())
            {
                synchronized (modelCache)
                {
                    return modelCache.computeIfAbsent(camoState, state ->
                            getCamoModel(camoState)
                    ).getParticleTexture();
                }
            }
        }
        return baseModel.getParticleTexture();
    }



    private final Map<BlockState, FluidDummyModel> fluidModels = new HashMap<>();
    protected IBakedModel getCamoModel(BlockState camoState)
    {
        if (camoState.getBlock() instanceof FlowingFluidBlock)
        {
            return fluidModels.computeIfAbsent(camoState, state -> new FluidDummyModel(((FlowingFluidBlock) state.getBlock()).getFluid()));
        }
        return Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(camoState);
    }

    private static IModelData getCamoData(IBakedModel model, BlockState state, IModelData data)
    {
        World world = data.getData(FramedBlockData.WORLD);
        BlockPos pos = data.getData(FramedBlockData.POS);

        if (world == null || pos == null || pos.equals(BlockPos.ZERO)) { return data; }

        return model.getModelData(world, pos, state, data);
    }

    protected static List<BakedQuad> getAllQuads(IBakedModel model, BlockState state, Random rand, IModelData data)
    {
        List<BakedQuad> quads = new ArrayList<>();
        for (Direction dir : Direction.values())
        {
            if (FMLEnvironment.production || FORCE_NODATA) { quads.addAll(model.getQuads(state, dir, rand, EmptyModelData.INSTANCE)); }
            else { quads.addAll(model.getQuads(state, dir, rand, data)); } //For debug purposes when creating new models
        }
        return quads;
    }
}