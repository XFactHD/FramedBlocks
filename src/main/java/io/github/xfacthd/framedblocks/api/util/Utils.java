package io.github.xfacthd.framedblocks.api.util;

import com.google.common.base.Preconditions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.TriState;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

/// Provides various generic helpers.
public final class Utils {
    private static final Identifier RL_TEMPLATE = Utils.id(FramedConstants.MOD_ID, "");
    /// Indicates whether the game is running in a production environment.
    public static final boolean PRODUCTION = FMLEnvironment.isProduction();
    /// Indicates whether the game is running on the physical client.
    public static final boolean CLIENT_DIST = FMLEnvironment.getDist().isClient();

    /// Returns a text component with a translation key in the format `[prefix.]framedblocks[.postfix]`
    /// and the given formatting arguments.
    ///
    /// @param prefix    The prefix to prepend the translation key with
    /// @param postfix   The postfix to append to the translation key
    /// @param arguments The formatting arguments to insert into the translated text
    /// @return a translatable text component
    public static MutableComponent translate(@Nullable String prefix, @Nullable String postfix, Object... arguments) {
        return Component.translatable(translationKey(prefix, postfix), arguments);
    }

    /// Returns a text component with a translation key in the format `[prefix.]framedblocks[.postfix]`.
    ///
    /// @param prefix    The prefix to prepend the translation key with
    /// @param postfix   The postfix to append to the translation key
    /// @return a translatable text component
    public static MutableComponent translate(@Nullable String prefix, @Nullable String postfix) {
        return Component.translatable(translationKey(prefix, postfix));
    }

    /// {@return a translation key in the format `[prefix.]framedblocks[.postfix]`}
    ///
    /// @param prefix  The prefix to prepend the translation key with
    /// @param postfix The postfix to append to the translation key
    public static String translationKey(@Nullable String prefix, @Nullable String postfix) {
        String key = "";
        if (prefix != null) {
            key = prefix + ".";
        }
        key += FramedConstants.MOD_ID;
        if (postfix != null) {
            key += "." + postfix;
        }
        return key;
    }

    /// {@return a translation key for a config entry of the given type and config key}
    ///
    /// @param type The type of the enclosing config
    /// @param key  The key of the config entry
    public static String translateConfig(String type, String key) {
        return translationKey("config", type + "." + key);
    }

    /// Build an array of text components indexed by the enum's ordinal in the format
    /// `prefix.framedblocks.postfix.value_serialized_name` and apply the given
    /// format modifiers to it.
    ///
    /// @param prefix     The prefix to prepend the translation keys with
    /// @param postfix    The postfix to insert between mod ID and the value name
    /// @param values     The enum values to translate
    /// @param formatting The format modifiers to apply to the text components
    /// @return the text components of the enum values
    public static <T extends Enum<T> & StringRepresentable> Component[] buildEnumTranslations(
            String prefix, String postfix, T[] values, ChatFormatting... formatting
    ) {
        return Arrays.stream(values)
                .map(v -> translate(prefix, postfix + "." + v.getSerializedName()))
                .map(c -> c.withStyle(formatting))
                .toArray(Component[]::new);
    }

    /// Build an array of text components indexed by the enum's ordinal with the enum value translations
    /// inserted as a format argument into the translation of the given key.
    ///
    /// @param key               The translation accepting a formatting argument
    /// @param values            The enum values to bind
    /// @param valueTranslations The translations of the enum values
    /// @return the text components of the bound enum value translations
    public static <T extends Enum<T>> Component[] bindEnumTranslation(
            String key, T[] values, Component[] valueTranslations
    ) {
        Preconditions.checkArgument(
                values.length == valueTranslations.length, "Value and translation arrays must have the same length"
        );
        Component[] components = new Component[values.length];
        for (T v : values) {
            components[v.ordinal()] = Component.translatable(key, valueTranslations[v.ordinal()]);
        }
        return components;
    }

    /// {@return a user-displayable representation of the given tag key}
    ///
    /// @param tag The tag to translate
    public static MutableComponent translateTag(TagKey<?> tag) {
        String key = Tags.getTagTranslationKey(tag);
        return Component.translatableWithFallback(key, "#" + tag.location());
    }

    /// {@return an immutable list containing the content of the two given lists}
    ///
    /// @param listOne The first list
    /// @param listTwo The second list
    public static <T> List<T> concat(List<T> listOne, List<T> listTwo) {
        if (listOne.isEmpty()) {
            return listTwo;
        }
        if (listTwo.isEmpty()) {
            return listOne;
        }

        List<T> list = new ArrayList<>(listOne.size() + listTwo.size());
        list.addAll(listOne);
        list.addAll(listTwo);
        return List.copyOf(list);
    }

    /// {@return an immutable set containing the content of the two given set}
    ///
    /// @param setOne The first list
    /// @param setTwo The second list
    public static <T> Set<T> concat(Set<T> setOne, Set<T> setTwo) {
        if (setOne.isEmpty()) {
            return setTwo;
        }
        if (setTwo.isEmpty()) {
            return setOne;
        }

        Set<T> set = new HashSet<>(setOne.size() + setTwo.size());
        set.addAll(setOne);
        set.addAll(setTwo);
        return Set.copyOf(set);
    }

    /// Copy all elements from the source list to the destination list.
    /// (Significantly faster than [ArrayList#addAll(Collection)] in benchmarks).
    ///
    /// @param src  The list to copy from
    /// @param dest The list to copy into
    /// @return the destination list
    @SuppressWarnings({ "UseBulkOperation", "ForLoopReplaceableByForEach" })
    public static <T> ArrayList<T> copyAll(List<T> src, ArrayList<T> dest) {
        if (src.isEmpty()) {
            return dest;
        }

        dest.ensureCapacity(dest.size() + src.size());
        for (int i = 0; i < src.size(); i++) {
            dest.add(src.get(i));
        }
        return dest;
    }

    /// {@return a block tag key of the given name in the `framedblocks` namespace}
    ///
    /// @param name The name of the tag
    public static TagKey<Block> blockTag(String name) {
        return blockTag(FramedConstants.MOD_ID, name);
    }

    /// {@return a block tag key of the given name in the given namespace}
    ///
    /// @param modid the namespace of the tag
    /// @param name The name of the tag
    public static TagKey<Block> blockTag(String modid, String name) {
        return BlockTags.create(Utils.id(modid, name));
    }

    /// {@return an item tag key of the given name in the `framedblocks` namespace}
    ///
    /// @param name The name of the tag
    public static TagKey<Item> itemTag(String name) {
        return itemTag(FramedConstants.MOD_ID, name);
    }

    /// {@return an item tag key of the given name in the given namespace}
    ///
    /// @param modid the namespace of the tag
    /// @param name The name of the tag
    public static TagKey<Item> itemTag(String modid, String name) {
        return ItemTags.create(Utils.id(modid, name));
    }

    /// {@return an identifier with the given path in the `framedblocks` namespace}
    ///
    /// @param path The path of the identifier
    public static Identifier id(String path) {
        return RL_TEMPLATE.withPath(path);
    }

    /// {@return an identifier with the given path in the given namespace}
    ///
    /// @param namespace The namespace of the identifier
    /// @param path      The path of the identifier
    public static Identifier id(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    /// {@return a payload type with the given path in the `framedblocks` namespace}
    ///
    /// @param path The path of the payload type
    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> payloadType(String path) {
        return new CustomPacketPayload.Type<>(id(path));
    }

    /// {@return the resource key of the given holder if available, else throws an exception}
    ///
    /// @param holder The holder whose key to resolve
    public static <T> ResourceKey<T> getKeyOrThrow(Holder<T> holder) {
        return holder.unwrapKey().orElseThrow(
                () -> new IllegalArgumentException("Direct holders and unbound reference holders are not supported")
        );
    }

    /// Add the given stack to the given player's inventory.
    /// If the player is in survival mode and the item does not fit in the inventory, then it is dropped instead.
    /// If the player is in creative mode, then the item is only added if the player doesn't already have it
    /// and there is space in the inventory, otherwise the item is destroyed.
    ///
    /// @param player The player to give the stack to
    /// @param stack  The stack to give to the player
    public static void giveToPlayer(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        boolean creative = player.hasInfiniteMaterials();
        if (!creative) {
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        } else if (!player.getInventory().contains(stack)) {
            player.getInventory().add(stack);
        }
    }

    /// Drop the contents of the given item resource handler into the level.
    ///
    /// @param level       The level to drop the items in
    /// @param pos         The position to drop the items around
    /// @param itemHandler The resource handler whose contents to drop
    public static void dropItemResourceHandlerContents(Level level, BlockPos pos, ItemStacksResourceHandler itemHandler) {
        for (int i = 0; i < itemHandler.size(); i++) {
            int count = itemHandler.getAmountAsInt(i);
            if (count > 0) {
                ItemResource resource = itemHandler.getResource(i);
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), resource.toStack(count));
            }
        }
    }

    /// Clear all slots of the given item resource handler.
    ///
    /// @param itemHandler The resource handler to clear
    public static void clearItemResourceHandler(ItemStacksResourceHandler itemHandler) {
        for (int i = 0; i < itemHandler.size(); i++) {
            itemHandler.set(i, ItemResource.EMPTY, 0);
        }
    }

    /// Extract one item from the given access and return whether the extraction succeeded.
    ///
    /// @param access The item access to extract from
    /// @param commit Whether the transaction should be commited
    /// @return whether the extraction succeeded
    public static boolean extractOneFromItemAccess(ItemAccess access, boolean commit) {
        try (Transaction tx = Transaction.openRoot()) {
            if (access.extract(access.getResource(), 1, tx) == 1) {
                if (commit) {
                    tx.commit();
                }
                return true;
            }
            return false;
        }
    }

    /// {@return whether the given stack is a wrench that can be used to rotate framed blocks}
    ///
    /// @param stack The stack to check
    public static boolean isWrenchRotationTool(ItemStack stack) {
        return stack.canPerformAction(FramedConstants.ItemAbilities.ACTION_WRENCH_ROTATE) || (stack.is(FramedConstants.Tags.TOOL_WRENCH) && !stack.is(FramedConstants.Tags.COMPLEX_WRENCH));
    }

    /// {@return wether the given stack is a configuration tool that can be used to rotate camos}
    ///
    /// @param stack The stack to check
    public static boolean isConfigurationTool(ItemStack stack) {
        return stack.is(FramedConstants.Objects.FRAMED_SCREWDRIVER) || stack.canPerformAction(FramedConstants.ItemAbilities.ACTION_WRENCH_CONFIGURE);
    }

    /// Format the given stack for display in an exception message or crash report.
    ///
    /// @param stack The stack to format
    /// @return the text representation of the stack
    public static String formatItemStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return "~~EMPTY~~";
        }

        String result = stack.getCount() + "x " + stack.getItem() + "[";
        DataComponentPatch patch = stack.getComponentsPatch();
        if (patch != DataComponentPatch.EMPTY) {
            result += patch;
        }
        return result + "]";
    }

    /// Format the given hit result for display in an exception message or crash report.
    ///
    /// @param hitResult The hit result to format
    /// @return the text representation of the hit result
    public static String formatHitResult(@Nullable HitResult hitResult) {
        if (hitResult == null) {
            return "~~NULL~~";
        }

        ToStringBuilder result = new ToStringBuilder(hitResult)
                .append("Type", hitResult.getType())
                .append("Location", hitResult.getLocation());
        if (hitResult instanceof BlockHitResult blockHit) {
            result.append("Position", blockHit.getBlockPos())
                    .append("Side", blockHit.getDirection())
                    .append("Inside", blockHit.isInside());
        } else if (hitResult instanceof EntityHitResult entityHit) {
            result.append("Entity", entityHit.getEntity());
        }
        return result.toString();
    }

    /// {@return the tristate representation of the given boolean}
    ///
    /// @param value The boolean to convert
    public static TriState toTriState(boolean value) {
        return value ? TriState.TRUE : TriState.FALSE;
    }

    @ApiStatus.Internal
    public static <T> T loadService(Class<T> clazz) {
        return ServiceLoader.load(clazz, Utils.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }

    private Utils() { }
}
