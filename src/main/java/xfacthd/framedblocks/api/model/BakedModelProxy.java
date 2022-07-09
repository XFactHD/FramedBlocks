package xfacthd.framedblocks.api.model;

import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@SuppressWarnings("deprecation")
public abstract class BakedModelProxy implements BakedModel
{
    protected final BakedModel baseModel;

    protected BakedModelProxy(BakedModel baseModel) { this.baseModel = baseModel; }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand)
    {
        return baseModel.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() { return baseModel.useAmbientOcclusion(); }

    @Override
    public boolean isGui3d() { return true; }

    @Override
    public boolean usesBlockLight() { return baseModel.usesBlockLight(); }

    @Override
    public boolean isCustomRenderer() { return false; }

    @Override
    public TextureAtlasSprite getParticleIcon() { return baseModel.getParticleIcon(); }

    @Override
    public ItemOverrides getOverrides() { return baseModel.getOverrides(); }

    @Override
    public ItemTransforms getTransforms() { return baseModel.getTransforms(); }
}