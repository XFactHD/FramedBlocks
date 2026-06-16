package io.github.xfacthd.framedblocks.common.datagen.util;

import com.google.common.collect.Streams;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;

import java.util.Collection;
import java.util.stream.Stream;

// TODO: upstream
public final class ObjectTagAppender<T> implements TagAppender<T> {
    private final TagAppender<T> parent;
    private final Registry<T> registry;

    public ObjectTagAppender(TagAppender<T> parent, Registry<T> registry) {
        this.parent = parent;
        this.registry = registry;
    }

    @Override
    public ObjectTagAppender<T> add(ResourceKey<T> element) {
        parent.add(element);
        return this;
    }

    public ObjectTagAppender<T> add(T element) {
        return add(resolve(element));
    }

    @Override
    @SafeVarargs
    public final ObjectTagAppender<T> add(ResourceKey<T>... elements) {
        parent.add(elements);
        return this;
    }

    @SafeVarargs
    public final ObjectTagAppender<T> add(T... elements) {
        for (T element : elements) {
            add(element);
        }
        return this;
    }

    @Override
    public ObjectTagAppender<T> addAll(Collection<ResourceKey<T>> elements) {
        parent.addAll(elements);
        return this;
    }

    @Override
    public ObjectTagAppender<T> addAll(Stream<ResourceKey<T>> elements) {
        parent.addAll(elements);
        return this;
    }

    public ObjectTagAppender<T> addAll(Iterable<T> elements) {
        return addAll(Streams.stream(elements).map(this::resolve));
    }

    @Override
    public ObjectTagAppender<T> addOptional(ResourceKey<T> element) {
        parent.addOptional(element);
        return this;
    }

    @SafeVarargs
    public final ObjectTagAppender<T> addOptional(ResourceKey<T>... elements) {
        for (ResourceKey<T> element : elements) {
            addOptional(element);
        }
        return this;
    }

    @Override
    public ObjectTagAppender<T> addTag(TagKey<T> tag) {
        parent.addTag(tag);
        return this;
    }

    @Override
    @SafeVarargs
    public final ObjectTagAppender<T> addTags(TagKey<T>... values) {
        return this;
    }

    @Override
    public ObjectTagAppender<T> addOptionalTag(TagKey<T> tag) {
        parent.addOptionalTag(tag);
        return this;
    }

    @Override
    @SafeVarargs
    public final ObjectTagAppender<T> addOptionalTags(TagKey<T>... values) {
        parent.addOptionalTags(values);
        return this;
    }

    @Override
    public ObjectTagAppender<T> add(TagEntry entry) {
        parent.add(entry);
        return this;
    }

    @Override
    public ObjectTagAppender<T> remove(ResourceKey<T> element) {
        parent.remove(element);
        return this;
    }

    @Override
    @SafeVarargs
    public final ObjectTagAppender<T> remove(ResourceKey<T> first, ResourceKey<T>... others) {
        parent.remove(first, others);
        return this;
    }

    @Override
    public ObjectTagAppender<T> remove(TagKey<T> tag) {
        parent.remove(tag);
        return this;
    }

    @Override
    @SafeVarargs
    public final ObjectTagAppender<T> remove(TagKey<T> first, TagKey<T>... tags) {
        parent.remove(first, tags);
        return this;
    }

    @Override
    public ObjectTagAppender<T> replace() {
        parent.replace();
        return this;
    }

    @Override
    public ObjectTagAppender<T> replace(boolean value) {
        parent.replace(value);
        return this;
    }

    private ResourceKey<T> resolve(T element) {
        return registry.getResourceKey(element).orElseThrow();
    }
}
