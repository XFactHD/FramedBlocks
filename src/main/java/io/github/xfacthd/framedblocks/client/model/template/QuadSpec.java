package io.github.xfacthd.framedblocks.client.model.template;

import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.List;

record QuadSpec(QuadModifier.Modifier[] modifiers, @Nullable Direction cullFace) {
    QuadSpec(List<QuadModifier.Modifier> modifiers, @Nullable Direction cullFace) {
        this(modifiers.toArray(QuadModifier.Modifier[]::new), cullFace);
    }
}
