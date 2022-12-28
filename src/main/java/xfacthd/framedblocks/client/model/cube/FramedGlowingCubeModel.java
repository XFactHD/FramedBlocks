package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.data.ModelData;

import java.util.*;

public class FramedGlowingCubeModel extends FramedCubeModel
{
    public FramedGlowingCubeModel(BlockState state, BakedModel baseModel) { super(state, baseModel); }

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

    @Override
    public boolean useAmbientOcclusion() { return false; }



    private static List<BakedQuad> applyFullbright(List<BakedQuad> quads)
    {
        List<BakedQuad> fullbrightQuads = new ArrayList<>(quads.size());
        quads.forEach(quad ->
        {
            int[] vertexData = quad.getVertices();
            BakedQuad newQuad = new BakedQuad(
                    Arrays.copyOf(vertexData, vertexData.length),
                    quad.getTintIndex(),
                    quad.getDirection(),
                    quad.getSprite(),
                    false
            );
            QuadTransformers.settingMaxEmissivity().processInPlace(newQuad);
            fullbrightQuads.add(newQuad);
        });
        return fullbrightQuads;
    }
}
