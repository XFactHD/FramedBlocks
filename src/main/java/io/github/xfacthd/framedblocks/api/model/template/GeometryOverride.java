package io.github.xfacthd.framedblocks.api.model.template;

import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/// Provides additional sets of quad modifiers to produce additional geometry separate from the template or
/// replace certain faces of the template entirely.
public interface GeometryOverride {
    /// {@return whether template faces of the given normal and cullability are entirely replaced}
    ///
    /// @param normal   The normal direction of the template face
    /// @param cullable Whether the template face is cullable
    boolean replacesFace(Direction normal, boolean cullable);

    /// Append additional geometry.
    ///
    /// @param output The geometry output
    void collectModifiers(Output output);

    @ApiStatus.NonExtendable
    interface Output {
        /// Add a set of modifiers resulting in an additional quad.
        /// The individual modifiers are passed to the consumer given to the `modifierAppender`.
        ///
        /// @param normal           The normal direction of the quad
        /// @param cullable         Whether the resulting quad should be cullable
        /// @param modifierAppender A consumer accepting a modifier sink
        void accept(Direction normal, boolean cullable, Consumer<Consumer<QuadModifier.Modifier>> modifierAppender);
    }
}
