package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public abstract class BakedModelProxy implements IBakedModel
{
    protected final IBakedModel baseModel;

    protected BakedModelProxy(IBakedModel baseModel) { this.baseModel = baseModel; }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        return baseModel.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() { return baseModel.isAmbientOcclusion(); }

    @Override
    public boolean isGui3d() { return true; }

    @Override
    public boolean func_230044_c_() { return baseModel.func_230044_c_(); }

    @Override
    public boolean isBuiltInRenderer() { return false; }

    @Override
    public TextureAtlasSprite getParticleTexture() { return baseModel.getParticleTexture(); }

    @Override
    public ItemOverrideList getOverrides() { return baseModel.getOverrides(); }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() { return baseModel.getItemCameraTransforms(); }
}