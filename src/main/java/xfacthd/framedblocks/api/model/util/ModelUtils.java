package xfacthd.framedblocks.api.model.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.model.quad.QuadData;
import xfacthd.framedblocks.api.util.ConfigView;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.mixin.client.AccessorWeightedBakedModel;

import java.util.*;
import java.util.function.Predicate;

public final class ModelUtils
{
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final ChunkRenderTypeSet SOLID = ChunkRenderTypeSet.of(RenderType.solid());
    public static final ChunkRenderTypeSet CUTOUT = ChunkRenderTypeSet.of(RenderType.cutout());
    public static final ChunkRenderTypeSet TRANSLUCENT = ChunkRenderTypeSet.of(RenderType.translucent());
    // Factor 16 is required because the relative UV of a TextureAtlasSprite is not 0-16 anymore since 1.20.2
    public static final float UV_SUBSTEP_COUNT = 16F * 8F;

    public static Direction fillNormal(QuadData data)
    {
        Vector3f v1 = data.pos(3, new Vector3f());
        Vector3f t1 = data.pos(1, new Vector3f());
        Vector3f v2 = data.pos(2, new Vector3f());
        Vector3f t2 = data.pos(0, new Vector3f());

        v1.sub(t1);
        v2.sub(t2);
        v2.cross(v1);
        v2.normalize();

        for (int vert = 0; vert < 4; vert++)
        {
            data.normal(vert, v2);
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
     * @param data The {@link QuadData} being operated on
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
            QuadData data,
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
                invert = !vAxis || (data.uv(0, 1) > data.uv(1, 1)) || (data.uv(3, 1) > data.uv(2, 1));
            }
            else if (quadDir == Direction.DOWN)
            {
                invert = !vAxis || (data.uv(0, 1) < data.uv(1, 1)) || (data.uv(3, 1) < data.uv(2, 1));
            }
            else if (!vAxis)
            {
                invert = !invert;
            }
        }

        float coordMin = Math.min(coord1, coord2);
        float coordMax = Math.max(coord1, coord2);

        int uvIdx = rotated != vAxis ? 1 : 0;

        float uvMin = Math.min(data.uv(uv1, uvIdx), data.uv(uv2, uvIdx));
        float uvMax = Math.max(data.uv(uv1, uvIdx), data.uv(uv2, uvIdx));

        if (coordTo == coordMin)
        {
            data.uv(uvTo, uvIdx,  (invert) ? uvMax : uvMin);
        }
        else if (coordTo == coordMax)
        {
            data.uv(uvTo, uvIdx,  (invert) ? uvMin : uvMax);
        }
        else
        {
            if (ConfigView.Client.INSTANCE.useDiscreteUVSteps())
            {
                float relMin = uvIdx == 0 ? sprite.getUOffset(uvMin) : sprite.getVOffset(uvMin);
                float relMax = uvIdx == 0 ? sprite.getUOffset(uvMax) : sprite.getVOffset(uvMax);

                float mult = (coordTo - coordMin) / (coordMax - coordMin);
                if (invert) { mult = 1F - mult; }

                float relTo = Mth.lerp(mult, relMin, relMax);
                relTo = Math.round(relTo * UV_SUBSTEP_COUNT) / UV_SUBSTEP_COUNT;
                data.uv(uvTo, uvIdx, uvIdx == 0 ? sprite.getU(relTo) : sprite.getV(relTo));
            }
            else
            {
                float mult = (coordTo - coordMin) / (coordMax - coordMin);
                if (invert) { mult = 1F - mult; }
                data.uv(uvTo, uvIdx, Mth.lerp(mult, uvMin, uvMax));
            }
        }
    }

    public static boolean isQuadRotated(QuadData data)
    {
        return (Mth.equal(data.uv(0, 1), data.uv(1, 1)) || Mth.equal(data.uv(3, 1), data.uv(2, 1))) &&
               (Mth.equal(data.uv(1, 0), data.uv(2, 0)) || Mth.equal(data.uv(0, 0), data.uv(3, 0)));
    }

    public static boolean isQuadMirrored(QuadData data, boolean rotated)
    {
        if (!rotated)
        {
            return (data.uv(0, 0) > data.uv(3, 0) && data.uv(1, 0) > data.uv(2, 0)) ||
                   (data.uv(0, 1) > data.uv(1, 1) && data.uv(3, 1) > data.uv(2, 1));
        }
        else
        {
            return (data.uv(0, 0) > data.uv(1, 0) && data.uv(3, 0) > data.uv(2, 0)) ||
                   (data.uv(0, 1) < data.uv(3, 1) && data.uv(1, 1) < data.uv(2, 1));
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

    public static BakedModel getModel(BlockState state)
    {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
    }

    public static ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data)
    {
        return getModel(state).getRenderTypes(state, random, data);
    }

    public static ArrayList<BakedQuad> getCullableQuads(
            BakedModel model,
            BlockState state,
            RandomSource rand,
            ModelData data,
            RenderType renderType,
            Predicate<Direction> filter
    )
    {
        if (model instanceof WeightedBakedModel weighted)
        {
            model = ((AccessorWeightedBakedModel) weighted).framedblocks$getWrappedModel();
        }

        ArrayList<BakedQuad> quads = new ArrayList<>();
        for (Direction dir : DIRECTIONS)
        {
            if (filter.test(dir))
            {
                Utils.copyAll(model.getQuads(state, dir, rand, data, renderType), quads);
            }
        }
        return quads;
    }



    private ModelUtils() { }
}
