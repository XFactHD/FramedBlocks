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
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.*;
import net.minecraftforge.common.util.ConcatenatedListView;
import xfacthd.framedblocks.api.model.BakedModelProxy;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockStateCache;
import xfacthd.framedblocks.common.block.IFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

import javax.annotation.Nullable;
import java.util.*;

public class FramedDoubleBlockModel extends BakedModelProxy
{
    private static final ModelData EMPTY_FRAME = makeDefaultData(false);
    private static final ModelData EMPTY_ALT_FRAME = makeDefaultData(true);

    private final boolean specialItemModel;
    private final DoubleBlockTopInteractionMode particleMode;
    private final Vec3 firstpersonTransform;
    private final Tuple<BlockState, BlockState> dummyStates;
    private Tuple<BakedModel, BakedModel> models = null;

    public FramedDoubleBlockModel(
            BlockState state,
            BakedModel baseModel,
            Vec3 firstpersonTransform,
            boolean specialItemModel
    )
    {
        super(baseModel);
        DoubleBlockStateCache cache = ((IFramedDoubleBlock) state.getBlock()).getCache(state);
        this.dummyStates = cache.getBlockPair();
        this.particleMode = cache.getTopInteractionMode();
        this.firstpersonTransform = firstpersonTransform;
        this.specialItemModel = specialItemModel;
    }

    @Override
    public List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction side,
            RandomSource rand,
            ModelData extraData,
            RenderType layer
    )
    {
        List<List<BakedQuad>> quads = new ArrayList<>(2);
        Tuple<BakedModel, BakedModel> models = getModels();

        ModelData dataLeft = extraData.get(FramedDoubleBlockEntity.DATA_LEFT);
        quads.add(
                models.getA().getQuads(dummyStates.getA(), side, rand, dataLeft != null ? dataLeft : EMPTY_FRAME, layer)
        );

        ModelData dataRight = extraData.get(FramedDoubleBlockEntity.DATA_RIGHT);
        quads.add(invertTintIndizes(
                models.getB().getQuads(dummyStates.getB(), side, rand, dataRight != null ? dataRight : EMPTY_ALT_FRAME, layer)
        ));

        return ConcatenatedListView.of(quads);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand)
    {
        if (specialItemModel)
        {
            return getQuads(state, side, rand, ModelData.EMPTY, RenderType.cutout());
        }
        return super.getQuads(state, side, rand);
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
                yield baseModel.getParticleIcon();
            }
        };
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data)
    {
        Tuple<BakedModel, BakedModel> models = getModels();

        ModelData dataLeft = data.get(FramedDoubleBlockEntity.DATA_LEFT);
        ModelData dataRight = data.get(FramedDoubleBlockEntity.DATA_RIGHT);

        return ChunkRenderTypeSet.union(
                models.getA().getRenderTypes(dummyStates.getA(), rand, dataLeft != null ? dataLeft : ModelData.EMPTY),
                models.getB().getRenderTypes(dummyStates.getB(), rand, dataRight != null ? dataRight : ModelData.EMPTY)
        );
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData tileData)
    {
        Tuple<BakedModel, BakedModel> models = getModels();

        ModelData dataLeft = tileData.get(FramedDoubleBlockEntity.DATA_LEFT);
        ModelData dataRight = tileData.get(FramedDoubleBlockEntity.DATA_RIGHT);

        dataLeft = models.getA().getModelData(level, pos, dummyStates.getA(), dataLeft != null ? dataLeft : ModelData.EMPTY);
        dataRight = models.getB().getModelData(level, pos, dummyStates.getB(), dataRight != null ? dataRight : ModelData.EMPTY);

        return tileData.derive()
                .with(FramedDoubleBlockEntity.DATA_LEFT, dataLeft)
                .with(FramedDoubleBlockEntity.DATA_RIGHT, dataRight)
                .build();
    }

    @Override
    protected void applyInHandTransformation(PoseStack poseStack, ItemDisplayContext ctx)
    {
        if (firstpersonTransform != null)
        {
            poseStack.translate(firstpersonTransform.x, firstpersonTransform.y, firstpersonTransform.z);
        }
    }



    protected final Tuple<BakedModel, BakedModel> getModels()
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
    protected final TextureAtlasSprite getSpriteOrDefault(ModelData data, boolean secondary)
    {
        TextureAtlasSprite sprite = getSprite(data, secondary);
        //noinspection deprecation
        return sprite != null ? sprite : baseModel.getParticleIcon();
    }

    protected final TextureAtlasSprite getSprite(ModelData data, boolean secondary)
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
        FramedBlockData data = new FramedBlockData.Immutable(Blocks.AIR.defaultBlockState(), new boolean[6], altModel);
        return ModelData.builder()
                .with(FramedBlockData.PROPERTY, data)
                .build();
    }
}