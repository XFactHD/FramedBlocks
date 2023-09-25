package xfacthd.framedblocks.api.model.quad;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.client.model.IQuadTransformer;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.model.util.ModelUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class QuadModifier
{
    private static final QuadModifier FAILED = new QuadModifier(null, true, -1, false, false, true);
    private static final QuadModifier FAILED_FULL = new QuadModifier(null, false, -1, false, false, true);

    private final Data data;
    private final boolean limited;
    private int tintIndex;
    private boolean noShade;
    private boolean modified;
    private boolean failed;

    /**
     * @return a {@code QuadModifier} for the given {@link BakedQuad} that can only modify vertex position, texture and normals
     */
    public static QuadModifier geometry(BakedQuad quad)
    {
        return new QuadModifier(new Data(quad), true, -1, false, false, false);
    }

    /**
     * @return a {@code QuadModifier} for the given {@link BakedQuad} that can modify all vertex elements
     */
    public static QuadModifier full(BakedQuad quad)
    {
        return new QuadModifier(new Data(quad), false, -1, false, false, false);
    }

    private QuadModifier(Data data, boolean limited, int tintIndex, boolean noShade, boolean modified, boolean failed)
    {
        this.data = data;
        this.limited = limited;
        this.tintIndex = tintIndex;
        this.noShade = noShade;
        this.modified = modified;
        this.failed = failed;
    }

    /**
     * Apply the given {@link Modifier} to the current vertex data if {@code apply} is true. If a previous modifier
     * failed, then the modification will not be applied
     */
    public QuadModifier applyIf(Modifier modifier, boolean apply)
    {
        return apply ? apply(modifier) : this;
    }

    /**
     * Apply the given {@link Modifier} to the current vertex data. If a previous modifier failed,
     * then the modification will not be applied
     */
    public QuadModifier apply(Modifier modifier)
    {
        if (!failed)
        {
            failed = !modifier.accept(data);
            modified = true;
        }
        return this;
    }

    public QuadModifier tintIndex(int tintIndex)
    {
        Preconditions.checkState(this.tintIndex == -1, "TintIndex has already been set");

        this.tintIndex = tintIndex;
        modified = true;
        return this;
    }

    public QuadModifier noShade()
    {
        noShade = true;
        modified = true;
        return this;
    }

    /**
     * Re-assemble the quad and add it to the given quad list. If any of modifier failed,
     * the quad will not be exported
     */
    public void export(List<BakedQuad> quadList)
    {
        export(quadList::add);
    }

    /**
     * Re-assemble a copy of the quad and export it to the given quad consumer. If any of the modifiers failed,
     * the quad will not be exported
     */
    public void export(Consumer<BakedQuad> quadConsumer)
    {
        if (failed)
        {
            return;
        }

        if (!modified)
        {
            quadConsumer.accept(data.quad);
            return;
        }

        BakedQuad newQuad = new BakedQuad(
                data.vertexData,
                tintIndex == -1 ? data.quad.getTintIndex() : tintIndex,
                ModelUtils.fillNormal(data),
                data.quad.getSprite(),
                !noShade && data.quad.isShade(),
                data.quad.hasAmbientOcclusion()
        );
        quadConsumer.accept(newQuad);
    }

    /**
     * Re-assemble the quad, modifying the vertex data of the input quad directly. If any modifiers failed,
     * the quad will not be modified
     */
    public void modifyInPlace()
    {
        Preconditions.checkState(tintIndex == -1, "In-place modification can't change tintIndex but a tintIndex has been set");
        Preconditions.checkState(!noShade, "In-place modification can't change shading but noShade has been set");

        if (failed)
        {
            return;
        }

        ModelUtils.fillNormal(data);
        System.arraycopy(data.vertexData, 0, data.quad.getVertices(), 0, data.vertexData.length);
    }

    /**
     * Clone this {@code QuadModifier} to continue modifying the source quad in multiple different ways without
     * having to repeat the equivalent modification steps
     * @return a new {@code QuadModifier} with a deep-copy of the current data or an empty,
     * failed modifier if this modifier previously failed
     */
    public QuadModifier derive()
    {
        if (failed)
        {
            return limited ? FAILED : FAILED_FULL;
        }
        return new QuadModifier(new Data(data), limited, tintIndex, noShade, modified, false);
    }

    public boolean hasFailed()
    {
        return failed;
    }



    public static final class Data
    {
        private final BakedQuad quad;
        private final int[] vertexData;
        private final boolean uvRotated;
        private final boolean uvMirrored;

        public Data(BakedQuad quad)
        {
            this.quad = quad;
            int[] vertexData = quad.getVertices();
            this.vertexData = Arrays.copyOf(vertexData, vertexData.length);
            this.uvRotated = ModelUtils.isQuadRotated(this);
            this.uvMirrored = ModelUtils.isQuadMirrored(this, uvRotated);
        }

        private Data(Data data)
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
            out[startIdx    ] = Float.intBitsToFloat(vertexData[offset]);
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
            vertexData[offset    ] = Float.floatToRawIntBits(x);
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
            out[startIdx    ] = Float.intBitsToFloat(vertexData[offset]);
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
            vertexData[offset    ] = Float.floatToRawIntBits(u);
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
                    (((byte)  (vals.x * 127F)) & 0xFF) |
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

    @FunctionalInterface
    public interface Modifier
    {
        boolean accept(Data data);
    }
}
