package io.github.xfacthd.framedblocks.client.model.template;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.OverlayPartGenerator;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.template.TemplateOverlayProvider;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

sealed class BasicTemplatedGeometry extends Geometry permits AppendingTemplatedGeometry {
    private final QuadSpec[][] quadSpecs;
    private final boolean useBaseModel;
    private final boolean forceUngeneratedBaseModel;
    private final boolean solidNoCamoModel;
    private final boolean transformAllQuads;
    @Nullable
    private final TemplateOverlayProvider overlay;

    BasicTemplatedGeometry(GeometryTemplateSpecEntry geoSpec, QuadSpec[][] quadSpecs, boolean transformAllQuads, @Nullable TemplateOverlayProvider overlay) {
        this.quadSpecs = quadSpecs;
        this.useBaseModel = geoSpec.useBaseModel();
        this.forceUngeneratedBaseModel = geoSpec.forceUngeneratedBaseModel();
        this.solidNoCamoModel = geoSpec.solidNoCamoModel();
        this.transformAllQuads = transformAllQuads;
        this.overlay = overlay;
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        for (QuadSpec spec : quadSpecs[quad.direction().ordinal()]) {
            QuadModifier quadModifier = QuadModifier.of(quad);
            for (QuadModifier.Modifier modifier : spec.modifiers()) {
                quadModifier = quadModifier.apply(modifier);
            }
            quadModifier.export(quadMap, spec.cullFace());
        }
    }

    @Override
    public boolean hasGeneratedOverlay(FramedBlockData blockData, @Nullable Object cacheKeyUserData) {
        return overlay != null && overlay.hasGeneratedOverlay(blockData);
    }

    @Override
    public void generateOverlayParts(OverlayPartGenerator generator, RandomSource rand, @Nullable Object cacheKeyUserData) {
        Objects.requireNonNull(overlay).generateOverlayParts(generator, rand);
    }

    @Override
    public boolean useBaseModel() {
        return useBaseModel;
    }

    @Override
    public boolean forceUngeneratedBaseModel() {
        return forceUngeneratedBaseModel;
    }

    @Override
    public boolean useSolidNoCamoModel() {
        return solidNoCamoModel;
    }

    @Override
    public boolean transformAllQuads() {
        return transformAllQuads;
    }
}
