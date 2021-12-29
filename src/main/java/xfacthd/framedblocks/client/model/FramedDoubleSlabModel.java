package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoubleSlabModel extends FramedDoubleBlockModel
{
    @SuppressWarnings("unused")
    public FramedDoubleSlabModel(BlockState state, BakedModel baseModel) { super(baseModel, false); }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates()
    {
        BlockState slabState = FBContent.blockFramedSlab.get().defaultBlockState();
        return new Tuple<>(slabState.setValue(PropertyHolder.TOP, false), slabState.setValue(PropertyHolder.TOP, true));
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getA());
    }
}