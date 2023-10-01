package xfacthd.framedblocks.api.model.quad;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.client.model.IQuadTransformer;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.model.util.ModelUtils;

import java.util.Arrays;

public final class QuadData
{
    final BakedQuad quad;
    final int[] vertexData;
    private final boolean uvRotated;
    private final boolean uvMirrored;

    public QuadData(BakedQuad quad)
    {
        this.quad = quad;
        int[] vertexData = quad.getVertices();
        this.vertexData = Arrays.copyOf(vertexData, vertexData.length);
        this.uvRotated = ModelUtils.isQuadRotated(this);
        this.uvMirrored = ModelUtils.isQuadMirrored(this, uvRotated);
    }

    QuadData(QuadData data)
    {
        this.quad = data.quad;
        this.vertexData = Arrays.copyOf(data.vertexData, data.vertexData.length);
        this.uvRotated = data.uvRotated;
        this.uvMirrored = data.uvMirrored;
    }

    public BakedQuad quad()
    {
        return quad;
    }

    public boolean uvRotated()
    {
        return uvRotated;
    }

    public boolean uvMirrored()
    {
        return uvMirrored;
    }

    public float pos(int vert, int idx)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        return Float.intBitsToFloat(vertexData[offset + idx]);
    }

    public void pos(int vert, float[] out, int startIdx)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        out[startIdx] = Float.intBitsToFloat(vertexData[offset]);
        out[startIdx + 1] = Float.intBitsToFloat(vertexData[offset + 1]);
        out[startIdx + 2] = Float.intBitsToFloat(vertexData[offset + 2]);
    }

    public Vector3f pos(int vert, Vector3f out)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        out.x = Float.intBitsToFloat(vertexData[offset]);
        out.y = Float.intBitsToFloat(vertexData[offset + 1]);
        out.z = Float.intBitsToFloat(vertexData[offset + 2]);
        return out;
    }

    public void pos(int vert, int idx, float val)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        vertexData[offset + idx] = Float.floatToRawIntBits(val);
    }

    public void pos(int vert, float x, float y, float z)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        vertexData[offset] = Float.floatToRawIntBits(x);
        vertexData[offset + 1] = Float.floatToRawIntBits(y);
        vertexData[offset + 2] = Float.floatToRawIntBits(z);
    }

    public float uv(int vert, int idx)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        return Float.intBitsToFloat(vertexData[offset + idx]);
    }

    public void uv(int vert, float[] out, int startIdx)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        out[startIdx] = Float.intBitsToFloat(vertexData[offset]);
        out[startIdx + 1] = Float.intBitsToFloat(vertexData[offset + 1]);
    }

    public void uv(int vert, int idx, float val)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        vertexData[offset + idx] = Float.floatToRawIntBits(val);
    }

    public void uv(int vert, float u, float v)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        vertexData[offset] = Float.floatToRawIntBits(u);
        vertexData[offset + 1] = Float.floatToRawIntBits(v);
    }

    public float normal(int vert, int idx)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.NORMAL;
        int packedNormal = vertexData[offset];
        return ((byte) ((packedNormal >> (8 * idx)) & 0xFF)) / 127F;
    }

    public void normal(int vert, int idx, float val)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.NORMAL;
        int packedNormal = vertexData[offset];
        vertexData[offset] = ((((byte) (val * 127F)) & 0xFF) << 8 * idx) | (packedNormal & ~(0x000000FF << (8 * idx)));
    }

    public void normal(int vert, Vector3f vals)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.NORMAL;
        int packedNormal = vertexData[offset];
        vertexData[offset] =
                (((byte) (vals.x * 127F)) & 0xFF) |
                        ((((byte) (vals.y * 127F)) & 0xFF) << 8) |
                        ((((byte) (vals.z * 127F)) & 0xFF) << 16) |
                        (packedNormal & 0xFF000000);
    }

    public int color(int vert, int idx)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.COLOR;
        int packedColor = vertexData[offset];
        return (packedColor >> (8 * idx)) & 0xFF;
    }

    public void color(int vert, int idx, int val)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.COLOR;
        int packedColor = vertexData[offset];
        vertexData[offset] = ((val & 0xFF) << (8 * idx)) | (packedColor & ~(0x000000FF << (8 * idx)));
    }

    public int light(int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV2;
        return vertexData[offset];
    }

    public void light(int vert, int val)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV2;
        vertexData[offset] = val;
    }
}
