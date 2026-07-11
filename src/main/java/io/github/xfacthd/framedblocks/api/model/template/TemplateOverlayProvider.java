package io.github.xfacthd.framedblocks.api.model.template;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.geometry.OverlayPartGenerator;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

/// Specifies how to generate an additional overlay over the transformed geometry.
public interface TemplateOverlayProvider {
    /// {@return whether a block with the given data has an overlay}
    ///
    /// @param blockData The data applied to the framed block
    boolean hasGeneratedOverlay(FramedBlockData blockData);

    /// Generate the additional overlay with the givne generator and RNG.
    ///
    /// @param generator The generator to use for generating the overlay
    /// @param rand      The RNG to use for generating the overlay
    void generateOverlayParts(OverlayPartGenerator generator, RandomSource rand);

    /// Functional interface for creating an overlay provider for the given context.
    @FunctionalInterface
    interface Factory {
        /// {@return the overlay provider to apply for the givent context}
        ///
        /// @param ctx The context in which the overlay provider is requested
        @Nullable TemplateOverlayProvider create(GeometryFactory.Context ctx);
    }
}
