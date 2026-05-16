package io.github.xfacthd.framedblocks.common.util.registration;

import io.github.xfacthd.framedblocks.api.util.registration.DeferredAttachmentType;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class DeferredAttachmentTypeRegister extends DeferredRegister<AttachmentType<?>> {
    private DeferredAttachmentTypeRegister(String namespace) {
        super(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, namespace);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <I extends AttachmentType<?>> DeferredHolder<AttachmentType<?>, I> createHolder(
            ResourceKey<? extends Registry<AttachmentType<?>>> registryKey, Identifier key
    ) {
        return (DeferredHolder<AttachmentType<?>, I>) DeferredAttachmentType.createAttachment(ResourceKey.create(registryKey, key));
    }

    public <D> DeferredAttachmentType<D> registerAttachmentType(String name, Supplier<D> defaultValue, UnaryOperator<AttachmentType.Builder<D>> builder) {
        return (DeferredAttachmentType<D>) super.register(name, () -> builder.apply(AttachmentType.builder(defaultValue)).build());
    }

    public static DeferredAttachmentTypeRegister create(String namespace) {
        return new DeferredAttachmentTypeRegister(namespace);
    }
}
