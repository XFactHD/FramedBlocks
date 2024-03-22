package xfacthd.framedblocks.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
import net.neoforged.neoforge.common.util.ConcatenatedListView;
import net.neoforged.neoforge.common.util.TriState;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockStateCache;
import xfacthd.framedblocks.common.block.IFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

import javax.annotation.Nullable;
import java.util.*;

public final class FramedDoubleBlockModel extends BakedModelWrapper<BakedModel>
{
    private static final ModelData EMPTY_FRAME = makeDefaultData(false);
    private static final ModelData EMPTY_ALT_FRAME = makeDefaultData(true);

    private final DoubleBlockTopInteractionMode particleMode;
    private final Vec3 firstpersonTransform;
    private final Tuple<BlockState, BlockState> dummyStates;
    private Tuple<BakedModel, BakedModel> models = null;

    public FramedDoubleBlockModel(GeometryFactory.Context ctx, Vec3 firstpersonTransform)
    {
        super(ctx.baseModel());
        BlockState state = ctx.state();
        DoubleBlockStateCache cache = ((IFramedDoubleBlock) state.getBlock()).getCache(state);
        this.dummyStates = cache.getBlockPair();
        this.particleMode = cache.getTopInteractionMode();
        this.firstpersonTransform = firstpersonTransform;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, RenderType layer)
    {
        List<List<BakedQuad>> quads = new ArrayList<>(2);
        Tuple<BakedModel, BakedModel> models = getModels();

        ModelData dataLeft = Objects.requireNonNullElse(extraData.get(FramedDoubleBlockEntity.DATA_LEFT), EMPTY_FRAME);
        quads.add(models.getA().getQuads(dummyStates.getA(), side, rand, dataLeft, layer));

        ModelData dataRight = Objects.requireNonNullElse(extraData.get(FramedDoubleBlockEntity.DATA_RIGHT), EMPTY_ALT_FRAME);
        quads.add(invertTintIndizes(models.getB().getQuads(dummyStates.getB(), side, rand, dataRight, layer)));

        return ConcatenatedListView.of(quads);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand)
    {
        return getQuads(state, side, rand, ModelData.EMPTY, RenderType.cutout());
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data)
    {
        return switch (particleMode)
        {
            case FIRST -> getSpriteOrDefault(data, false);
            case SECOND -> getSpriteOrDefault(data, true);
            case EITHER ->
            {
                TextureAtlasSprite sprite = getSprite(data, false);
                if (sprite != null)
                {
                    yield sprite;
                }

                sprite = getSprite(data, true);
                if (sprite != null)
                {
                    yield sprite;
                }

                //noinspection deprecation
                yield originalModel.getParticleIcon();
            }
        };
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data)
    {
        Tuple<BakedModel, BakedModel> models = getModels();

        ModelData dataLeft = Objects.requireNonNullElse(data.get(FramedDoubleBlockEntity.DATA_LEFT), ModelData.EMPTY);
        ModelData dataRight = Objects.requireNonNullElse(data.get(FramedDoubleBlockEntity.DATA_RIGHT), ModelData.EMPTY);

        return ChunkRenderTypeSet.union(
                models.getA().getRenderTypes(dummyStates.getA(), rand, dataLeft),
                models.getB().getRenderTypes(dummyStates.getB(), rand, dataRight)
        );
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData tileData)
    {
        Tuple<BakedModel, BakedModel> models = getModels();

        ModelData dataLeft = Objects.requireNonNullElse(tileData.get(FramedDoubleBlockEntity.DATA_LEFT), ModelData.EMPTY);
        ModelData dataRight = Objects.requireNonNullElse(tileData.get(FramedDoubleBlockEntity.DATA_RIGHT), ModelData.EMPTY);

        dataLeft = models.getA().getModelData(level, pos, dummyStates.getA(), dataLeft);
        dataRight = models.getB().getModelData(level, pos, dummyStates.getB(), dataRight);

        return tileData.derive()
                .with(FramedDoubleBlockEntity.DATA_LEFT, dataLeft)
                .with(FramedDoubleBlockEntity.DATA_RIGHT, dataRight)
                .build();
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext type, PoseStack poseStack, boolean applyLeftHandTransform)
    {
        getTransforms().getTransform(type).apply(applyLeftHandTransform, poseStack);
        if (type.firstPerson() || type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || type == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
        {
            if (firstpersonTransform != null)
            {
                poseStack.translate(firstpersonTransform.x, firstpersonTransform.y, firstpersonTransform.z);
            }
        }
        return this;
    }

    @Override
    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType)
    {
        Tuple<BakedModel, BakedModel> models = getModels();

        ModelData dataLeft = Objects.requireNonNullElse(data.get(FramedDoubleBlockEntity.DATA_LEFT), ModelData.EMPTY);
        TriState aoLeft = models.getA().useAmbientOcclusion(dummyStates.getA(), dataLeft, renderType);
        if (aoLeft == TriState.TRUE)
        {
            return aoLeft;
        }

        ModelData dataRight = Objects.requireNonNullElse(data.get(FramedDoubleBlockEntity.DATA_RIGHT), ModelData.EMPTY);
        TriState aoRight = models.getA().useAmbientOcclusion(dummyStates.getB(), dataRight, renderType);
        if (aoRight == TriState.TRUE)
        {
            return aoRight;
        }

        return TriState.DEFAULT;
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
    {
        return List.of(this);
    }



    private Tuple<BakedModel, BakedModel> getModels()
    {
        if (models == null)
        {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            models = new Tuple<>(
                    dispatcher.getBlockModel(dummyStates.getA()),
                    dispatcher.getBlockModel(dummyStates.getB())
            );
        }
        return models;
    }

    /**
     * Returns the camo-dependent particle texture of the side given by {@code key} when the camo is not air,
     * else returns the basic "framed block" sprite
     */
    private TextureAtlasSprite getSpriteOrDefault(ModelData data, boolean secondary)
    {
        TextureAtlasSprite sprite = getSprite(data, secondary);
        //noinspection deprecation
        return sprite != null ? sprite : originalModel.getParticleIcon();
    }

    private TextureAtlasSprite getSprite(ModelData data, boolean secondary)
    {
        ModelData innerData = data.get(secondary ? FramedDoubleBlockEntity.DATA_RIGHT : FramedDoubleBlockEntity.DATA_LEFT);
        if (innerData != null)
        {
            FramedBlockData fbData = innerData.get(FramedBlockData.PROPERTY);
            if (fbData != null && !fbData.getCamoState().isAir())
            {
                BakedModel model = secondary ? getModels().getB() : getModels().getA();
                return model.getParticleIcon(innerData);
            }
        }
        return null;
    }

    private static List<BakedQuad> invertTintIndizes(List<BakedQuad> quads)
    {
        List<BakedQuad> inverted = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads)
        {
            inverted.add(ModelUtils.invertTintIndex(quad));
        }
        return inverted;
    }

    private static ModelData makeDefaultData(boolean altModel)
    {
        FramedBlockData data = new FramedBlockData(Blocks.AIR.defaultBlockState(), altModel);
        return ModelData.builder().with(FramedBlockData.PROPERTY, data).build();
    }
}