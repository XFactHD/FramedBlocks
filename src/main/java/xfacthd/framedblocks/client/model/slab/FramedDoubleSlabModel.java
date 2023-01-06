package xfacthd.framedblocks.client.model.slab;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.client.model.FramedDoubleBlockModel;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedDoubleSlabModel extends FramedDoubleBlockModel
{
    @SuppressWarnings("unused")
    public FramedDoubleSlabModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel, false);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data)
    {
        return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getB());
    }
}