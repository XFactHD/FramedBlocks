package io.github.xfacthd.framedblocks.api.model.quad;

import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

public final class QuadModifier
{
    private static final QuadModifier FAILED = new QuadModifier(null, true);

    private final QuadData data;
    private boolean failed;
    private boolean exported;

    /**
     * @return a {@code QuadModifier} for the given {@link BakedQuad}
     */
    public static QuadModifier of(BakedQuad quad)
    {
        return new QuadModifier(new QuadData(quad), false);
    }

    private QuadModifier(@UnknownNullability QuadData data, boolean failed)
    {
        this.data = data;
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
        }
        return this;
    }

    /**
     * Re-assemble the quad and add it to the given quad map under the provided cull face.
     * If any modifier failed, the quad will not be exported.
     */
    public void export(QuadMapBuilder quadMap, @Nullable Direction cullFace)
    {
        BakedQuad quad = exportDirect();
        if (quad != null)
        {
            quadMap.getOrCreate(cullFace).add(quad);
        }
    }

    @Nullable
    public BakedQuad exportDirect()
    {
        if (failed)
        {
            return null;
        }

        BakedQuad newQuad = data.toQuad();
        exported = true;
        return newQuad;
    }

    /**
     * Clone this {@code QuadModifier} to continue modifying the source quad in multiple different ways without
     * having to repeat the equivalent modification steps
     * @return a new {@code QuadModifier} with a deep-copy of the current data or an empty,
     * failed modifier if this modifier previously failed
     */
    public QuadModifier derive()
    {
        return failed ? FAILED : new QuadModifier(new QuadData(data), false);
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
