package io.github.xfacthd.framedblocks.api.util;

import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.component.FrameConfig;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.registration.DeferredDataComponentType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public final class FramedConstants {
    public static final String MOD_ID = "framedblocks";

    public static final class Registries {
        public static final ResourceKey<Registry<CamoContainerFactory<?>>> CAMO_CONTAINER_FACTORY_REGISTRY_KEY = registry("camo_container");
        public static final ResourceKey<Registry<BlockOverlay>> BLOCK_OVERLAY_REGISTRY_KEY = registry("block_overlay");

        private static <T> ResourceKey<Registry<T>> registry(String name) {
            return ResourceKey.createRegistryKey(Utils.id(name));
        }

        private Registries() { }
    }

    public static final class Objects {
        public static final Holder<Block> FRAMED_CUBE = DeferredBlock.createBlock(Utils.id("framed_cube"));

        public static final Holder<Item> FRAMED_HAMMER = DeferredItem.createItem(Utils.id("framed_hammer"));
        public static final Holder<Item> FRAMED_WRENCH = DeferredItem.createItem(Utils.id("framed_wrench"));
        public static final Holder<Item> FRAMED_KEY = DeferredItem.createItem(Utils.id("framed_key"));
        public static final Holder<Item> FRAMED_SCREWDRIVER = DeferredItem.createItem(Utils.id("framed_screwdriver"));
        public static final Holder<Item> FRAMED_REINFORCEMENT = DeferredItem.createItem(Utils.id("framed_reinforcement"));
        public static final Holder<Item> PHANTOM_PASTE = DeferredItem.createItem(Utils.id("phantom_paste"));
        public static final Holder<Item> GLOW_PASTE = DeferredItem.createItem(Utils.id("glow_paste"));

        public static final DeferredDataComponentType<CamoList> DC_TYPE_CAMO_LIST = DeferredDataComponentType.createDataComponent(Utils.id("camo_list"));
        public static final DeferredDataComponentType<FrameConfig> DC_TYPE_FRAME_CONFIG = DeferredDataComponentType.createDataComponent(Utils.id("frame_config"));
        public static final DeferredDataComponentType<Holder<BlockOverlay>> DC_TYPE_BLOCK_OVERLAY = DeferredDataComponentType.createDataComponent(Utils.id("block_overlay"));
        public static final DeferredDataComponentType<WrenchRotationMode> DC_TYPE_WRENCH_MODE = DeferredDataComponentType.createDataComponent(Utils.id("wrench_mode"));
        /// If present on an item, breaking a framed block with it will unconditionally retain the camo on the dropped item instead of dropping it separately
        public static final DeferredDataComponentType<Unit> DC_TYPE_RETAIN_CAMO = DeferredDataComponentType.createDataComponent(Utils.id("retain_camo"));

        private Objects() { }
    }

    public static final class Tags {
        public static final TagKey<Block> FRAMEABLE = Utils.blockTag("frameable");
        public static final TagKey<Block> BLOCK_BLACKLIST = Utils.blockTag("blacklisted");
        /// Allow other mods to whitelist their BEs, circumventing the config setting
        public static final TagKey<Block> BE_WHITELIST = Utils.blockTag("blockentity_whitelisted");
        /// Blocks tagged with this will not be occluded by framed blocks using them as camo, both as camo and directly placed
        public static final TagKey<Block> NON_OCCLUDEABLE = Utils.blockTag("non_occludeable");
        /// Group tag containing all full-cube blocks excluding ones that can deviate from that via player interaction
        public static final TagKey<Block> GROUP_FULL_CUBE = Utils.blockTag("group/full");

        public static final TagKey<Fluid> FLUID_BLACKLIST = FluidTags.create(Utils.id("blacklisted"));
        public static final TagKey<Item> TOOL_WRENCH = Utils.itemTag("c", "tools/wrench");
        public static final TagKey<Item> COMPLEX_WRENCH = Utils.itemTag("complex_wrench");
        /// Allow other mods to add items that temporarily disable intangibility to allow interaction with the targeted block
        public static final TagKey<Item> DISABLE_INTANGIBLE = Utils.itemTag("disable_intangible");
        /// Items tagged with this cannot be used as fluid containers in fluid camo application via crafting
        public static final TagKey<Item> CRAFTING_BLOCKED_FLUID_CONTAINERS = Utils.itemTag("crafting_blocked_fluid_containers");

        /// Specifies the order in which [BlockOverlay]s are listed in the Paint Roller screen
        public static final TagKey<BlockOverlay> OVERLAY_ORDER = TagKey.create(Registries.BLOCK_OVERLAY_REGISTRY_KEY, Utils.id("overlay_order"));

        private Tags() { }
    }

    public static final class ItemAbilities {
        /// Provided by tools for rotating blocks
        public static final ItemAbility ACTION_WRENCH_ROTATE = ItemAbility.get("wrench_rotate");
        /// Provided by tools for emptying items out of blocks (respected for removal of standard block camos)
        public static final ItemAbility ACTION_WRENCH_EMPTY = ItemAbility.get("wrench_empty");
        /// Providing by tools for configuring blocks (respected for camo rotation)
        public static final ItemAbility ACTION_WRENCH_CONFIGURE = ItemAbility.get("wrench_configure");

        private ItemAbilities() { }
    }

    private FramedConstants() { }
}
