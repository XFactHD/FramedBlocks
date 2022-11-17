package xfacthd.framedblocks.api.model.quad;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.client.model.pipeline.LightUtil;
import xfacthd.framedblocks.api.util.client.ModelUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class QuadModifier
{
    private static final QuadModifier FAILED = new QuadModifier(null, true, -1, false, true);
    private static final QuadModifier FAILED_FULL = new QuadModifier(null, false, -1, false, true);

    private final Data data;
    private final boolean limited;
    private int tintIndex = -1;
    private boolean noShade;
    private boolean failed;

    /**
     * @return a {@code QuadModifier} for the given {@link BakedQuad} that can only modify vertex position, texture and normals
     */
    public static QuadModifier geometry(BakedQuad quad)
    {
        float[][] pos = new float[4][3];
        float[][] uv = new float[4][2];
        float[][] normal = new float[4][3];

        int[] vertexData = quad.getVertices();
        for (int i = 0; i < 4; i++)
        {
            LightUtil.unpack(vertexData, pos[i], DefaultVertexFormat.BLOCK, i, ModelUtils.ELEMENT_POS);
            LightUtil.unpack(vertexData, uv[i], DefaultVertexFormat.BLOCK, i, ModelUtils.ELEMENT_UV);
            LightUtil.unpack(vertexData, normal[i], DefaultVertexFormat.BLOCK, i, ModelUtils.ELEMENT_NORMAL);
        }

        return new QuadModifier(new Data(quad, pos, uv, normal), true, -1, false, false);
    }

    /**
     * @return a {@code QuadModifier} for the given {@link BakedQuad} that can modify all vertex elements
     */
    public static QuadModifier full(BakedQuad quad)
    {
        float[][] pos = new float[4][3];
        float[][] uv = new float[4][2];
        float[][] normal = new float[4][3];
        float[] colorf = new float[4];
        int[][] color = new int[4][4];
        int[][] light = new int[4][2];

        int[] vertexData = quad.getVertices();
        for (int i = 0; i < 4; i++)
        {
            LightUtil.unpack(vertexData, pos[i], DefaultVertexFormat.BLOCK, i, ModelUtils.ELEMENT_POS);
            LightUtil.unpack(vertexData, uv[i], DefaultVertexFormat.BLOCK, i, ModelUtils.ELEMENT_UV);
            LightUtil.unpack(vertexData, normal[i], DefaultVertexFormat.BLOCK, i, ModelUtils.ELEMENT_NORMAL);
            LightUtil.unpack(vertexData, colorf, DefaultVertexFormat.BLOCK, i, ModelUtils.ELEMENT_COLOR);

            color[i][0] = (int)colorf[0];
            color[i][1] = (int)colorf[1];
            color[i][2] = (int)colorf[2];
            color[i][3] = (int)colorf[3];

            int offset = i * ModelUtils.QUAD_STRIDE + ModelUtils.OFFSET_LIGHT;
            int packedLight = vertexData[offset];
            light[i][0] = LightTexture.block(packedLight);
            light[i][1] = LightTexture.sky(packedLight);
        }

        return new QuadModifier(new Data(quad, pos, uv, normal, color, light), false, -1, false, false);
    }

    private QuadModifier(Data data, boolean limited, int tintIndex, boolean noShade, boolean failed)
    {
        this.data = data;
        this.limited = limited;
        this.tintIndex = tintIndex;
        this.noShade = noShade;
        this.failed = failed;
    }

    /**
     * Apply the given {@link Modifier} to the current vertex data if {@code apply} is true. If a previous modifier
     * failed, then the modification will not be applied
     */
    public QuadModifier applyIf(Modifier modifier, boolean apply) { return apply ? apply(modifier) : this; }

    /**
     * Apply the given {@link Modifier} to the current vertex data. If a previous modifier failed,
     * then the modification will not be applied
     */
    public QuadModifier apply(Modifier modifier)
    {
        if (!failed)
        {
            failed = !modifier.accept(data);
        }
        return this;
    }

    public QuadModifier tintIndex(int tintIndex)
    {
        Preconditions.checkState(this.tintIndex == -1, "TintIndex has already been set");

        this.tintIndex = tintIndex;
        return this;
    }

    public QuadModifier noShade()
    {
        noShade = true;
        return this;
    }

    /**
     * Re-assemble the quad and add it to the given quad list. If any of modifier failed,
     * the quad will not be exported
     */
    public void export(List<BakedQuad> quadList) { export(quadList::add); }

    /**
     * Re-assemble a copy of the quad and export it to the given quad consumer. If any of the modifiers failed,
     * the quad will not be exported
     */
    public void export(Consumer<BakedQuad> quadConsumer)
    {
        if (failed) { return; }

        int[] vertexData = data.quad.getVertices();
        vertexData = Arrays.copyOf(vertexData, vertexData.length);
        packVertexData(vertexData);

        BakedQuad newQuad = new BakedQuad(
                vertexData,
                tintIndex == -1 ? data.quad.getTintIndex() : tintIndex,
                data.quad.getDirection(),
                data.quad.getSprite(),
                !noShade && data.quad.isShade()
        );
        ModelUtils.fillNormal(newQuad);
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

        if (failed) { return; }

        packVertexData(data.quad.getVertices());
        ModelUtils.fillNormal(data.quad);
    }

    private void packVertexData(int[] vertexData)
    {
        float[] colorf = new float[4];

        for (int i = 0; i < 4; i++)
        {
            LightUtil.pack(data.pos[i], vertexData, DefaultVertexFormat.BLOCK, i, ModelUtils.ELEMENT_POS);
            LightUtil.pack(data.uv[i], vertexData, DefaultVertexFormat.BLOCK, i, ModelUtils.ELEMENT_UV);
            LightUtil.pack(data.normal[i], vertexData, DefaultVertexFormat.BLOCK, i, ModelUtils.ELEMENT_NORMAL);

            if (data.color != null)
            {
                colorf[0] = (float)data.color[i][0];
                colorf[1] = (float)data.color[i][1];
                colorf[2] = (float)data.color[i][2];
                colorf[3] = (float)data.color[i][3];

                LightUtil.pack(colorf, vertexData, DefaultVertexFormat.BLOCK, i, ModelUtils.ELEMENT_COLOR);
            }
            if (data.light != null)
            {
                int offset = i * ModelUtils.QUAD_STRIDE + ModelUtils.OFFSET_LIGHT;
                vertexData[offset] = LightTexture.pack(data.light[i][0], data.light[i][1]);
            }
        }
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
        return new QuadModifier(new Data(data), limited, tintIndex, noShade, false);
    }

    public boolean hasFailed() { return failed; }



    public record Data(BakedQuad quad, float[][] pos, float[][] uv, float[][] normal, int[][] color, int[][] light)
    {
        public Data(BakedQuad quad, float[][] pos, float[][] uv, float[][] normal)
        {
            this(quad, pos, uv, normal, null, null);
        }

        @SuppressWarnings("CopyConstructorMissesField")
        public Data(Data data)
        {
            this(data.quad,
                 deepCopy(data.pos),
                 deepCopy(data.uv),
                 deepCopy(data.normal),
                 data.color != null ? deepCopy(data.color) : null,
                 data.light != null ? deepCopy(data.light) : null
            );
        }

        private static float[][] deepCopy(float[][] arr)
        {
            float[][] newArr = new float[arr.length][];
            for (int i = 0; i < arr.length; i++)
            {
                newArr[i] = Arrays.copyOf(arr[i], arr[i].length);
            }
            return newArr;
        }

        private static int[][] deepCopy(int[][] arr)
        {
            int[][] newArr = new int[arr.length][];
            for (int i = 0; i < arr.length; i++)
            {
                newArr[i] = Arrays.copyOf(arr[i], arr[i].length);
            }
            return newArr;
        }
    }

    @FunctionalInterface
    public interface Modifier
    {
        boolean accept(Data data);
    }
}
