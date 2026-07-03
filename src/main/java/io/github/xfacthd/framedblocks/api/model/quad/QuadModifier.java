package io.github.xfacthd.framedblocks.api.model.quad;

import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.CheckReturnValue;
import org.jspecify.annotations.Nullable;

/// Manages modification of baked quads through a builder-style pattern.
public sealed class QuadModifier permits QuadModifierPool.LeakDetectingQuadModifier {
    private static final QuadModifier FAILED = new QuadModifier(true);

    private final ExtMutableQuad mutableQuad = new ExtMutableQuad();
    private final boolean failed;
    boolean retired = false;

    /// {@return a quad modifier for the given quad}
    ///
    /// @param quad The quad to modify
    public static QuadModifier of(BakedQuad quad) {
        QuadModifier modifier = QuadModifierPool.acquire();
        modifier.mutableQuad.setFrom(quad);
        return modifier;
    }

    QuadModifier(boolean failed) {
        this.failed = failed;
    }

    /// Apply the given [Modifier] to the current vertex data if `apply` is true. If a previous modifier
    /// failed, then the modification will not be applied.
    ///
    /// @param modifier The modifier to apply
    /// @param apply    Whether the modifier should be applied
    /// @return this quad modifier or the failed quad modifier if modifier application failed
    @CheckReturnValue
    public QuadModifier applyIf(Modifier modifier, boolean apply) {
        return apply ? apply(modifier) : this;
    }

    /// Apply the given [Modifier] to the current vertex data. If a previous modifier failed,
    /// then the modification will not be applied.
    ///
    /// @param modifier The modifier to apply
    /// @return this quad modifier or the failed quad modifier if modifier application failed
    @CheckReturnValue
    public QuadModifier apply(Modifier modifier) {
        ensureValid();
        if (!failed && !modifier.accept(mutableQuad)) {
            QuadModifierPool.release(this);
            return FAILED;
        }
        return this;
    }

    /// Re-assemble the quad and add it to the given quad map under the provided cull face.
    /// If any modifier failed, the quad will not be exported.
    ///
    /// @param quadMap  The quad map to add the modified quad to
    /// @param cullFace The cullface under which to add the quad
    public void export(QuadMapBuilder quadMap, @Nullable Direction cullFace) {
        BakedQuad quad = exportDirect();
        if (quad != null) {
            quadMap.getOrCreate(cullFace).add(quad);
        }
    }

    /// {@return the re-assembled quad or null if any modifier failed}
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

    /// Clone this quad modifier to continue modifying the source quad in multiple different ways without
    /// having to repeat the equivalent modification steps.
    ///
    /// @return a new quad modifier with a deep-copy of the current data or the failed quad modifier any modifier failed
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

    /// Discard this `QuadModifier` to return it to the pool without exporting it.
    public void discard() {
        ensureValid();
        if (!failed) {
            QuadModifierPool.release(this);
        }
    }

    /// {@return whether this modifier has failed}
    public boolean isFailed() {
        return failed;
    }

    private void ensureValid() {
        if (retired) {
            throw new IllegalStateException("QuadModifier has been retired, no further modifications allowed!");
        }
    }

    /// Functional interface representing a modifier to apply to a quad.
    @FunctionalInterface
    public interface Modifier {
        /// Modify the given quad and return whether the modification succeeded.
        /// If the modification failed, then the given quad must not have been modified.
        ///
        /// @param quad The quad to modify
        /// @return whether the modification succeeded
        boolean accept(ExtMutableQuad quad);
    }
}
