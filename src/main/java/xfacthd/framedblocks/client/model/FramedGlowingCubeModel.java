package xfacthd.framedblocks.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.LightUtil;
import xfacthd.framedblocks.api.util.client.ModelUtils;

import java.util.*;

public class FramedGlowingCubeModel extends FramedCubeModel
{
    public FramedGlowingCubeModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData)
    {
        List<BakedQuad> quads = super.getQuads(state, side, rand, extraData);
        return applyFullbright(quads);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
    {
        List<BakedQuad> quads = super.getQuads(state, side, rand);
        return applyFullbright(quads);
    }

    @Override
    public boolean useAmbientOcclusion() { return false; }



    private static List<BakedQuad> applyFullbright(List<BakedQuad> quads)
    {
        List<BakedQuad> fullbrightQuads = new ArrayList<>(quads.size());
        float[] light = new float[2];
        quads.forEach(quad ->
        {
            int[] vertexData = quad.getVertices();
            vertexData = Arrays.copyOf(vertexData, vertexData.length);
            BakedQuad newQuad = new BakedQuad(
                    vertexData,
                    quad.getTintIndex(),
                    quad.getDirection(),
                    quad.getSprite(),
                    false
            );

            for (int i = 0; i < 4; i++)
            {
                LightUtil.unpack(vertexData, light, DefaultVertexFormat.BLOCK, i, ModelUtils.ELEMENT_LIGHT);
                light[0] = light[1] = (float)LightTexture.FULL_BLOCK / (float)Short.MAX_VALUE;
                LightUtil.pack(light, vertexData, DefaultVertexFormat.BLOCK, i, ModelUtils.ELEMENT_LIGHT);
            }

            fullbrightQuads.add(newQuad);
        });
        return fullbrightQuads;
    }
}
