package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedLeverModel extends FramedBlockModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(FramedBlocks.MODID, "block/framed_block");

    public FramedLeverModel(BlockType type, IBakedModel baseModel) { super(type, baseModel); }

    @Override
    protected BakedQuad buildQuad(BakedQuad baseQuad, BakedQuad camoQuad)
    {
        if (!baseQuad.getSprite().getName().equals(TEXTURE))
        {
            return ModelUtils.duplicateQuad(baseQuad);
        }
        return super.buildQuad(baseQuad, camoQuad);
    }
}