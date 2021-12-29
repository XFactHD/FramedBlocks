package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.*;
import xfacthd.framedblocks.client.util.FramedBlockData;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class FramedDoubleBlockModel extends BakedModelProxy
{
    private final boolean specialItemModel;
    private Tuple<BlockState, BlockState> dummyStates = null;
    private Tuple<IBakedModel, IBakedModel> models = null;

    protected FramedDoubleBlockModel(IBakedModel baseModel, boolean specialItemModel)
    {
        super(baseModel);
        this.specialItemModel = specialItemModel;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        if (dummyStates == null) { dummyStates = getDummyStates(); }
        if (models == null) { models = getModels(); }

        IModelData dataLeft = extraData.getData(FramedDoubleTileEntity.DATA_LEFT);
        List<BakedQuad> quads = new ArrayList<>(
                getModels().getA().getQuads(dummyStates.getA(), side, rand, dataLeft != null ? dataLeft : EmptyModelData.INSTANCE)
        );

        IModelData dataRight = extraData.getData(FramedDoubleTileEntity.DATA_RIGHT);
        quads.addAll(invertTintIndizes(
                getModels().getB().getQuads(dummyStates.getB(), side, rand, dataRight != null ? dataRight : EmptyModelData.INSTANCE)
        ));

        return quads;
    }

    @Override
    @Deprecated
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
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
    @SuppressWarnings({"deprecation", "ConstantConditions"})
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data)
    {
        IModelData innerData = data.getData(FramedDoubleTileEntity.DATA_LEFT);
        if (innerData != null && !innerData.getData(FramedBlockData.CAMO).isAir())
        {
            return getModels().getA().getParticleTexture(innerData);
        }
        innerData = data.getData(FramedDoubleTileEntity.DATA_RIGHT);
        if (innerData != null && !innerData.getData(FramedBlockData.CAMO).isAir())
        {
            return getModels().getB().getParticleTexture(innerData);
        }
        return baseModel.getParticleTexture();
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof FramedDoubleTileEntity)
        {
            return te.getModelData();
        }
        return tileData;
    }



    protected abstract Tuple<BlockState, BlockState> getDummyStates();

    protected Tuple<IBakedModel, IBakedModel> getModels()
    {
        if (models == null)
        {
            if (dummyStates == null) { dummyStates = getDummyStates(); }

            BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
            models = new Tuple<>(
                    dispatcher.getModelForState(dummyStates.getA()),
                    dispatcher.getModelForState(dummyStates.getB())
            );
        }
        return models;
    }

    /**
     * Returns the camo-dependent particle texture of the side given by {@code key} when the camo is not air,
     * else returns the basic "framed block" sprite
     */
    protected TextureAtlasSprite getSpriteOrDefault(IModelData data, ModelProperty<IModelData> key, IBakedModel model)
    {
        IModelData innerData = data.getData(key);
        //noinspection ConstantConditions
        if (innerData != null && !innerData.getData(FramedBlockData.CAMO).isAir())
        {
            return model.getParticleTexture(innerData);
        }

        //noinspection deprecation
        return baseModel.getParticleTexture();
    }

    private List<BakedQuad> invertTintIndizes(List<BakedQuad> quads)
    {
        return quads.stream()
                .map(ModelUtils::invertTintIndex)
                .collect(Collectors.toList());
    }
}