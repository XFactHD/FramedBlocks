package xfacthd.framedblocks.client.util;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.pipeline.LightUtil;

import java.util.Arrays;

public class ModelUtils
{
    private static final int ELEMENT_POS = findElement(DefaultVertexFormats.ELEMENT_POSITION);
    private static final int ELEMENT_COLOR = findElement(DefaultVertexFormats.ELEMENT_COLOR);
    private static final int ELEMENT_UV = findElement(DefaultVertexFormats.ELEMENT_UV0);
    private static final int ELEMENT_LIGHT = findElement(DefaultVertexFormats.ELEMENT_UV2);
    private static final int ELEMENT_NORMAL = findElement(DefaultVertexFormats.ELEMENT_NORMAL);

    public static boolean modifyQuad(BakedQuad quad, VertexDataConsumer consumer)
    {
        int[] vertexData = quad.getVertices();

        float[][] pos = new float[4][3];
        float[][] color = new float[4][4];
        float[][] uv = new float[4][2];
        float[][] light = new float[4][2];
        float[][] normal = new float[4][3];

        for (int vert = 0; vert < 4; vert++)
        {
            LightUtil.unpack(vertexData, pos[vert], DefaultVertexFormats.BLOCK, vert, ELEMENT_POS);
            LightUtil.unpack(vertexData, color[vert], DefaultVertexFormats.BLOCK, vert, ELEMENT_COLOR);
            LightUtil.unpack(vertexData, uv[vert], DefaultVertexFormats.BLOCK, vert, ELEMENT_UV);
            LightUtil.unpack(vertexData, light[vert], DefaultVertexFormats.BLOCK, vert, ELEMENT_LIGHT);
            LightUtil.unpack(vertexData, normal[vert], DefaultVertexFormats.BLOCK, vert, ELEMENT_NORMAL);
        }

        boolean accept = consumer.accept(pos, color, uv, light, normal);
        if (!accept) { return false; }

        for (int vert = 0; vert < 4; vert++)
        {
            LightUtil.pack(pos[vert], vertexData, DefaultVertexFormats.BLOCK, vert, ELEMENT_POS);
            LightUtil.pack(color[vert], vertexData, DefaultVertexFormats.BLOCK, vert, ELEMENT_COLOR);
            LightUtil.pack(uv[vert], vertexData, DefaultVertexFormats.BLOCK, vert, ELEMENT_UV);
            LightUtil.pack(light[vert], vertexData, DefaultVertexFormats.BLOCK, vert, ELEMENT_LIGHT);
            LightUtil.pack(normal[vert], vertexData, DefaultVertexFormats.BLOCK, vert, ELEMENT_NORMAL);
        }

        fillNormal(quad);

        return true;
    }

    public static float[][] unpackElement(BakedQuad quad, VertexFormatElement element)
    {
        int elemPos = findElement(element);

        float[][] data = new float[4][4];
        for (int vert = 0; vert < 4; vert++)
        {
            LightUtil.unpack(quad.getVertices(), data[vert], DefaultVertexFormats.BLOCK, vert, elemPos);
        }
        return data;
    }

    public static int findElement(VertexFormatElement targetElement)
    {
        int idx = 0;
        for (VertexFormatElement element : DefaultVertexFormats.BLOCK.getElements())
        {
            if (element == targetElement)
            {
                return idx;
            }
            idx++;
        }
        throw new IllegalArgumentException("Format doesn't have a " + targetElement + " element");
    }

    public static BakedQuad duplicateQuad(BakedQuad quad)
    {
        int[] vertexData = quad.getVertices();
        vertexData = Arrays.copyOf(vertexData, vertexData.length);

        return new BakedQuad(
                vertexData,
                quad.getTintIndex(),
                quad.getDirection(),
                quad.getSprite(),
                quad.isShade()
        );
    }

    /**
     * Calculate face normals from vertex positions
     * Adapted from {@link net.minecraftforge.client.ForgeHooksClient#fillNormal(int[], Direction)}
     */
    public static void fillNormal(BakedQuad quad)
    {
        float[][] pos = unpackElement(quad, DefaultVertexFormats.ELEMENT_POSITION);

        Vector3f v1 = new Vector3f(pos[3][0], pos[3][1], pos[3][2]);
        Vector3f t1 = new Vector3f(pos[1][0], pos[1][1], pos[1][2]);
        Vector3f v2 = new Vector3f(pos[2][0], pos[2][1], pos[2][2]);
        Vector3f t2 = new Vector3f(pos[0][0], pos[0][1], pos[0][2]);

        v1.sub(t1);
        v2.sub(t2);
        v2.cross(v1);
        v2.normalize();

        int x = ((byte) Math.round(v2.x() * 127)) & 0xFF;
        int y = ((byte) Math.round(v2.y() * 127)) & 0xFF;
        int z = ((byte) Math.round(v2.z() * 127)) & 0xFF;

        int normal = x | (y << 0x08) | (z << 0x10);

        int[] vertexData = quad.getVertices();
        int step = vertexData.length / 4; //This is needed to support the extended vertex formats used by shaders in OptiFine
        for(int vert = 0; vert < 4; vert++)
        {
            vertexData[vert * step + 7] = normal;
        }
    }

    /**
     * Maps a coordinate 'coordTo' between the given coordinates 'coord1' and 'coord2'
     * onto the UV range they occupy as given by the values at 'uv1' and 'uv2' in the 'uv'
     * array, calculates the target UV coordinate corresponding to the value of 'coordTo'
     * and places it at 'uvTo' in the 'uv' array
     * @param quadDir The direction the quad is facing in
     * @param coord1 The first coordinate
     * @param coord2 The second coordinate
     * @param coordTo The target coordinate, must lie between coord1 and coord2
     * @param uv The UV data (modified in place)
     * @param uv1 The first UV texture coordinate
     * @param uv2 The second UV texture coordinate
     * @param vAxis Wether the modification should happen on the V axis or the U axis
     * @param invert Wether the coordinates grow in the opposite direction of the texture coordinates
     * @param rotated Wether the UVs are rotated
     * @param mirrored Wether the UVs are mirrored
     */
    public static void remapUV(Direction quadDir, float coord1, float coord2, float coordTo, float[][] uv, int uv1, int uv2, int uvTo, boolean vAxis, boolean invert, boolean rotated, boolean mirrored)
    {
        remapUV(quadDir, coord1, coord2, coordTo, uv, uv, uv1, uv2, uvTo, vAxis, invert, rotated, mirrored);
    }

    /**
     * Maps a coordinate 'coordTo' between the given coordinates 'coord1' and 'coord2'
     * onto the UV range they occupy as given by the values at 'uv1' and 'uv2' in the 'uv'
     * array, calculates the target UV coordinate corresponding to the value of 'coordTo'
     * and places it at 'uvTo' in the 'uv' array
     * @param quadDir The direction the quad is facing in
     * @param coord1 The first coordinate
     * @param coord2 The second coordinate
     * @param coordTo The target coordinate, must lie between coord1 and coord2
     * @param uvSrc The source UV data (not modified)
     * @param uvDest The UV data to modify (modified in place)
     * @param uv1 The first UV texture coordinate
     * @param uv2 The second UV texture coordinate
     * @param vAxis Wether the modification should happen on the V axis or the U axis
     * @param invert Wether the coordinates grow in the opposite direction of the texture coordinates
     * @param rotated Wether the UVs are rotated
     * @param mirrored Wether the UVs are mirrored
     */
    public static void remapUV(Direction quadDir, float coord1, float coord2, float coordTo, float[][] uvSrc, float[][] uvDest, int uv1, int uv2, int uvTo, boolean vAxis, boolean invert, boolean rotated, boolean mirrored)
    {
        if (rotated)
        {
            if (quadDir == Direction.UP)
            {
                invert = vAxis == mirrored;
            }
            else if (quadDir == Direction.DOWN)
            {
                invert = !mirrored;
            }
            else if ((quadDir.getAxisDirection() == Direction.AxisDirection.POSITIVE) != vAxis)
            {
                invert = !invert;
            }
        }
        else if (mirrored)
        {
            if (quadDir == Direction.UP)
            {
                invert = !vAxis || (uvSrc[0][1] > uvSrc[1][1]);
            }
            else if (quadDir == Direction.DOWN)
            {
                invert = !vAxis || (uvSrc[0][1] < uvSrc[1][1]);
            }
            else if (!vAxis)
            {
                invert = !invert;
            }
        }

        float coordMin = Math.min(coord1, coord2);
        float coordMax = Math.max(coord1, coord2);

        int uvIdx = rotated != vAxis ? 1 : 0;

        float uvMin = Math.min(uvSrc[uv1][uvIdx], uvSrc[uv2][uvIdx]);
        float uvMax = Math.max(uvSrc[uv1][uvIdx], uvSrc[uv2][uvIdx]);

        if (coordTo == coordMin)
        {
            uvDest[uvTo][uvIdx] = (invert) ? uvMax : uvMin;
        }
        else if (coordTo == coordMax)
        {
            uvDest[uvTo][uvIdx] = (invert) ? uvMin : uvMax;
        }
        else
        {
            float mult = (coordTo - coordMin) / (coordMax - coordMin);
            if (invert) { mult = 1F - mult; }
            uvDest[uvTo][uvIdx] = MathHelper.lerp(mult, uvMin, uvMax);
        }
    }

    public static boolean isQuadRotated(float[][] uv)
    {
        return (MathHelper.equal(uv[0][1], uv[1][1]) || MathHelper.equal(uv[3][1], uv[2][1])) &&
                (MathHelper.equal(uv[1][0], uv[2][0]) || MathHelper.equal(uv[0][0], uv[3][0]));
    }

    public static boolean isQuadMirrored(float[][] uv)
    {
        boolean rotated = isQuadRotated(uv);
        if (!rotated)
        {
            return (uv[0][0] > uv[3][0] && uv[1][0] > uv[2][0]) ||
                    (uv[0][1] > uv[1][1] && uv[3][1] > uv[2][1]);
        }
        else
        {
            return (uv[0][0] > uv[1][0] && uv[3][0] > uv[2][0]) ||
                    (uv[0][1] < uv[3][1] && uv[1][1] < uv[2][1]);
        }
    }

    /**
     * Creates a shallow copy of the given BakedQuad in order to invert the tint index for BakedQuads
     * used by the second model of a double block
     * @apiNote The vertex data of the returned BakedQuad is not a copy, it must not be modified later!
     */
    public static BakedQuad invertTintIndex(BakedQuad quad)
    {
        return new BakedQuad(
                quad.getVertices(), //Don't need to copy the vertex data, it won't be modified by the caller
                encodeSecondaryTintIndex(quad.getTintIndex()),
                quad.getDirection(),
                quad.getSprite(),
                quad.isShade()
        );
    }

    public static int encodeSecondaryTintIndex(int tintIndex) { return (tintIndex + 2) * -1; }

    public static int decodeSecondaryTintIndex(int tintIndex) { return (tintIndex * -1) - 2; }



    public interface VertexDataConsumer
    {
        boolean accept(float[][] pos, float[][] color, float[][] uv, float[][] light, float[][] normal);
    }
}