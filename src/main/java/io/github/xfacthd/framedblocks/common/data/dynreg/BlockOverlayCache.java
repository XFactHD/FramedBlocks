package io.github.xfacthd.framedblocks.common.data.dynreg;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BlockOverlayCache {
    private static final BlockOverlayCache SERVER_INSTANCE = new BlockOverlayCache();
    private static final BlockOverlayCache CLIENT_INSTANCE = new BlockOverlayCache();

    private final List<Holder<BlockOverlay>> overlays = new ArrayList<>();
    private final List<Holder<BlockOverlay>> overlaysView = Collections.unmodifiableList(overlays);
    private final Set<Item> validItems = new ReferenceOpenHashSet<>();
    private final Map<Item, Holder<BlockOverlay>> typeByItem = new Reference2ReferenceOpenHashMap<>();
    private boolean initialized = false;

    public static BlockOverlayCache get(boolean client) {
        return client ? CLIENT_INSTANCE : SERVER_INSTANCE;
    }

    public List<Holder<BlockOverlay>> getOverlays() {
        return overlaysView;
    }

    public boolean isValidItem(ItemStack stack) {
        return validItems.contains(stack.getItem());
    }

    public Holder<BlockOverlay> getOverlay(Item item) {
        return typeByItem.get(item);
    }

    public void update(HolderLookup.Provider registries) {
        clear();

        if (!initialized) {
            initialized = true;
            registries.lookupOrThrow(FramedConstants.Registries.BLOCK_OVERLAY_REGISTRY_KEY)
                    .listElements()
                    .forEach(overlay -> {
                        Item item = overlay.value().sourceItem().value();
                        overlays.add(overlay);
                        validItems.add(item);
                        typeByItem.put(item, overlay);
                    });
        }

        List<Holder<BlockOverlay>> overlayOrder = registries.getOrThrow(FramedConstants.Tags.OVERLAY_ORDER).stream().toList();
        overlays.sort((o1, o2) -> {
            int idx1 = overlayOrder.indexOf(o1);
            int idx2 = overlayOrder.indexOf(o2);
            if (idx1 != -1 && idx2 != -1) {
                return Integer.compare(idx1, idx2);
            }
            if (idx1 == -1 ^ idx2 == -1) {
                return Integer.compare(idx2, idx1);
            }
            ResourceKey<BlockOverlay> key1 = Utils.getKeyOrThrow(o1);
            ResourceKey<BlockOverlay> key2 = Utils.getKeyOrThrow(o2);
            return key1.compareTo(key2);
        });
    }

    public void clear() {
        overlays.clear();
        validItems.clear();
        typeByItem.clear();
        initialized = false;
    }

    private BlockOverlayCache() { }
}
