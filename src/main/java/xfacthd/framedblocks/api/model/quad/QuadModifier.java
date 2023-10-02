package xfacthd.framedblocks.api.model.quad;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.block.model.BakedQuad;
import xfacthd.framedblocks.api.model.util.ModelUtils;

import java.util.List;
import java.util.function.Consumer;

public final class QuadModifier
{
    private static final QuadModifier FAILED = new QuadModifier(null, true, -1, false, false, true);
    private static final QuadModifier FAILED_FULL = new QuadModifier(null, false, -1, false, false, true);

    private final QuadData data;
    private final boolean limited;
    private int tintIndex;
    private boolean noShade;
    private boolean modified;
    private boolean failed;
    private boolean exported;

    /**
     * @return a {@code QuadModifier} for the given {@link BakedQuad} that can only modify vertex position, texture and normals
     */
    public static QuadModifier geometry(BakedQuad quad)
    {
        return new QuadModifier(new QuadData(quad), true, -1, false, false, false);
    }

    /**
     * @return a {@code QuadModifier} for the given {@link BakedQuad} that can modify all vertex elements
     */
    public static QuadModifier full(BakedQuad quad)
    {
        return new QuadModifier(new QuadData(quad), false, -1, false, false, false);
    }

    private QuadModifier(QuadData data, boolean limited, int tintIndex, boolean noShade, boolean modified, boolean failed)
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
        exported = true;
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
            return limited ? FAILED : FAILED_FULL;
        }
        return new QuadModifier(new QuadData(data), limited, tintIndex, noShade, modified, false);
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
