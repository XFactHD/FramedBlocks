package io.github.xfacthd.framedblocks.api.model.geometry;

import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface QuadListModifier
{
    /**
     * Allows modifying the quads for a particular side before they are copied to the {@link QuadMapBuilder}.
     * <p>
     * If the implementation manually copies any quads to the provided {@link QuadMapBuilder}, then it must
     * remove the affected quads from the provided list, otherwise they will be copied to the
     * {@link QuadMapBuilder} by the calling code.
     *
     * @param quadMap The {@link QuadMapBuilder} that will contain the final quads
     * @param quads   The list of quads for the side currently being operated on
     * @param side    The side currently being operated on
     */
    void modify(QuadMapBuilder quadMap, ArrayList<BakedQuad> quads, @Nullable Direction side);



    static QuadListModifier filteringCullFace(Predicate<Direction> filter)
    {
        return (quadMap, quads, side) ->
        {
            if (side != null && filter.test(side)) quads.clear();
        };
    }

    static QuadListModifier filtering(Predicate<BakedQuad> filter)
    {
        return (quadMap, quads, side) -> quads.removeIf(filter);
    }

    static QuadListModifier replacing(Function<BakedQuad, @Nullable BakedQuad> modifier)
    {
        return (quadMap, quads, side) ->
        {
            ListIterator<BakedQuad> it = quads.listIterator();
            while (it.hasNext())
            {
                BakedQuad newQuad = modifier.apply(it.next());
                if (newQuad != null)
                {
                    it.set(newQuad);
                }
                else
                {
                    it.remove();
                }
            }
        };
    }
}
