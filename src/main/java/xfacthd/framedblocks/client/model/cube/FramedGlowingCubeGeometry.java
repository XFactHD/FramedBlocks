package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
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
    public List<BakedQuad> postProcessUncachedQuads(List<BakedQuad> quads)
    {
        List<BakedQuad> fullbrightQuads = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads)
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
            fullbrightQuads.add(newQuad);
        }
        return fullbrightQuads;
    }
}
