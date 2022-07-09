package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.FramedDoubleSlabBlockEntity;

public class FramedDoubleSlabModel extends FramedDoubleBlockModel
{
    @SuppressWarnings("unused")
    public FramedDoubleSlabModel(BlockState state, BakedModel baseModel) { super(baseModel, false); }

    @Override
    protected Tuple<BlockState, BlockState> getDummyStates() { return FramedDoubleSlabBlockEntity.getBlockPair(); }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data)
    {
        return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getB());
    }
}