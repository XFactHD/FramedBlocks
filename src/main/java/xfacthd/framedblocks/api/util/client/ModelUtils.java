package xfacthd.framedblocks.api.util.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.util.FramedBlockData;
import xfacthd.framedblocks.api.util.Utils;

import java.util.*;

public final class ModelUtils
{
    public static final ChunkRenderTypeSet SOLID = ChunkRenderTypeSet.of(RenderType.solid());
    public static final ChunkRenderTypeSet CUTOUT = ChunkRenderTypeSet.of(RenderType.cutout());

    public static boolean modifyQuad(BakedQuad quad, VertexDataConsumer consumer)
    {
        int[] vertexData = quad.getVertices();

        float[][] pos = new float[4][3];
        float[][] uv = new float[4][2];
        float[][] normal = new float[4][3];

        for (int vert = 0; vert < 4; vert++)
        {
            unpackPosition(vertexData, pos[vert], vert);
            unpackUV(vertexData, uv[vert], vert);
            unpackNormals(vertexData, normal[vert], vert);
        }

        boolean success = consumer.accept(pos, uv, normal);
        if (!success) { return false; }

        for (int vert = 0; vert < 4; vert++)
        {
            packPosition(pos[vert], vertexData, vert);
            packUV(uv[vert], vertexData, vert);
            packNormals(normal[vert], vertexData, vert);
        }

        fillNormal(quad);

        return true;
    }

    public static void unpackPosition(int[] vertexData, float[] pos, int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        pos[0] = Float.intBitsToFloat(vertexData[offset]);
        pos[1] = Float.intBitsToFloat(vertexData[offset + 1]);
        pos[2] = Float.intBitsToFloat(vertexData[offset + 2]);
    }

    public static void unpackUV(int[] vertexData, float[] uv, int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        uv[0] = Float.intBitsToFloat(vertexData[offset]);
        uv[1] = Float.intBitsToFloat(vertexData[offset + 1]);
    }

    public static void unpackNormals(int[] vertexData, float[] normal, int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.NORMAL;
        int packedNormal = vertexData[offset];

        normal[0] = ((byte) (packedNormal & 0xFF)) / 127F;
        normal[1] = ((byte) ((packedNormal >> 8) & 0xFF)) / 127F;
        normal[2] = ((byte) ((packedNormal >> 16) & 0xFF)) / 127F;
    }

    public static void unpackColor(int[] vertexData, int[] color, int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.COLOR;
        int packedColor = vertexData[offset];

        color[0] = packedColor & 0xFF;
        color[1] = (packedColor >> 8) & 0xFF;
        color[2] = (packedColor >> 16) & 0xFF;
        color[3] = (packedColor >> 24) & 0xFF;
    }

    public static void unpackLight(int[] vertexData, int[] light, int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV2;
        int packedLight = vertexData[offset];

        light[0] = packedLight & 0xFFFF;
        light[1] = (packedLight >> 16) & 0xFFFF;
    }

    public static void packPosition(float[] pos, int[] vertexData, int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        vertexData[offset    ] = Float.floatToRawIntBits(pos[0]);
        vertexData[offset + 1] = Float.floatToRawIntBits(pos[1]);
        vertexData[offset + 2] = Float.floatToRawIntBits(pos[2]);
    }

    public static void packUV(float[] uv, int[] vertexData, int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        vertexData[offset    ] = Float.floatToRawIntBits(uv[0]);
        vertexData[offset + 1] = Float.floatToRawIntBits(uv[1]);
    }

    public static void packNormals(float[] normal, int[] vertexData, int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.NORMAL;

        int packedNormal = vertexData[offset];
        vertexData[offset] =
                (((byte)  (normal[0] * 127F)) & 0xFF) |
                ((((byte) (normal[1] * 127F)) & 0xFF) << 8) |
                ((((byte) (normal[2] * 127F)) & 0xFF) << 16) |
                (packedNormal & 0xFF000000);
    }

    public static void packColor(int[] color, int[] vertexData, int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.COLOR;

        vertexData[offset] = ( color[0] & 0xFF) |
                             ((color[1] & 0xFF) << 8) |
                             ((color[2] & 0xFF) << 16) |
                             ((color[3] & 0xFF) << 24);
    }

    public static void packLight(int[] light, int[] vertexData, int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV2;
        vertexData[offset] = (light[0] & 0xFFFF) | ((light[1] & 0xFFFF) << 16);
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
        float[][] pos = new float[4][3];
        for (int i = 0; i < 4; i++)
        {
            unpackPosition(quad.getVertices(), pos[i], i);
        }

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
        for (int vert = 0; vert < 4; vert++)
        {
            vertexData[vert * IQuadTransformer.STRIDE + IQuadTransformer.NORMAL] = normal;
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
            else if (Utils.isPositive(quadDir) != vAxis)
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
            uvDest[uvTo][uvIdx] = Mth.lerp(mult, uvMin, uvMax);
        }
    }

    public static boolean isQuadRotated(float[][] uv)
    {
        return (Mth.equal(uv[0][1], uv[1][1]) || Mth.equal(uv[3][1], uv[2][1])) &&
               (Mth.equal(uv[1][0], uv[2][0]) || Mth.equal(uv[0][0], uv[3][0]));
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

    public static ModelData getCamoModelData(BakedModel model, BlockState state, ModelData data)
    {
        BlockAndTintGetter level = data.get(FramedBlockData.LEVEL);
        BlockPos pos = data.get(FramedBlockData.POS);

        if (level == null || pos == null || pos == BlockPos.ZERO) { return ModelData.EMPTY; }

        return model.getModelData(level, pos, state, data);
    }

    public static List<BakedQuad> getAllCullableQuads(BakedModel model, BlockState state, RandomSource rand, RenderType renderType)
    {
        List<BakedQuad> quads = new ArrayList<>();
        for (Direction dir : Direction.values())
        {
            quads.addAll(model.getQuads(state, dir, rand, ModelData.EMPTY, renderType));
        }
        return quads;
    }



    public interface VertexDataConsumer
    {
        boolean accept(float[][] pos, float[][] uv, float[][] normal);
    }



    private ModelUtils() { }
}