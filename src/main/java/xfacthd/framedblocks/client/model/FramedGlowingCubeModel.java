package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.List;

public class FramedGlowingCubeModel extends FramedCubeModel
{
    private static final IQuadTransformer FULLBRIGHT_TRANSFORMER = IQuadTransformer.applyingLightmap(LightTexture.FULL_BRIGHT);

    public FramedGlowingCubeModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType)
    {
        List<BakedQuad> quads = super.getQuads(state, side, rand, extraData, renderType);
        return applyFullbright(quads);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand)
    {
        List<BakedQuad> quads = super.getQuads(state, side, rand);
        return applyFullbright(quads);
    }



    private static List<BakedQuad> applyFullbright(List<BakedQuad> quads)
    {
        List<BakedQuad> fullbrightQuads = new ArrayList<>(quads.size());
        quads.forEach(quad ->
                fullbrightQuads.add(FULLBRIGHT_TRANSFORMER.process(quad))
        );
        return fullbrightQuads;
    }
}
