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
import net.minecraftforge.common.util.ConcatenatedListView;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.model.BakedModelProxy;
import xfacthd.framedblocks.api.util.FramedBlockData;
import xfacthd.framedblocks.api.util.client.ModelUtils;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class FramedDoubleBlockModel extends BakedModelProxy
{
    private final boolean specialItemModel;
    private final Tuple<BlockState, BlockState> dummyStates;
    private Tuple<BakedModel, BakedModel> models = null;

    public FramedDoubleBlockModel(BlockState state, BakedModel baseModel, boolean specialItemModel)
    {
        super(baseModel);
        this.dummyStates = AbstractFramedDoubleBlock.getStatePair(state);
        this.specialItemModel = specialItemModel;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, RenderType layer)
    {
        List<List<BakedQuad>> quads = new ArrayList<>(2);
        Tuple<BakedModel, BakedModel> models = getModels();

        ModelData dataLeft = extraData.get(FramedDoubleBlockEntity.DATA_LEFT);
        quads.add(
                models.getA().getQuads(dummyStates.getA(), side, rand, dataLeft != null ? dataLeft : ModelData.EMPTY, layer)
        );

        ModelData dataRight = extraData.get(FramedDoubleBlockEntity.DATA_RIGHT);
        quads.add(invertTintIndizes(
                models.getB().getQuads(dummyStates.getB(), side, rand, dataRight != null ? dataRight : ModelData.EMPTY, layer)
        ));

        return ConcatenatedListView.of(quads);
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand)
    {
        if (specialItemModel)
        {
            Tuple<BakedModel, BakedModel> models = getModels();

            List<List<BakedQuad>> quads = new ArrayList<>(2);
            quads.add(models.getA().getQuads(dummyStates.getA(), side, rand));
            quads.add(models.getB().getQuads(dummyStates.getB(), side, rand));
            return ConcatenatedListView.of(quads);
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
    public ModelData getModelData(@Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData)
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
    protected final TextureAtlasSprite getSpriteOrDefault(ModelData data, ModelProperty<ModelData> key, BakedModel model)
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
        List<BakedQuad> inverted = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads)
        {
            inverted.add(ModelUtils.invertTintIndex(quad));
        }
        return inverted;
    }
}