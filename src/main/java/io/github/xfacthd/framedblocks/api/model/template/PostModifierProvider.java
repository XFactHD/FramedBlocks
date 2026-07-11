package io.github.xfacthd.framedblocks.api.model.template;

import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/// Provides additional quad modifiers which are applied after any given quad is cut to
/// fit the geometry loaded from the template.
public interface PostModifierProvider {
    /// Collect additional modifiers to apply to quads of the given normal direction and cullability status.
    ///
    /// @param normal    The closest direction to the quad's normal vector
    /// @param cullable  Whether the quad is cullable
    /// @param collector The consumer to pass additional modifiers to
    void collectPostModifiers(Direction normal, boolean cullable, Consumer<QuadModifier.Modifier> collector);

    /// Adjust the cullface under which quads of the given normal direction are exported.
    ///
    /// @param normal   The closest direction to the quad's normal vector
    /// @param cullface The original cullface as determined from the template
    /// @return the adjusted cullface
    default @Nullable Direction transformCullFace(Direction normal, @Nullable Direction cullface) {
        return cullface;
    }
}
