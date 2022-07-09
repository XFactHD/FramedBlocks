package xfacthd.framedblocks.client.model;

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
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.*;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.model.BakedModelProxy;
import xfacthd.framedblocks.api.util.FramedBlockData;
import xfacthd.framedblocks.api.util.client.ModelUtils;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class FramedDoubleBlockModel extends BakedModelProxy
{
    private final boolean specialItemModel;
    private Tuple<BlockState, BlockState> dummyStates = null;
    private Tuple<BakedModel, BakedModel> models = null;

    protected FramedDoubleBlockModel(BakedModel baseModel, boolean specialItemModel)
    {
        super(baseModel);
        this.specialItemModel = specialItemModel;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, RenderType layer)
    {
        ModelData dataLeft = extraData.get(FramedDoubleBlockEntity.DATA_LEFT);
        List<BakedQuad> quads = new ArrayList<>(
                getModels().getA().getQuads(dummyStates.getA(), side, rand, dataLeft != null ? dataLeft : ModelData.EMPTY, layer)
        );

        ModelData dataRight = extraData.get(FramedDoubleBlockEntity.DATA_RIGHT);
        quads.addAll(invertTintIndizes(
                getModels().getB().getQuads(dummyStates.getB(), side, rand, dataRight != null ? dataRight : ModelData.EMPTY, layer)
        ));

        return quads;
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand)
    {
        if (specialItemModel)
        {
            List<BakedQuad> quads = new ArrayList<>(getModels().getA().getQuads(state, side, rand));
            quads.addAll(getModels().getB().getQuads(state, side, rand));
            return quads;
        }
        return super.getQuads(state, side, rand);
    }

    @Override
    @SuppressWarnings("deprecation")
    public TextureAtlasSprite getParticleIcon(@Nonnull ModelData data)
    {
        ModelData innerData = data.get(FramedDoubleBlockEntity.DATA_LEFT);
        if (innerData != null)
        {
            FramedBlockData fbData = innerData.get(FramedBlockData.PROPERTY);
            if (fbData != null && !fbData.getCamoState().isAir())
            {
                return getModels().getA().getParticleIcon(innerData);
            }
        }
        innerData = data.get(FramedDoubleBlockEntity.DATA_RIGHT);
        if (innerData != null)
        {
            FramedBlockData fbData = innerData.get(FramedBlockData.PROPERTY);
            if (fbData != null && !fbData.getCamoState().isAir())
            {
                return getModels().getB().getParticleIcon(innerData);
            }
        }
        return baseModel.getParticleIcon();
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
    {
        Tuple<BakedModel, BakedModel> models = getModels();

        ModelData dataLeft = data.get(FramedDoubleBlockEntity.DATA_LEFT);
        ModelData dataRight = data.get(FramedDoubleBlockEntity.DATA_RIGHT);

        return ChunkRenderTypeSet.union(
                models.getA().getRenderTypes(dummyStates.getA(), rand, dataLeft != null ? dataLeft : ModelData.EMPTY),
                models.getB().getRenderTypes(dummyStates.getB(), rand, dataRight != null ? dataRight : ModelData.EMPTY)
        );
    }

    @Nonnull
    @Override
    public ModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData)
    {
        if (world.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
        {
            return be.getModelData();
        }
        return tileData;
    }



    protected abstract Tuple<BlockState, BlockState> getDummyStates();

    protected Tuple<BakedModel, BakedModel> getModels()
    {
        if (models == null)
        {
            if (dummyStates == null) { dummyStates = getDummyStates(); }

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
    protected TextureAtlasSprite getSpriteOrDefault(ModelData data, ModelProperty<ModelData> key, BakedModel model)
    {
        ModelData innerData = data.get(key);
        if (innerData != null)
        {
            FramedBlockData fbData = innerData.get(FramedBlockData.PROPERTY);
            if (fbData != null && !fbData.getCamoState().isAir())
            {
                return model.getParticleIcon(innerData);
            }
        }

        //noinspection deprecation
        return baseModel.getParticleIcon();
    }

    private static List<BakedQuad> invertTintIndizes(List<BakedQuad> quads)
    {
        return quads.stream()
                .map(ModelUtils::invertTintIndex)
                .collect(Collectors.toList());
    }
}