package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;

public class FramedDoubleSlabModel extends FramedDoubleBlockModel
{
    @SuppressWarnings("unused")
    public FramedDoubleSlabModel(BlockState state, IBakedModel baseModel) { super(baseModel, false); }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        BlockState slabState = FBContent.blockFramedSlab.get().defaultBlockState();
        return new Tuple<>(slabState.setValue(PropertyHolder.TOP, false), slabState.setValue(PropertyHolder.TOP, true));
    }

    @Override
    public TextureAtlasSprite getParticleTexture(IModelData data)
    {
        return getSpriteOrDefault(data, FramedDoubleTileEntity.DATA_RIGHT, getModels().getA());
    }
}