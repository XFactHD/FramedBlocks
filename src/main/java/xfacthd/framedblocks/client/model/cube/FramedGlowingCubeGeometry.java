package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;

import java.util.*;

public class FramedGlowingCubeGeometry extends FramedCubeGeometry
{
    private static final IQuadTransformer FULLBRIGHT_TRANSFORMER = QuadTransformers.settingMaxEmissivity();

    public FramedGlowingCubeGeometry(GeometryFactory.Context ctx)
    {
        super(ctx);
    }

    @Override
    public void postProcessUncachedQuads(List<BakedQuad> quads)
    {
        quads.replaceAll(quad ->
        {
            int[] vertexData = quad.getVertices();
            BakedQuad newQuad = new BakedQuad(
                    Arrays.copyOf(vertexData, vertexData.length),
                    quad.getTintIndex(),
                    quad.getDirection(),
                    quad.getSprite(),
                    false,
                    false
            );
            FULLBRIGHT_TRANSFORMER.processInPlace(newQuad);
            return newQuad;
        });
    }

    @Override
    public boolean hasUncachedPostProcessing()
    {
        return true;
    }

    @Override
    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType)
    {
        return TriState.FALSE;
    }
}
