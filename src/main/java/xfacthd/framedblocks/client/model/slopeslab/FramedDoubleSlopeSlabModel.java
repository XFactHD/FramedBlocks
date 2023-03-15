package xfacthd.framedblocks.client.model.slopeslab;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.client.model.FramedDoubleBlockModel;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class FramedDoubleSlopeSlabModel extends FramedDoubleBlockModel
{
    public FramedDoubleSlopeSlabModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel, true);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData data)
    {
        return getSpriteOrDefault(data, FramedDoubleBlockEntity.DATA_RIGHT, getModels().getB());
    }

    @Override
    protected void applyInHandTransformation(PoseStack poseStack, ItemDisplayContext ctx)
    {
        poseStack.translate(0, .5, 0);
    }



    public static BlockState itemSource() { return FBContent.blockFramedDoubleSlopeSlab.get().defaultBlockState(); }
}
