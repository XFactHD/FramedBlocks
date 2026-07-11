package io.github.xfacthd.framedblocks.api.model.template;

import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

/// Specifies which templates a block uses and the transformations and attributes to apply to these templates.
@ApiStatus.NonExtendable
public abstract class GeometryTemplateSpec {
    /// {@return a template spec for the given block, built with the given entry builder}
    ///
    /// @param block   The block to create a template spec for
    /// @param builder The function to use for creating spec entries for the block's states
    public static GeometryTemplateSpec create(Holder<Block> block, BiConsumer<BlockState, SpecEntryBuilder> builder) {
        return InternalClientAPI.INSTANCE.createGeometryTemplateSpec(block, builder);
    }

    /// Builder for template spec entries.
    @ApiStatus.NonExtendable
    @SuppressWarnings("UnusedReturnValue")
    public static abstract class SpecEntryBuilder {
        /// Add the given source file to this spec entry.
        ///
        /// @param type The type of the source file
        /// @param file The path of the source file, relative to the type's source directory
        /// @return this builder
        public abstract SpecEntryBuilder addSourceFile(SourceType type, Identifier file);

        /// Add the given source file with the given file-specific transformation to this spec entry.
        ///
        /// @param type         The type of the source file
        /// @param file         The path of the source file, relative to the type's source directory
        /// @param xformBuilder A function for building the transformation
        /// @return this builder
        public abstract SpecEntryBuilder addSourceFile(SourceType type, Identifier file, UnaryOperator<TemplateTransformBuilder> xformBuilder);

        /// Specify a transformation to apply to all files of this entry after the file-specific transforations.
        ///
        /// @param xformBuilder A function for building the transformation
        /// @return this builder
        public abstract SpecEntryBuilder transform(UnaryOperator<TemplateTransformBuilder> xformBuilder);

        /// Specify a copycat predicate determining which faces of the resulting geometry should use copycat-style quad cutting.
        ///
        /// @param predicate The copycat predicate to apply
        /// @return this builder
        public abstract SpecEntryBuilder copycatPredicate(CopycatPredicate predicate);

        /// Specify whether the resulting geometry should use the base model instead of the generic Framed Cube
        /// when no camo is applied.
        ///
        /// @param useBaseModel Whether the base model should be used
        /// @return this builder
        public abstract SpecEntryBuilder useBaseModel(boolean useBaseModel);

        /// Specify whether the resulting geometry should use the base model with no quad modifications
        /// when no camo is applied. Requires [#useBaseModel(boolean)] to be `true`.
        ///
        /// @param forceUngeneratedBaseModel Whether the base model should be used unmodified
        /// @return this builder
        public abstract SpecEntryBuilder forceUngeneratedBaseModel(boolean forceUngeneratedBaseModel);

        /// Specify whether the resulting geometry should use a fully opaque version of the generic Framed Cube
        /// when no camo is applied.
        ///
        /// @param solidNoCamoModel Whether a fully opaque model should be used
        /// @return this builder
        public abstract SpecEntryBuilder solidNoCamoModel(boolean solidNoCamoModel);

        /// Add additional quad modifiers which are applied after any given quad is cut to
        /// fit the geometry loaded from the template.
        ///
        /// @param postModifiers A function providing the additional modifiers
        /// @return this builder
        public abstract SpecEntryBuilder postModifiers(PostModifierProvider postModifiers);

        /// Specify that the base model should be appended as additional geometry.
        ///
        /// @param includeNull Whether "uncullable" faces from the base model should be included
        /// @param cullNonNull Whether cullable faces from the base model should be culled according to the block's occlusion state
        /// @param shaderState The blockstate to pass to shader mods as the "appearance" of the model parts
        /// @return this builder
        public abstract SpecEntryBuilder appendBaseModel(boolean includeNull, boolean cullNonNull, @Nullable BlockState shaderState);

        /// Specify an additional overlay generated based on the transformed geometry.
        ///
        /// @param overlayFactory A function for creating the overlay provider
        /// @return this builder
        public abstract SpecEntryBuilder overlay(TemplateOverlayProvider.Factory overlayFactory);
    }
}
