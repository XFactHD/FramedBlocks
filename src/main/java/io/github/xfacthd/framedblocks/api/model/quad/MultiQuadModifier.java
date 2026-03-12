package io.github.xfacthd.framedblocks.api.model.quad;

import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public final class MultiQuadModifier
{
    private final QuadModifier modOne;
    private final QuadModifier modTwo;

    public MultiQuadModifier(QuadModifier modOne, QuadModifier modTwo)
    {
        this.modOne = modOne;
        this.modTwo = modTwo;
    }

    /**
     * Apply the given {@link QuadModifier.Modifier} to both wrapped {@link QuadModifier}s if {@code apply} is true.
     * @see QuadModifier#applyIf(QuadModifier.Modifier,boolean)
     */
    public MultiQuadModifier applyIf(QuadModifier.Modifier modifier, boolean apply)
    {
        modOne.applyIf(modifier, apply);
        modTwo.applyIf(modifier, apply);
        return this;
    }

    /**
     * Apply the given {@link QuadModifier.Modifier} to both wrapped {@link QuadModifier}s.
     * @see QuadModifier#apply(QuadModifier.Modifier)
     */
    public MultiQuadModifier apply(QuadModifier.Modifier modifier)
    {
        modOne.apply(modifier);
        modTwo.apply(modifier);
        return this;
    }

    /**
     * Re-assemble the quads of both wrapped {@link QuadModifier}s and add them to the given quad map under
     * the provided cull face.
     * @see QuadModifier#export(QuadMapBuilder, Direction)
     */
    public void export(QuadMapBuilder quadMap, @Nullable Direction cullFace)
    {
        modOne.export(quadMap, cullFace);
        modTwo.export(quadMap, cullFace);
    }

    /**
     * Clone the wrapped {@link QuadModifier}s to continue modifying their source quads in multiple different ways without
     * having to repeat the equivalent modification steps
     * @return a new {@code MultiQuadModifier} with a deep-copy of the current data or an empty,
     * failed modifier if this modifier previously failed
     */
    public MultiQuadModifier derive()
    {
        return new MultiQuadModifier(modOne.derive(), modTwo.derive());
    }
}
