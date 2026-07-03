package io.github.xfacthd.framedblocks.api.block.blockentity;

import io.github.xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.ItemPredicate;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/// Represents modifiers which can be applied to any framed block.
public enum FrameModifier implements StringRepresentable {
    /// Makes the framed block emit light
    GLOWING(
            IFramedBlockEntity::isGlowing,
            IFramedBlockEntity::setGlowing,
            ItemPredicate.ofTag(Tags.Items.DUSTS_GLOWSTONE),
            () -> ItemResource.of(Items.GLOWSTONE_DUST),
            BlueprintCopyBehaviour::getGlowstoneCount
    ),
    /// Makes the framed block intangible (removes collision)
    INTANGIBLE(
            IFramedBlockEntity::isMarkedIntangible,
            IFramedBlockEntity::setIntangible,
            ItemPredicate.ofItem(FramedConstants.Objects.PHANTOM_PASTE),
            () -> ItemResource.of(FramedConstants.Objects.PHANTOM_PASTE),
            BlueprintCopyBehaviour::getIntangibleCount
    ),
    /// Makes the framed block immune to fire, most explosions and certain mobs (i.e. the Ender Dragon)
    REINFORCED(
            IFramedBlockEntity::isReinforced,
            IFramedBlockEntity::setReinforced,
            ItemPredicate.ofItem(FramedConstants.Objects.FRAMED_REINFORCEMENT),
            () -> ItemResource.of(FramedConstants.Objects.FRAMED_REINFORCEMENT),
            BlueprintCopyBehaviour::getReinforcementCount
    ),
    /// Makes the framed block appear emissive (i.e. fullbright)
    EMISSIVE(
            IFramedBlockEntity::isEmissive,
            IFramedBlockEntity::setEmissive,
            ItemPredicate.ofItem(FramedConstants.Objects.GLOW_PASTE),
            () -> ItemResource.of(FramedConstants.Objects.GLOW_PASTE),
            BlueprintCopyBehaviour::getEmissiveCount
    ),
    ;

    static final FrameModifier[] MODIFIERS = values();
    public static final int COUNT = MODIFIERS.length;
    private static final IntFunction<FrameModifier> BY_ID = ByIdMap.continuous(FrameModifier::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, FrameModifier> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, FrameModifier::ordinal);

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final FlagGetter flagGetter;
    private final FlagSetter flagSetter;
    private final ItemPredicate itemPredicate;
    private final Supplier<ItemResource> defaultResourceProvider;
    private final BlueprintReader blueprintReader;

    FrameModifier(FlagGetter flagGetter, FlagSetter flagSetter, ItemPredicate itemPredicate, Supplier<ItemResource> defaultResourceProvider, BlueprintReader blueprintReader) {
        this.flagGetter = flagGetter;
        this.flagSetter = flagSetter;
        this.itemPredicate = itemPredicate;
        this.defaultResourceProvider = defaultResourceProvider;
        this.blueprintReader = blueprintReader;
    }

    /// {@return whether this modifier is active on the given [IFramedBlockEntity]}
    ///
    /// @param be The BE to check against
    public boolean isActive(IFramedBlockEntity be) {
        return flagGetter.getFlag(be);
    }

    /// Set the state of this modifier on the given [IFramedBlockEntity].
    ///
    /// @param be     The BE to adjust this modifier's state on
    /// @param active The target state of this modifier
    public void setActive(IFramedBlockEntity be, boolean active) {
        flagSetter.setFlag(be, active);
    }

    /// {@return whether this modifier can be applied with the given [ItemResource]}
    ///
    /// @param resource The resource to test against
    public boolean matches(ItemResource resource) {
        return itemPredicate.test(resource);
    }

    /// {@return whether this modifier can be applied with the given [ItemStack]}
    ///
    /// @param stack The stack to test against
    public boolean matches(ItemStack stack) {
        return itemPredicate.test(stack);
    }

    /// {@return the default [ItemResource] to use for applying this modifier}
    public ItemResource getDefaultResource() {
        return defaultResourceProvider.get();
    }

    /// {@return the default [ItemStack] to use for applying this modifier}
    public ItemStack getDefaultStack() {
        return defaultResourceProvider.get().toStack();
    }

    /// {@return the default [ItemStack] with the given count to use for applying this modifier}
    ///
    /// @param count The stack size of the resulting stack
    public ItemStack getDefaultStack(int count) {
        return defaultResourceProvider.get().toStack(count);
    }

    /// Collect the stack of items of this modifier, if any, required to apply the given [BlueprintData] to a block.
    ///
    /// @param behaviour The copy behavior of the block to be placed by the blueprint
    /// @param data      The blueprint data to apply to the block
    /// @param output    The list of stacks to add the stack to
    public void collectForBlueprint(BlueprintCopyBehaviour behaviour, BlueprintData data, List<ItemStack> output) {
        int count = blueprintReader.getCount(behaviour, data);
        if (count > 0) {
            output.add(getDefaultStack(count));
        }
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    /// {@return whether the given {@link ItemResource} {@linkplain #matches(ItemResource) matches} any modifier}
    ///
    /// @param resource The resource to test against
    public static boolean matchesAny(ItemResource resource) {
        return findMatching(resource) != null;
    }

    /// {@return whether the given {@link ItemStack} {@linkplain #matches(ItemStack) matches} any modifier}
    ///
    /// @param stack The stack to test against
    public static boolean matchesAny(ItemStack stack) {
        return findMatching(stack) != null;
    }

    /// {@return the modifier, if any, matching the given [ItemResource]}
    ///
    /// @param resource The resource to test against
    public static @Nullable FrameModifier findMatching(ItemResource resource) {
        for (FrameModifier modifier : MODIFIERS) {
            if (modifier.itemPredicate.test(resource)) {
                return modifier;
            }
        }
        return null;
    }

    /// {@return the modifier, if any, matching the given [ItemStack]}
    ///
    /// @param stack The stack to test against
    public static @Nullable FrameModifier findMatching(ItemStack stack) {
        for (FrameModifier modifier : MODIFIERS) {
            if (modifier.itemPredicate.test(stack)) {
                return modifier;
            }
        }
        return null;
    }

    @FunctionalInterface
    private interface FlagGetter {
        boolean getFlag(IFramedBlockEntity be);
    }

    @FunctionalInterface
    private interface FlagSetter {
        void setFlag(IFramedBlockEntity be, boolean flag);
    }

    @FunctionalInterface
    private interface BlueprintReader {
        int getCount(BlueprintCopyBehaviour behaviour, BlueprintData data);
    }
}
