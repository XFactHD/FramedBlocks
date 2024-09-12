package xfacthd.framedblocks.api.model.quad;

import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.List;

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

    public MultiQuadModifier tintIndex(int tintIndex)
    {
        modOne.tintIndex(tintIndex);
        modTwo.tintIndex(tintIndex);
        return this;
    }

    public MultiQuadModifier shade(boolean shade)
    {
        modOne.shade(shade);
        modTwo.shade(shade);
        return this;
    }

    public MultiQuadModifier ambientOcclusion(boolean ao)
    {
        modOne.ambientOcclusion(ao);
        modTwo.ambientOcclusion(ao);
        return this;
    }

    /**
     * Re-assemble the quads of both wrapped {@link QuadModifier}s and add them to the given quad list.
     * @see QuadModifier#export(List)
     */
    public void export(List<BakedQuad> quadList)
    {
        modOne.export(quadList);
        modTwo.export(quadList);
    }

    /**
     * Re-assemble the quads of both wrapped {@link QuadModifier}s, modifying the vertex data of the input quad directly.
     * @see QuadModifier#modifyInPlace()
     */
    public void modifyInPlace()
    {
        modOne.modifyInPlace();
        modTwo.modifyInPlace();
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
