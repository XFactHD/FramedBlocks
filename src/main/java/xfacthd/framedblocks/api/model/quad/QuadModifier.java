package xfacthd.framedblocks.api.model.quad;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.block.model.BakedQuad;
import xfacthd.framedblocks.api.model.util.ModelUtils;

import java.util.List;

public final class QuadModifier
{
    private static final QuadModifier FAILED = new QuadModifier(null, -1, false, false, false, true);

    private final QuadData data;
    private int tintIndex;
    private boolean shade;
    private boolean ao;
    private boolean modified;
    private boolean failed;
    private boolean exported;

    /**
     * @return a {@code QuadModifier} for the given {@link BakedQuad}
     */
    public static QuadModifier of(BakedQuad quad)
    {
        return new QuadModifier(new QuadData(quad), -1, quad.isShade(), quad.hasAmbientOcclusion(), false, false);
    }

    private QuadModifier(QuadData data, int tintIndex, boolean shade, boolean ao, boolean modified, boolean failed)
    {
        this.data = data;
        this.tintIndex = tintIndex;
        this.shade = shade;
        this.ao = ao;
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
            if (exported)
            {
                throw new IllegalStateException(
                        "QuadModifier has been exported, no further modifications allowed without deriving"
                );
            }
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

    public QuadModifier shade(boolean shade)
    {
        if (shade != data.quad.isShade())
        {
            this.shade = shade;
            modified = true;
        }
        return this;
    }

    public QuadModifier ambientOcclusion(boolean ao)
    {
        if (ao != data.quad.hasAmbientOcclusion())
        {
            this.ao = ao;
            modified = true;
        }
        return this;
    }

    /**
     * Re-assemble the quad and add it to the given quad list. If any of modifier failed,
     * the quad will not be exported
     */
    public void export(List<BakedQuad> quadList)
    {
        if (failed)
        {
            return;
        }

        if (!modified)
        {
            quadList.add(data.quad);
            return;
        }

        BakedQuad newQuad = new BakedQuad(
                data.vertexData,
                tintIndex == -1 ? data.quad.getTintIndex() : tintIndex,
                ModelUtils.fillNormal(data),
                data.quad.getSprite(),
                shade,
                ao
        );
        quadList.add(newQuad);
        exported = true;
    }

    /**
     * Re-assemble the quad, modifying the vertex data of the input quad directly. If any modifiers failed,
     * the quad will not be modified
     */
    public void modifyInPlace()
    {
        Preconditions.checkState(tintIndex == -1, "In-place modification can't change tintIndex but a tintIndex has been set");
        Preconditions.checkState(shade == data.quad.isShade(), "In-place modification can't change shading but shade has been modified");
        Preconditions.checkState(ao == data.quad.hasAmbientOcclusion(), "In-place modification can't change AO but AO has been modified");

        if (failed)
        {
            return;
        }

        ModelUtils.fillNormal(data);
        System.arraycopy(data.vertexData, 0, data.quad.getVertices(), 0, data.vertexData.length);
        exported = true;
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
            return FAILED;
        }
        return new QuadModifier(new QuadData(data), tintIndex, shade, ao, modified, false);
    }

    public boolean hasFailed()
    {
        return failed;
    }



    @FunctionalInterface
    public interface Modifier
    {
        boolean accept(QuadData data);
    }
}
