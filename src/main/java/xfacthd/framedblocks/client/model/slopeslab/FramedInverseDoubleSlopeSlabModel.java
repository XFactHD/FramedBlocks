package xfacthd.framedblocks.client.model.slopeslab;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.client.model.FramedDoubleBlockModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedInverseDoubleSlopeSlabModel extends FramedDoubleBlockModel
{
    public FramedInverseDoubleSlopeSlabModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel, true);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull IModelData data)
    {
        return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getB());
    }



    public static BlockState itemSource() { return FBContent.blockFramedInverseDoubleSlopeSlab.get().defaultBlockState(); }
}
