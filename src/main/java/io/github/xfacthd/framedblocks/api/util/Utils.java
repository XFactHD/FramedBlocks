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

public final class Utils {
    private static final Identifier RL_TEMPLATE = Utils.id(FramedConstants.MOD_ID, "");
    public static final boolean PRODUCTION = FMLEnvironment.isProduction();
    public static final boolean CLIENT_DIST = FMLEnvironment.getDist().isClient();

    public static MutableComponent translate(@Nullable String prefix, @Nullable String postfix, Object... arguments) {
        return Component.translatable(translationKey(prefix, postfix), arguments);
    }

    public static MutableComponent translate(@Nullable String prefix, @Nullable String postfix) {
        return Component.translatable(translationKey(prefix, postfix));
    }

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

    public static String translateConfig(String type, String key) {
        return translationKey("config", type + "." + key);
    }

    public static <T extends Enum<T> & StringRepresentable> Component[] buildEnumTranslations(
            String prefix, String postfix, T[] values, ChatFormatting... formatting
    ) {
        return Arrays.stream(values)
                .map(v -> translate(prefix, postfix + "." + v.getSerializedName()))
                .map(c -> c.withStyle(formatting))
                .toArray(Component[]::new);
    }

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

    public static MutableComponent translateTag(TagKey<?> tag) {
        String key = Tags.getTagTranslationKey(tag);
        return Component.translatableWithFallback(key, "#" + tag.location());
    }

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

    /**
     * Copy all elements from the source list to the destination list
     * (Significantly faster than {@link ArrayList#addAll(Collection)} in benchmarks)
     */
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

    public static TagKey<Block> blockTag(String name) {
        return blockTag(FramedConstants.MOD_ID, name);
    }

    public static TagKey<Block> blockTag(String modid, String name) {
        return BlockTags.create(Utils.id(modid, name));
    }

    public static TagKey<Item> itemTag(String name) {
        return itemTag(FramedConstants.MOD_ID, name);
    }

    public static TagKey<Item> itemTag(String modid, String name) {
        return ItemTags.create(Utils.id(modid, name));
    }

    public static Identifier id(String path) {
        return RL_TEMPLATE.withPath(path);
    }

    public static Identifier id(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> payloadType(String path) {
        return new CustomPacketPayload.Type<>(id(path));
    }

    public static <T> ResourceKey<T> getKeyOrThrow(Holder<T> holder) {
        return holder.unwrapKey().orElseThrow(
                () -> new IllegalArgumentException("Direct holders and unbound reference holders are not supported")
        );
    }

    /**
     * Place the given {@link ItemStack} in the given {@link Player}'s inventory or drop it if it doesn't fit if the
     * player is in survival or place it in the player's inventory if the player is in creative mode and doesn't
     * already have the item
     *
     * @param player The player to give the stack to
     * @param stack The stack to give to the player
     */
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

    public static void dropItemResourceHandlerContents(Level level, BlockPos pos, ItemStacksResourceHandler itemHandler) {
        for (int i = 0; i < itemHandler.size(); i++) {
            int count = itemHandler.getAmountAsInt(i);
            if (count > 0) {
                ItemResource resource = itemHandler.getResource(i);
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), resource.toStack(count));
            }
        }
    }

    public static void clearItemResourceHandler(ItemStacksResourceHandler itemHandler) {
        for (int i = 0; i < itemHandler.size(); i++) {
            itemHandler.set(i, ItemResource.EMPTY, 0);
        }
    }

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

    public static boolean isWrenchRotationTool(ItemStack stack) {
        return stack.canPerformAction(FramedConstants.ItemAbilities.ACTION_WRENCH_ROTATE) || (stack.is(FramedConstants.Tags.TOOL_WRENCH) && !stack.is(FramedConstants.Tags.COMPLEX_WRENCH));
    }

    public static boolean isConfigurationTool(ItemStack stack) {
        return stack.is(FramedConstants.Objects.FRAMED_SCREWDRIVER) || stack.canPerformAction(FramedConstants.ItemAbilities.ACTION_WRENCH_CONFIGURE);
    }

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
