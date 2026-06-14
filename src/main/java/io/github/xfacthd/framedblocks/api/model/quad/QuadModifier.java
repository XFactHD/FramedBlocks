package io.github.xfacthd.framedblocks.api.model.quad;

import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.CheckReturnValue;
import org.jspecify.annotations.Nullable;

public sealed class QuadModifier permits QuadModifierPool.LeakDetectingQuadModifier {
    private static final QuadModifier FAILED = new QuadModifier(true);

    private final ExtMutableQuad mutableQuad = new ExtMutableQuad();
    private final boolean failed;
    boolean retired = false;

    /**
     * @return a {@code QuadModifier} for the given {@link BakedQuad}
     */
    public static QuadModifier of(BakedQuad quad) {
        QuadModifier modifier = QuadModifierPool.acquire();
        modifier.mutableQuad.setFrom(quad);
        return modifier;
    }

    QuadModifier(boolean failed) {
        this.failed = failed;
    }

    /**
     * Apply the given {@link Modifier} to the current vertex data if {@code apply} is true. If a previous modifier
     * failed, then the modification will not be applied
     */
    @CheckReturnValue
    public QuadModifier applyIf(Modifier modifier, boolean apply) {
        return apply ? apply(modifier) : this;
    }

    /**
     * Apply the given {@link Modifier} to the current vertex data. If a previous modifier failed,
     * then the modification will not be applied
     */
    @CheckReturnValue
    public QuadModifier apply(Modifier modifier) {
        ensureValid();
        if (!failed && !modifier.accept(mutableQuad)) {
            QuadModifierPool.release(this);
            return FAILED;
        }
        return this;
    }

    /**
     * Re-assemble the quad and add it to the given quad map under the provided cull face.
     * If any modifier failed, the quad will not be exported.
     */
    public void export(QuadMapBuilder quadMap, @Nullable Direction cullFace) {
        BakedQuad quad = exportDirect();
        if (quad != null) {
            quadMap.getOrCreate(cullFace).add(quad);
        }
    }

    public @Nullable BakedQuad exportDirect() {
        ensureValid();
        if (failed) {
            return null;
        }

        mutableQuad.recomputeNormals(true);
        BakedQuad newQuad = mutableQuad.toBakedQuad();
        QuadModifierPool.release(this);
        return newQuad;
    }

    /**
     * Clone this {@code QuadModifier} to continue modifying the source quad in multiple different ways without
     * having to repeat the equivalent modification steps
     * @return a new {@code QuadModifier} with a deep-copy of the current data or an empty,
     * failed modifier if this modifier previously failed
     */
    @CheckReturnValue
    public QuadModifier derive() {
        ensureValid();
        if (failed) {
            return FAILED;
        }

        QuadModifier modifier = QuadModifierPool.acquire();
        mutableQuad.copyInto(modifier.mutableQuad);
        return modifier;
    }

    /**
     * Discard this {@code QuadModifier} to return it to the pool without exporting it.
     */
    public void discard() {
        ensureValid();
        if (!failed) {
            QuadModifierPool.release(this);
        }
    }

    public boolean isFailed() {
        return failed;
    }

    private void ensureValid() {
        if (retired) {
            throw new IllegalStateException("QuadModifier has been retired, no further modifications allowed!");
        }
    }

    @FunctionalInterface
    public interface Modifier {
        boolean accept(ExtMutableQuad quad);
    }
}
