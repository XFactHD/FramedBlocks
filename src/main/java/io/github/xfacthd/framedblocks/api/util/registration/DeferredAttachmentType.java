package io.github.xfacthd.framedblocks.api.util.registration;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/// Specialized deferred holder for [AttachmentType]s.
public final class DeferredAttachmentType<T> extends DeferredHolder<AttachmentType<?>, AttachmentType<T>> {
    private DeferredAttachmentType(ResourceKey<AttachmentType<?>> key) {
        super(key);
    }

    /// {@return a deferred holder for the attachment type registered under the given name}
    ///
    /// @param name The registry name of the attachment type
    public static <T> DeferredAttachmentType<T> createAttachment(Identifier name) {
        return createAttachment(ResourceKey.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, name));
    }

    /// {@return a deferred holder for the attachment type registered under the given key}
    ///
    /// @param key The registry key of the attachment type
    public static <T> DeferredAttachmentType<T> createAttachment(ResourceKey<AttachmentType<?>> key) {
        return new DeferredAttachmentType<>(key);
    }
}
