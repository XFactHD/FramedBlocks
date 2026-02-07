package io.github.xfacthd.framedblocks.api.util;

import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class FramedConstants
{
    public static final String MOD_ID = "framedblocks";
    public static final ResourceKey<Registry<CamoContainerFactory<?>>> CAMO_CONTAINER_FACTORY_REGISTRY_KEY = registry("camo_container");

    private static <T> ResourceKey<Registry<T>> registry(String name)
    {
        return ResourceKey.createRegistryKey(Utils.id(name));
    }

    private FramedConstants() { }
}
