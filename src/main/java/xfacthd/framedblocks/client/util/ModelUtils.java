package xfacthd.framedblocks.client.util;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.pipeline.LightUtil;

import java.util.Arrays;

public class ModelUtils
{
    public static BakedQuad modifyQuad(BakedQuad quad, VertexDataConsumer consumer)
    {
        int elemPos = findElement(VertexFormatElement.Usage.POSITION, 0);
        int elemColor = findElement(VertexFormatElement.Usage.COLOR, 0);
        int elemUV = findElement(VertexFormatElement.Usage.UV, 0);
        int elemLight = findElement(VertexFormatElement.Usage.UV, 2);
        int elemNormal = findElement(VertexFormatElement.Usage.NORMAL, 0);

        int[] vertexData = quad.getVertexData();

        float[][] pos = new float[4][3];
        float[][] color = new float[4][4];
        float[][] uv = new float[4][2];
        float[][] light = new float[4][2];
        float[][] normal = new float[4][3];

        for (int vert = 0; vert < 4; vert++)
        {
            LightUtil.unpack(vertexData, pos[vert], DefaultVertexFormats.BLOCK, vert, elemPos);
            LightUtil.unpack(vertexData, color[vert], DefaultVertexFormats.BLOCK, vert, elemColor);
            LightUtil.unpack(vertexData, uv[vert], DefaultVertexFormats.BLOCK, vert, elemUV);
            LightUtil.unpack(vertexData, light[vert], DefaultVertexFormats.BLOCK, vert, elemLight);
            LightUtil.unpack(vertexData, normal[vert], DefaultVertexFormats.BLOCK, vert, elemNormal);
        }

        consumer.accept(pos, color, uv, light, normal);

        for (int vert = 0; vert < 4; vert++)
        {
            LightUtil.pack(pos[vert], vertexData, DefaultVertexFormats.BLOCK, vert, elemPos);
            LightUtil.pack(color[vert], vertexData, DefaultVertexFormats.BLOCK, vert, elemColor);
            LightUtil.pack(uv[vert], vertexData, DefaultVertexFormats.BLOCK, vert, elemUV);
            LightUtil.pack(light[vert], vertexData, DefaultVertexFormats.BLOCK, vert, elemLight);
            LightUtil.pack(normal[vert], vertexData, DefaultVertexFormats.BLOCK, vert, elemNormal);
        }

        return quad;
    }

    public static float[][] unpackElement(BakedQuad quad, VertexFormatElement.Usage usage, int index)
    {
        int elemPos = findElement(usage, index);

        float[][] data = new float[4][4];
        for (int vert = 0; vert < 4; vert++)
        {
            LightUtil.unpack(quad.getVertexData(), data[vert], DefaultVertexFormats.BLOCK, vert, elemPos);
        }
        return data;
    }

    public static int findElement(VertexFormatElement.Usage usage, int index)
    {
        int idx = 0;
        for (VertexFormatElement element : DefaultVertexFormats.BLOCK.getElements())
        {
            if (element.getUsage() == usage && element.getIndex() == index)
            {
                return idx;
            }
            idx++;
        }
        throw new IllegalArgumentException("Format doesn't have a " + usage.getDisplayName() + " element");
    }

    public static BakedQuad duplicateQuad(BakedQuad quad)
    {
        int[] vertexData = quad.getVertexData();
        vertexData = Arrays.copyOf(vertexData, vertexData.length);

        return new BakedQuad(
                vertexData,
                quad.getTintIndex(),
                quad.getFace(),
                quad.getSprite(),
                quad.applyDiffuseLighting()
        );
    }

    public static Direction findHorizontalFacing(BakedQuad quad)
    {
        float[][] normal = unpackElement(quad, VertexFormatElement.Usage.NORMAL, 0);

        float nX = normal[0][0];
        float nZ = normal[0][2];

        if (nX < 0) { return Direction.WEST; }
        if (nX > 0) { return Direction.EAST; }
        if (nZ < 0) { return Direction.NORTH; }
        if (nZ > 0) { return Direction.SOUTH; }
        return Direction.DOWN;
    }

    public static boolean isFacingTowards(BakedQuad quad, Direction dir)
    {
        float[][] normal = unpackElement(quad, VertexFormatElement.Usage.NORMAL, 0);

        float nX = normal[0][0];
        float nY = normal[0][1];
        float nZ = normal[0][2];

        switch (dir)
        {
            case DOWN:
                return nY < 0;
            case UP:
                return nY > 0;
            case NORTH:
                return nZ < 0;
            case SOUTH:
                return nZ > 0;
            case WEST:
                return nX < 0;
            case EAST:
                return nX > 0;
        }
        return false;
    }

    public static void rotateElement(float[][] elem, boolean right, int count)
    {
        for (int i = 0; i < count; i++)
        {
            if (right)
            {
                float[] temp = elem[0];
                elem[0] = elem[1];
                elem[1] = elem[2];
                elem[2] = elem[3];
                elem[3] = temp;
            }
            else
            {
                float[] temp = elem[3];
                elem[3] = elem[2];
                elem[2] = elem[1];
                elem[1] = elem[0];
                elem[0] = temp;
            }
        }
    }

    public interface VertexDataConsumer
    {
        void accept(float[][] pos, float[][] color, float[][] uv, float[][] light, float[][] normal);
    }
}