package io.github.xfacthd.framedblocks.client.model.wrapping;

import com.mojang.serialization.Codec;
import io.github.xfacthd.framedblocks.api.model.standalone.StandaloneWrapperKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public final class StandaloneWrapperKeys {
    private static final ExtraCodecs.LateBoundIdMapper<Identifier, StandaloneWrapperKey<?>> REGISTRY = new ExtraCodecs.LateBoundIdMapper<>();
    public static final Codec<StandaloneWrapperKey<?>> CODEC = REGISTRY.codec(Identifier.CODEC);

    static void registerKey(StandaloneWrapperKey<?> wrapperKey) {
        REGISTRY.put(wrapperKey.definitionFile(), wrapperKey);
    }

    private StandaloneWrapperKeys() {}
}
