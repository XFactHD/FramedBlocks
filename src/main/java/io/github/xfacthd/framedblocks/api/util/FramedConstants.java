package io.github.xfacthd.framedblocks.api.util;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public final class FramedConstants {
    public static final String MOD_ID = "framedblocks";
    public static final ResourceKey<Registry<CamoContainerFactory<?>>> CAMO_CONTAINER_FACTORY_REGISTRY_KEY = registry("camo_container");
    public static final ResourceKey<Registry<BlockOverlay>> BLOCK_OVERLAY_REGISTRY_KEY = registry("block_overlay");

    private static <T> ResourceKey<Registry<T>> registry(String name) {
        return ResourceKey.createRegistryKey(Utils.id(name));
    }

    public static final class Tags {
        /// Specifies the order in which [BlockOverlay]s are listed in the Paint Roller screen
        public static final TagKey<BlockOverlay> OVERLAY_ORDER = TagKey.create(BLOCK_OVERLAY_REGISTRY_KEY, Utils.id("overlay_order"));

        private Tags() { }
    }

    private FramedConstants() { }
}
