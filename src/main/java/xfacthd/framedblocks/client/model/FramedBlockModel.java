package xfacthd.framedblocks.client.model;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xfacthd.framedblocks.client.util.FramedBlockData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public abstract class FramedBlockModel extends BakedModelProxy
{
    private static final boolean FORCE_NODATA = true;

    private final Table<BlockState, RenderType, Map<Direction, List<BakedQuad>>> quadCacheTable = HashBasedTable.create();
    private final Map<BlockState, BakedModel> modelCache = new HashMap<>();
    private final BlockState state;
    private final BlockType type;

    public FramedBlockModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel);
        this.state = state;
        this.type = ((IFramedBlock)state.getBlock()).getBlockType();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData)
    {
        RenderType layer = MinecraftForgeClient.getRenderLayer();
        BlockState camoState = Blocks.AIR.defaultBlockState();

        if (extraData instanceof FramedBlockData data)
        {
            if (side != null && ((IFramedBlock)state.getBlock()).isSideHidden(
                    data.getWorld(),
                    data.getPos(),
                    state,
                    side
            )) { return Collections.emptyList(); }

            camoState = data.getCamoState();
            boolean canRender = camoState != null && camoState.getBlock() instanceof LiquidBlock ?
                    ItemBlockRenderTypes.canRenderInLayer(camoState.getFluidState(), layer) :
                    camoState != null && ItemBlockRenderTypes.canRenderInLayer(camoState, layer);
            if (camoState != null && !camoState.isAir() && canRender)
            {
                return getCamoQuads(state, camoState, side, rand, extraData, layer);
            }
        }

        if (layer == null) { layer = RenderType.cutout(); }
        if ((camoState == null || camoState.isAir()) && layer == RenderType.cutout())
        {
            return getCamoQuads(state, FBContent.blockFramedCube.get().defaultBlockState(), side, rand, extraData, layer);
        }

        return Collections.emptyList();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        if (state == null) { state = this.state; }
        return getCamoQuads(state, FBContent.blockFramedCube.get().defaultBlockState(), side, rand, EmptyModelData.INSTANCE, RenderType.cutout());
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
                BakedModel model = modelCache.get(camoState);
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

        BakedModel camoModel = getCamoModel(camoState);
        List<BakedQuad> quads =
                getAllQuads(camoModel, camoState, rand, getCamoData(camoModel, camoState, data))
                .stream()
                .filter(q -> !type.getCtmPredicate().test(state, q.getDirection()))
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

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData)
    {
        if (world.getBlockEntity(pos) instanceof FramedTileEntity te)
        {
            return te.getModelData();
        }
        return tileData;
    }

    @Override
    public TextureAtlasSprite getParticleIcon(IModelData data)
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
                    ).getParticleIcon();
                }
            }
        }
        return baseModel.getParticleIcon();
    }



    private final Map<BlockState, FluidDummyModel> fluidModels = new HashMap<>();
    protected BakedModel getCamoModel(BlockState camoState)
    {
        if (camoState.getBlock() instanceof LiquidBlock fluid)
        {
            return fluidModels.computeIfAbsent(camoState, state -> new FluidDummyModel(fluid.getFluid()));
        }
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(camoState);
    }

    private static IModelData getCamoData(BakedModel model, BlockState state, IModelData data)
    {
        Level world = data.getData(FramedBlockData.WORLD);
        BlockPos pos = data.getData(FramedBlockData.POS);

        if (world == null || pos == null || pos.equals(BlockPos.ZERO)) { return data; }

        return model.getModelData(world, pos, state, data);
    }

    protected static List<BakedQuad> getAllQuads(BakedModel model, BlockState state, Random rand, IModelData data)
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