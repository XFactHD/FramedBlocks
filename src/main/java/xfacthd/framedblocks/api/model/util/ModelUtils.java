package xfacthd.framedblocks.api.model.util;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.util.Utils;

import java.lang.invoke.MethodHandle;
import java.util.*;

public final class ModelUtils
{
    public static final ChunkRenderTypeSet SOLID = ChunkRenderTypeSet.of(RenderType.solid());
    public static final ChunkRenderTypeSet CUTOUT = ChunkRenderTypeSet.of(RenderType.cutout());
    public static final ChunkRenderTypeSet TRANSLUCENT = ChunkRenderTypeSet.of(RenderType.translucent());
    private static final double UV_SUBSTEP_COUNT = 8D;

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

        light[0] = LightTexture.block(packedLight);
        light[1] = LightTexture.sky(packedLight);
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
        vertexData[offset] = LightTexture.pack(light[0], light[1]);
    }

    /**
     * Calculate face normals from vertex positions
     * Adapted from {@code net.minecraftforge.client.ForgeHooksClient#fillNormal(int[], Direction)}
     */
    @Deprecated(forRemoval = true)
    public static void fillNormal(BakedQuad quad)
    {
        int[] vertexData = quad.getVertices();
        float[][] pos = new float[4][3];
        for (int vert = 0; vert < 4; vert++)
        {
            unpackPosition(vertexData, pos[vert], vert);
        }
        float[][] normal = new float[4][3];
        fillNormal(pos, normal);
        for (int vert = 0; vert < 4; vert++)
        {
            packNormals(normal[vert], vertexData, vert);
        }
    }

    /**
     * Calculate face normals from vertex positions
     * Adapted from {@code net.minecraftforge.client.ForgeHooksClient#fillNormal(int[], Direction)}
     * @return The {@link Direction} closest to the calculated normal vector
     */
    public static Direction fillNormal(float[][] pos, float[][] normal)
    {
        Vector3f v1 = new Vector3f(pos[3][0], pos[3][1], pos[3][2]);
        Vector3f t1 = new Vector3f(pos[1][0], pos[1][1], pos[1][2]);
        Vector3f v2 = new Vector3f(pos[2][0], pos[2][1], pos[2][2]);
        Vector3f t2 = new Vector3f(pos[0][0], pos[0][1], pos[0][2]);

        v1.sub(t1);
        v2.sub(t2);
        v2.cross(v1);
        v2.normalize();

        for (int vert = 0; vert < 4; vert++)
        {
            normal[vert][0] = v2.x;
            normal[vert][1] = v2.y;
            normal[vert][2] = v2.z;
        }

        return Direction.getNearest(v2.x, v2.y, v2.z);
    }

    /**
     * Maps a coordinate 'coordTo' between the given coordinates 'coord1' and 'coord2'
     * onto the UV range they occupy as given by the values at 'uv1' and 'uv2' in the 'uv'
     * array, calculates the target UV coordinate corresponding to the value of 'coordTo'
     * and places it at 'uvTo' in the 'uv' array
     * @param quadDir The direction the quad is facing in
     * @param sprite The quad's texture
     * @param coord1 The first coordinate
     * @param coord2 The second coordinate
     * @param coordTo The target coordinate, must lie between coord1 and coord2
     * @param uv The UV data (modified in place)
     * @param uv1 The first UV texture coordinate
     * @param uv2 The second UV texture coordinate
     * @param uvTo The target UV texture coordinate
     * @param vAxis Whether the modification should happen on the V axis or the U axis
     * @param invert Whether the coordinates grow in the opposite direction of the texture coordinates
     * @param rotated Whether the UVs are rotated
     * @param mirrored Whether the UVs are mirrored
     */
    public static void remapUV(
            Direction quadDir,
            TextureAtlasSprite sprite,
            float coord1,
            float coord2,
            float coordTo,
            float[][] uv,
            int uv1,
            int uv2,
            int uvTo,
            boolean vAxis,
            boolean invert,
            boolean rotated,
            boolean mirrored
    )
    {
        remapUV(quadDir, sprite, coord1, coord2, coordTo, uv, uv, uv1, uv2, uvTo, vAxis, invert, rotated, mirrored);
    }

    /**
     * Maps a coordinate 'coordTo' between the given coordinates 'coord1' and 'coord2'
     * onto the UV range they occupy as given by the values at 'uv1' and 'uv2' in the 'uv'
     * array, calculates the target UV coordinate corresponding to the value of 'coordTo'
     * and places it at 'uvTo' in the 'uv' array
     * @param quadDir The direction the quad is facing in
     * @param sprite The quad's texture
     * @param coord1 The first coordinate
     * @param coord2 The second coordinate
     * @param coordTo The target coordinate, must lie between coord1 and coord2
     * @param uvSrc The source UV data (not modified)
     * @param uvDest The UV data to modify (modified in place)
     * @param uv1 The first UV texture coordinate
     * @param uv2 The second UV texture coordinate
     * @param uvTo The target UV texture coordinate
     * @param vAxis Whether the modification should happen on the V axis or the U axis
     * @param invert Whether the coordinates grow in the opposite direction of the texture coordinates
     * @param rotated Whether the UVs are rotated
     * @param mirrored Whether the UVs are mirrored
     */
    public static void remapUV(
            Direction quadDir,
            TextureAtlasSprite sprite,
            float coord1,
            float coord2,
            float coordTo,
            float[][] uvSrc,
            float[][] uvDest,
            int uv1,
            int uv2,
            int uvTo,
            boolean vAxis,
            boolean invert,
            boolean rotated,
            boolean mirrored
    )
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
            else if (!vAxis)
            {
                invert = invert == mirrored;
            }
        }
        else if (mirrored)
        {
            if (quadDir == Direction.UP)
            {
                invert = !vAxis || (uvSrc[0][1] > uvSrc[1][1]) || (uvSrc[3][1] > uvSrc[2][1]);
            }
            else if (quadDir == Direction.DOWN)
            {
                invert = !vAxis || (uvSrc[0][1] < uvSrc[1][1]) || (uvSrc[3][1] < uvSrc[2][1]);
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
            if (FramedBlocksClientAPI.getInstance().useDiscreteUVSteps())
            {
                double relMin = uvIdx == 0 ? sprite.getUOffset(uvMin) : sprite.getVOffset(uvMin);
                double relMax = uvIdx == 0 ? sprite.getUOffset(uvMax) : sprite.getVOffset(uvMax);

                double mult = (coordTo - coordMin) / (coordMax - coordMin);
                if (invert) { mult = 1D - mult; }

                double relTo = Mth.lerp(mult, relMin, relMax);
                relTo = Math.round(relTo * UV_SUBSTEP_COUNT) / UV_SUBSTEP_COUNT;
                uvDest[uvTo][uvIdx] = uvIdx == 0 ? sprite.getU(relTo) : sprite.getV(relTo);
            }
            else
            {
                float mult = (coordTo - coordMin) / (coordMax - coordMin);
                if (invert) { mult = 1F - mult; }
                uvDest[uvTo][uvIdx] = Mth.lerp(mult, uvMin, uvMax);
            }
        }
    }

    public static boolean isQuadRotated(float[][] uv)
    {
        return (Mth.equal(uv[0][1], uv[1][1]) || Mth.equal(uv[3][1], uv[2][1])) &&
               (Mth.equal(uv[1][0], uv[2][0]) || Mth.equal(uv[0][0], uv[3][0]));
    }

    @Deprecated(forRemoval = true)
    public static boolean isQuadMirrored(float[][] uv)
    {
        boolean rotated = isQuadRotated(uv);
        return isQuadMirrored(uv, rotated);
    }

    public static boolean isQuadMirrored(float[][] uv, boolean rotated)
    {
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

    public static int encodeSecondaryTintIndex(int tintIndex)
    {
        return (tintIndex + 2) * -1;
    }

    public static int decodeSecondaryTintIndex(int tintIndex)
    {
        return (tintIndex * -1) - 2;
    }

    public static ModelData getCamoModelData(ModelData data)
    {
        ModelData camoData = data.get(FramedBlockData.CAMO_DATA);
        return camoData != null ? camoData : ModelData.EMPTY;
    }

    private static final MethodHandle WBM_WRAPPED_MODEL = Utils.unreflectField(WeightedBakedModel.class, "f_119542_");

    public static List<BakedQuad> getAllCullableQuads(
            BakedModel model, BlockState state, RandomSource rand, ModelData data, RenderType renderType
    )
    {
        if (model instanceof WeightedBakedModel weighted)
        {
            try
            {
                // Use wrapped model for consistency and to avoid issues with invisible faces
                model = (BakedModel) WBM_WRAPPED_MODEL.invokeExact(weighted);
            }
            catch (Throwable e)
            {
                throw new RuntimeException("Failed to access field 'WeightedBakedModel#wrapped'", e);
            }
            Objects.requireNonNull(model, "Wrapped model of WeightedBakedModel is null?!");
        }

        ArrayList<BakedQuad> quads = new ArrayList<>();
        for (Direction dir : Direction.values())
        {
            List<BakedQuad> sideQuads = model.getQuads(state, dir, rand, data, renderType);
            if (sideQuads.isEmpty())
            {
                // Try extracting useful quads from the list of (supposedly) uncullable quads if querying cullable
                // ones returned nothing due to the dev forgetting to specify cull-faces in the model
                sideQuads = getFilteredNullQuads(model, state, rand, data, renderType, dir);
            }
            Utils.copyAll(sideQuads, quads);
        }
        return quads;
    }

    /**
     * Guess the cull-face of quads returned by {@link BakedModel#getQuads(BlockState, Direction, RandomSource, ModelData, RenderType)}
     * with a {@code null} side (i.e. supposedly uncullable quads) and filter them to return the ones applicable to the given
     * {@link Direction} and touching the block edge. This fixes blocks becoming invisible when mods forget to specify
     * cull-faces in their models
     * <p>
     * Heavily based on <a href="https://github.com/embeddedt/embeddium/blob/72ba934b27fa35856a0a64f3aa6c867592b2e54f/src/main/java/me/jellysquid/mods/sodium/client/model/quad/properties/ModelQuadFlags.java#L41-L115">Embeddium's quad flag calculation</a>,
     * licensed under LGPL v3
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static List<BakedQuad> getFilteredNullQuads(
            BakedModel model,
            BlockState state,
            RandomSource rand,
            ModelData data,
            @Nullable RenderType renderType,
            Direction side
    )
    {
        List<BakedQuad> nullQuads = model.getQuads(state, null, rand, data, renderType);
        if (nullQuads.isEmpty()) return Collections.emptyList();

        List<BakedQuad> filtered = new ArrayList<>();
        for (int i = 0; i < nullQuads.size(); i++)
        {
            BakedQuad quad = nullQuads.get(i);

            // Filter out quads pointing completely the wrong way early
            if (quad.getDirection() != side) continue;

            float minX = 32F;
            float minY = 32F;
            float minZ = 32F;
            float maxX = -32F;
            float maxY = -32F;
            float maxZ = -32F;

            int[] vertexData = quad.getVertices();
            for (int vert = 0; vert < 4; ++vert)
            {
                int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;

                float x = Float.intBitsToFloat(vertexData[offset]);
                float y = Float.intBitsToFloat(vertexData[offset + 1]);
                float z = Float.intBitsToFloat(vertexData[offset + 2]);

                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                minZ = Math.min(minZ, z);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
                maxZ = Math.max(maxZ, z);
            }

            boolean positive = Utils.isPositive(side);
            boolean aligned = switch(side.getAxis())
            {
                case X -> minX == maxX && (positive ? maxX > 0.9999F : minX < 0.0001F);
                case Y -> minY == maxY && (positive ? maxY > 0.9999F : minY < 0.0001F);
                case Z -> minZ == maxZ && (positive ? maxZ > 0.9999F : minZ < 0.0001F);
            };

            if (aligned)
            {
                filtered.add(quad);
            }
        }
        return filtered;
    }



    private ModelUtils() { }
}