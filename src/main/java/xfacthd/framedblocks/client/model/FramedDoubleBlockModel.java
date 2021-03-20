package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.client.model.BakedModelProxy;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class FramedDoubleBlockModel extends BakedModelProxy
{
    private Tuple<BlockState, BlockState> dummyStates = null;
    private Tuple<IBakedModel, IBakedModel> models = null;

    protected FramedDoubleBlockModel(IBakedModel baseModel) { super(baseModel); }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData)
    {
        if (dummyStates == null) { dummyStates = getDummyStates(); }
        if (models == null) { models = getModels(); }

        IModelData dataLeft = extraData.getData(FramedDoubleTileEntity.DATA_LEFT);
        List<BakedQuad> quads = new ArrayList<>(
                models.getA().getQuads(dummyStates.getA(), side, rand, dataLeft != null ? dataLeft : EmptyModelData.INSTANCE)
        );

        IModelData dataRight = extraData.getData(FramedDoubleTileEntity.DATA_RIGHT);
        quads.addAll(models.getB().getQuads(dummyStates.getB(), side, rand, dataRight != null ? dataRight : EmptyModelData.INSTANCE));

        return quads;
    }

    protected abstract Tuple<BlockState, BlockState> getDummyStates();

    private Tuple<IBakedModel, IBakedModel> getModels()
    {
        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        return new Tuple<>(
                dispatcher.getModelForState(dummyStates.getA()),
                dispatcher.getModelForState(dummyStates.getB())
        );
    }
}