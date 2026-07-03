package io.github.xfacthd.framedblocks.api.model.quad;

import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/// Wrapper around two [QuadModifier]s to simultaneously modify two copies of a quad with the same modifiers.
public final class MultiQuadModifier {
    private QuadModifier modOne;
    private QuadModifier modTwo;

    /// @param modOne The first wrapped modifier
    /// @param modTwo The second wrapped modifier
    public MultiQuadModifier(QuadModifier modOne, QuadModifier modTwo) {
        this.modOne = modOne;
        this.modTwo = modTwo;
    }

    /// Apply the given modifier to both wrapped [QuadModifier]s if `apply` is true.
    ///
    /// @see QuadModifier#applyIf(QuadModifier.Modifier, boolean)
    public MultiQuadModifier applyIf(QuadModifier.Modifier modifier, boolean apply) {
        modOne = modOne.applyIf(modifier, apply);
        modTwo = modTwo.applyIf(modifier, apply);
        return this;
    }

    /// Apply the given modifier to both wrapped [QuadModifier]s.
    ///
    /// @see QuadModifier#apply(QuadModifier.Modifier)
    public MultiQuadModifier apply(QuadModifier.Modifier modifier) {
        modOne = modOne.apply(modifier);
        modTwo = modTwo.apply(modifier);
        return this;
    }

    /// Re-assemble the quads of both wrapped [QuadModifier]s and add them to the given quad map under
    /// the provided cull face.
    ///
    /// @see QuadModifier#export(QuadMapBuilder, Direction)
    public void export(QuadMapBuilder quadMap, @Nullable Direction cullFace) {
        modOne.export(quadMap, cullFace);
        modTwo.export(quadMap, cullFace);
    }

    /// Clone the wrapped [QuadModifier]s to continue modifying their source quads in multiple different ways without
    /// having to repeat the equivalent modification steps.
    ///
    /// @return a new multi-quad modifier with a deep-copy of the current data or an empty,
    /// failed modifier if this modifier previously failed
    public MultiQuadModifier derive() {
        return new MultiQuadModifier(modOne.derive(), modTwo.derive());
    }

    /// Discard both wrapped [QuadModifier]s to return them to the pool without exporting them.
    public void discard() {
        modOne.discard();
        modTwo.discard();
    }
}
