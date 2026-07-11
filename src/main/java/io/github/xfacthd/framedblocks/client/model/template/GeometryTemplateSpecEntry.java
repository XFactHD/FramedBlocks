package io.github.xfacthd.framedblocks.client.model.template;

import com.mojang.math.OctahedralGroup;
import io.github.xfacthd.framedblocks.api.model.template.CopycatPredicate;
import io.github.xfacthd.framedblocks.api.model.template.PostModifierProvider;
import io.github.xfacthd.framedblocks.api.model.template.TemplateOverlayProvider;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.List;

record GeometryTemplateSpecEntry(
        List<SourceFile> sourceFiles,
        OctahedralGroup transform,
        CopycatPredicate copycatPredicate,
        boolean useBaseModel,
        boolean forceUngeneratedBaseModel,
        boolean solidNoCamoModel,
        @Nullable PostModifierProvider postModifiers,
        @Nullable BaseModelAppenderConfig appenderConfig,
        TemplateOverlayProvider.Factory overlayFactory
) {
    public boolean isCopycatFace(Direction normal, boolean cullable) {
        return copycatPredicate.test(normal, cullable);
    }
}
