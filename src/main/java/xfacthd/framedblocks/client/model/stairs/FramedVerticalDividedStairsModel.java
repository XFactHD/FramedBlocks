package xfacthd.framedblocks.client.model.stairs;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.client.model.FramedDoubleBlockModel;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedVerticalDividedStairsModel extends FramedDoubleBlockModel
{
    public FramedVerticalDividedStairsModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel, true);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data)
    {
        return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getB());
    }
}
