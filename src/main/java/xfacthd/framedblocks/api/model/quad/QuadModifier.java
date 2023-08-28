package xfacthd.framedblocks.api.model.quad;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
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
        float[][] pos = new float[4][3];
        float[][] uv = new float[4][2];
        float[][] normal = new float[4][3];

        int[] vertexData = quad.getVertices();
        for (int i = 0; i < 4; i++)
        {
            ModelUtils.unpackPosition(vertexData, pos[i], i);
            ModelUtils.unpackUV(vertexData, uv[i], i);
            ModelUtils.unpackNormals(vertexData, normal[i], i);
        }

        return new QuadModifier(new Data(quad, pos, uv, normal), true, -1, false, false, false);
    }

    /**
     * @return a {@code QuadModifier} for the given {@link BakedQuad} that can modify all vertex elements
     */
    public static QuadModifier full(BakedQuad quad)
    {
        float[][] pos = new float[4][3];
        float[][] uv = new float[4][2];
        float[][] normal = new float[4][3];
        int[][] color = new int[4][4];
        int[][] light = new int[4][2];

        int[] vertexData = quad.getVertices();
        for (int i = 0; i < 4; i++)
        {
            ModelUtils.unpackPosition(vertexData, pos[i], i);
            ModelUtils.unpackUV(vertexData, uv[i], i);
            ModelUtils.unpackNormals(vertexData, normal[i], i);
            ModelUtils.unpackColor(vertexData, color[i], i);
            ModelUtils.unpackLight(vertexData, light[i], i);
        }

        return new QuadModifier(new Data(quad, pos, uv, normal, color, light), false, -1, false, false, false);
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

        Direction normalDir = ModelUtils.fillNormal(data.pos, data.normal);

        int[] vertexData = data.quad.getVertices();
        vertexData = Arrays.copyOf(vertexData, vertexData.length);
        packVertexData(vertexData);

        BakedQuad newQuad = new BakedQuad(
                vertexData,
                tintIndex == -1 ? data.quad.getTintIndex() : tintIndex,
                normalDir,
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

        ModelUtils.fillNormal(data.pos, data.normal);
        packVertexData(data.quad.getVertices());
    }

    private void packVertexData(int[] vertexData)
    {
        for (int i = 0; i < 4; i++)
        {
            ModelUtils.packPosition(data.pos[i], vertexData, i);
            ModelUtils.packUV(data.uv[i], vertexData, i);
            ModelUtils.packNormals(data.normal[i], vertexData, i);

            if (data.color != null)
            {
                ModelUtils.packColor(data.color[i], vertexData, i);
            }
            if (data.light != null)
            {
                ModelUtils.packLight(data.light[i], vertexData, i);
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
        return new QuadModifier(new Data(data), limited, tintIndex, noShade, modified, false);
    }

    public boolean hasFailed()
    {
        return failed;
    }



    public record Data(
            BakedQuad quad,
            float[][] pos,
            float[][] uv,
            float[][] normal,
            int[][] color,
            int[][] light,
            boolean uvRotated,
            boolean uvMirrored
    )
    {
        public Data(BakedQuad quad, float[][] pos, float[][] uv, float[][] normal, int[][] color, int[][] light)
        {
            this(quad, pos, uv, normal, color, light, ModelUtils.isQuadRotated(uv), ModelUtils.isQuadMirrored(uv));
        }

        public Data(BakedQuad quad, float[][] pos, float[][] uv, float[][] normal)
        {
            this(quad, pos, uv, normal, null, null);
        }

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
