package io.github.xfacthd.framedblocks.client.model.template;

import com.google.common.base.Preconditions;
import com.mojang.math.OctahedralGroup;
import io.github.xfacthd.framedblocks.api.block.CopycatStyleBlock;
import io.github.xfacthd.framedblocks.api.model.template.CopycatPredicate;
import io.github.xfacthd.framedblocks.api.model.template.GeometryTemplateSpec;
import io.github.xfacthd.framedblocks.api.model.template.PostModifierProvider;
import io.github.xfacthd.framedblocks.api.model.template.SourceType;
import io.github.xfacthd.framedblocks.api.model.template.TemplateOverlayProvider;
import io.github.xfacthd.framedblocks.api.model.template.TemplateTransformBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

final class GeometryTemplateSpecBuilderImpl extends GeometryTemplateSpec.SpecEntryBuilder {
    private final List<SourceFile> sourceFiles = new ArrayList<>();
    private final boolean supportsCopycat;
    @Nullable
    private OctahedralGroup transform;
    private CopycatPredicate copycatPredicate;
    private boolean useBaseModel;
    private boolean forceUngeneratedBaseModel;
    private boolean solidNoCamoModel;
    private TemplateOverlayProvider.Factory overlayFactory = _ -> null;
    private @Nullable BaseModelAppenderConfig appenderConfig;
    @Nullable
    private PostModifierProvider postModifiers = null;

    GeometryTemplateSpecBuilderImpl(BlockState state) {
        this.supportsCopycat = state.getBlock() instanceof CopycatStyleBlock;
        this.copycatPredicate = supportsCopycat ? CopycatPredicate.ALWAYS : CopycatPredicate.NEVER;
    }

    @Override
    public GeometryTemplateSpecBuilderImpl addSourceFile(SourceType type, Identifier file) {
        return addSourceFile(type, file, OctahedralGroup.IDENTITY);
    }

    @Override
    public GeometryTemplateSpecBuilderImpl addSourceFile(SourceType type, Identifier file, UnaryOperator<TemplateTransformBuilder> xformBuilder) {
        return addSourceFile(type, file, xformBuilder.apply(new TemplateTransformBuilder()).build());
    }

    private GeometryTemplateSpecBuilderImpl addSourceFile(SourceType type, Identifier model, OctahedralGroup rotation) {
        this.sourceFiles.add(new SourceFile(model, type == SourceType.MODEL, rotation));
        return this;
    }

    @Override
    public GeometryTemplateSpec.SpecEntryBuilder transform(UnaryOperator<TemplateTransformBuilder> xformBuilder) {
        Preconditions.checkState(transform == null, "Transform already set");
        this.transform = xformBuilder.apply(new TemplateTransformBuilder()).build();
        return this;
    }

    @Override
    public GeometryTemplateSpecBuilderImpl copycatPredicate(CopycatPredicate predicate) {
        Preconditions.checkState(supportsCopycat, "Block %s does not support copycat style");
        this.copycatPredicate = predicate;
        return this;
    }

    @Override
    public GeometryTemplateSpecBuilderImpl useBaseModel(boolean useBaseModel) {
        this.useBaseModel = useBaseModel;
        return this;
    }

    @Override
    public GeometryTemplateSpecBuilderImpl forceUngeneratedBaseModel(boolean forceUngeneratedBaseModel) {
        this.forceUngeneratedBaseModel = forceUngeneratedBaseModel;
        return this;
    }

    @Override
    public GeometryTemplateSpecBuilderImpl solidNoCamoModel(boolean solidNoCamoModel) {
        this.solidNoCamoModel = solidNoCamoModel;
        return this;
    }

    @Override
    public GeometryTemplateSpec.SpecEntryBuilder postModifiers(PostModifierProvider postModifiers) {
        this.postModifiers = postModifiers;
        return this;
    }

    @Override
    public GeometryTemplateSpec.SpecEntryBuilder appendBaseModel(boolean includeNull, boolean cullNonNull, @Nullable BlockState shaderState) {
        this.appenderConfig = new BaseModelAppenderConfig(includeNull, cullNonNull, shaderState);
        return this;
    }

    @Override
    public GeometryTemplateSpec.SpecEntryBuilder overlay(TemplateOverlayProvider.Factory overlayFactory) {
        this.overlayFactory = overlayFactory;
        return this;
    }

    GeometryTemplateSpecEntry build() {
        Preconditions.checkState(!sourceFiles.isEmpty(), "No model specified");
        Preconditions.checkState(!forceUngeneratedBaseModel || useBaseModel, "Enabling forceUngeneratedBaseModel requires useBaseModel to be enabled");
        Preconditions.checkState(!useBaseModel || appenderConfig == null, "Cannot simultaneously modify and append the base model");
        return new GeometryTemplateSpecEntry(
                List.copyOf(sourceFiles),
                Objects.requireNonNullElse(transform, OctahedralGroup.IDENTITY),
                copycatPredicate,
                useBaseModel,
                forceUngeneratedBaseModel,
                solidNoCamoModel,
                postModifiers,
                appenderConfig,
                overlayFactory
        );
    }
}
