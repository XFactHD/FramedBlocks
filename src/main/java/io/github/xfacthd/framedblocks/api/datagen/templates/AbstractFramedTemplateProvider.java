package io.github.xfacthd.framedblocks.api.datagen.templates;

import io.github.xfacthd.framedblocks.api.internal.InternalClientAPI;
import net.minecraft.core.Direction;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/// Base class of template providers for generating geometry templates for framed blocks.
public abstract class AbstractFramedTemplateProvider implements DataProvider {
    protected static final EnumSet<Direction> DIR_ALL = EnumSet.allOf(Direction.class);
    protected static final EnumSet<Direction> DIR_VERT = EnumSet.of(Direction.DOWN, Direction.UP);
    protected static final EnumSet<Direction> DIR_HOR = EnumSet.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    protected static final EnumSet<Direction> DIR_DOWN = EnumSet.of(Direction.DOWN);
    protected static final EnumSet<Direction> DIR_UP = EnumSet.of(Direction.UP);
    protected static final EnumSet<Direction> DIR_NORTH = EnumSet.of(Direction.NORTH);
    protected static final EnumSet<Direction> DIR_SOUTH = EnumSet.of(Direction.SOUTH);
    protected static final EnumSet<Direction> DIR_WEST = EnumSet.of(Direction.WEST);
    protected static final EnumSet<Direction> DIR_EAST = EnumSet.of(Direction.EAST);
    protected static final EnumSet<Direction> DIR_EXCEPT_DOWN = EnumSet.complementOf(DIR_DOWN);
    protected static final EnumSet<Direction> DIR_EXCEPT_UP = EnumSet.complementOf(DIR_UP);
    protected static final EnumSet<Direction> DIR_EXCEPT_NORTH = EnumSet.complementOf(DIR_NORTH);
    protected static final EnumSet<Direction> DIR_EXCEPT_SOUTH = EnumSet.complementOf(DIR_SOUTH);
    protected static final EnumSet<Direction> DIR_EXCEPT_WEST = EnumSet.complementOf(DIR_WEST);
    protected static final EnumSet<Direction> DIR_EXCEPT_EAST = EnumSet.complementOf(DIR_EAST);

    private final PackOutput.PathProvider templatePathProvider;
    private final Map<Identifier, GeometryTemplateBuilder> templates = new HashMap<>();
    private final String modId;

    /// @param output The output to generate the models into
    /// @param modId  The mod ID to generate the models for
    protected AbstractFramedTemplateProvider(PackOutput output, String modId) {
        this.templatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "framed_templates");
        this.modId = modId;
    }

    /// Registers all templates to be generated.
    protected abstract void registerTemplates();

    /// {@return a builder for a geometry template under the given ID}
    ///
    /// @param id The ID to generate the template under
    protected final GeometryTemplateBuilder template(Identifier id) {
        GeometryTemplateBuilder builder = InternalClientAPI.INSTANCE.createGeometryTemplateBuilder();
        if (templates.putIfAbsent(id, builder) != null) {
            throw new IllegalStateException("Duplicate registration of template '" + id + "'");
        }
        return builder;
    }

    @Override
    public final CompletableFuture<?> run(CachedOutput cache) {
        registerTemplates();
        return DataProvider.saveAll(cache, GeometryTemplateBuilder::toJson, templatePathProvider::json, templates);
    }

    @Override
    public final String getName() {
        return "Geometry Templates - " + modId;
    }
}
