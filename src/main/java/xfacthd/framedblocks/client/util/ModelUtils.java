package xfacthd.framedblocks.client.util;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
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

        BakedQuad dupeQuad = new BakedQuad(
                vertexData,
                quad.getTintIndex(),
                quad.getFace(),
                quad.getSprite(),
                quad.applyDiffuseLighting()
        );

        ModelUtils.modifyQuad(dupeQuad, (pos, color, uv, light, normal) -> unmirrorUVs(uv));
        return dupeQuad;
    }

    /**
     * Unmirrors the UV coordinates if needed. This is the case for eg. grass and sand blocks
     */
    private static void unmirrorUVs(float[][] uv)
    {
        if (uv[0][0] > uv[2][0])
        {
            float temp = uv[0][0];
            uv[0][0] = uv[2][0];
            uv[2][0] = temp;
        }
        if (uv[1][0] > uv[3][0])
        {
            float temp = uv[1][0];
            uv[1][0] = uv[3][0];
            uv[3][0] = temp;
        }

        if (uv[0][1] > uv[2][1])
        {
            float temp = uv[0][1];
            uv[0][1] = uv[2][1];
            uv[2][1] = temp;
        }
        if (uv[3][1] > uv[1][1])
        {
            float temp = uv[1][1];
            uv[1][1] = uv[3][1];
            uv[3][1] = temp;
        }
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

        return switch (dir)
        {
            case DOWN -> nY < 0;
            case UP -> nY > 0;
            case NORTH -> nZ < 0;
            case SOUTH -> nZ > 0;
            case WEST -> nX < 0;
            case EAST -> nX > 0;
        };
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

    /**
     * Maps a x/z coordinate 'xzto' between the given x/z coordinates 'xzf1' and 'xzf2'
     * onto the u range they occupy as given by 'u1' and 'u2' and calculates the
     * u value corresponding to the value of 'xzto'
     * @param xzf1 The left X/Z coordinate
     * @param xzf2 The right X/Z coordinate
     * @param xzto The target X/Z coordinate, must lie between xzf1 and xzf2
     * @param u1 The left U texture coordinate
     * @param u2 The right U texture coordinate
     * @param invert Wether the coordinates grow in the opposite direction of the texture coordinates (true for quads pointing north or east)
     */
    public static float remapU(float xzf1, float xzf2, float xzto, float u1, float u2, boolean invert)
    {
        float xzMin = Math.min(xzf1, xzf2);
        float xzMax = Math.max(xzf1, xzf2);

        float uMin = Math.min(u1, u2);
        float uMax = Math.max(u1, u2);

        if (xzto == xzMin) { return invert ? uMax : uMin; }
        if (xzto == xzMax) { return invert ? uMin : uMax; }

        float mult = (xzto - xzMin) / (xzMax - xzMin);
        if (invert) { mult = 1F - mult; }
        return MathHelper.lerp(mult, uMin, uMax);
    }

    /**
     * Maps a y coordinate 'yto' between the given y coordinates 'yf1' and 'yf2'
     * onto the v range they occupy as given by 'v1' and 'v2' and calculates the
     * v value corresponding to the value of 'yto'
     * @param yf1 The bottom Y coordinate
     * @param yf2 The top Y coordinate
     * @param yto The target Y coordinate, must lie between yf1 and yf2
     * @param v1 The bottom V texture coordinate
     * @param v2 The top V texture coordinate
     * @param invert Wether the coordinates grow in the opposite direction of the texture coordinates (true for all quads except those pointing up)
     */
    public static float remapV(float yf1, float yf2, float yto, float v1, float v2, boolean invert)
    {
        float yMin = Math.min(yf1, yf2);
        float yMax = Math.max(yf1, yf2);

        float vMin = Math.min(v1, v2);
        float vMax = Math.max(v1, v2);

        if (yto == yMin) { return invert ? vMax : vMin; }
        if (yto == yMax) { return invert ? vMin : vMax; }

        float mult = (yto - yMin) / (yMax - yMin);
        if (invert) { mult = 1F - mult; }
        return MathHelper.lerp(mult, vMin, vMax);
    }



    public interface VertexDataConsumer
    {
        void accept(float[][] pos, float[][] color, float[][] uv, float[][] light, float[][] normal);
    }
}