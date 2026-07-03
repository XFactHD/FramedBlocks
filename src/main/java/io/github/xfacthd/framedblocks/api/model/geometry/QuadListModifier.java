package io.github.xfacthd.framedblocks.api.model.geometry;

import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;

/// Represents a function for modifying a list of quads from a specific cullface before
/// they are transferred into the quad map backing the resulting model part.
@FunctionalInterface
public interface QuadListModifier {
    /// Modify the quads for a particular cullface before they are copied to the quad map.
    ///
    /// If the implementation manually copies any quads to the provided quad map, then it must
    /// remove the affected quads from the provided list, otherwise they will be copied to the
    /// quad map again by the calling code.
    ///
    /// @param quadMap The quad map that will contain the final quads
    /// @param quads   The list of quads for the cullface currently being operated on
    /// @param side    The side currently being operated on
    void modify(QuadMapBuilder quadMap, ArrayList<BakedQuad> quads, @Nullable Direction side);

    /// {@return a modifier removing quads of cullfaces for which the filter returns `true`}
    ///
    /// @param filter The cullface filter
    static QuadListModifier filteringCullFace(Predicate<Direction> filter) {
        return (_, quads, side) -> {
            if (side != null && filter.test(side)) {
                quads.clear();
            }
        };
    }

    /// {@return a modifier removing quads matching the given filter}
    ///
    /// @param filter The quad filter
    static QuadListModifier filtering(Predicate<BakedQuad> filter) {
        return (_, quads, _) -> quads.removeIf(filter);
    }

    /// {@return a modifier changing and/or removing quads with the given function}
    ///
    /// @param modifier The function to modify the quads with
    static QuadListModifier replacing(Function<BakedQuad, @Nullable BakedQuad> modifier) {
        return (_, quads, _) -> {
            ListIterator<BakedQuad> it = quads.listIterator();
            while (it.hasNext()) {
                BakedQuad newQuad = modifier.apply(it.next());
                if (newQuad != null) {
                    it.set(newQuad);
                } else {
                    it.remove();
                }
            }
        };
    }
}
