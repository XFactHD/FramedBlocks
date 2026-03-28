package io.github.xfacthd.framedblocks.api.block.blockentity;

import io.github.xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.util.ItemPredicate;
import io.github.xfacthd.framedblocks.api.util.Utils;
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

public enum FrameModifier implements StringRepresentable {
    GLOWING(
            IFramedBlockEntity::isGlowing,
            IFramedBlockEntity::setGlowing,
            ItemPredicate.ofTag(Tags.Items.DUSTS_GLOWSTONE),
            () -> ItemResource.of(Items.GLOWSTONE_DUST),
            BlueprintCopyBehaviour::getGlowstoneCount
    ),
    INTANGIBLE(
            IFramedBlockEntity::isMarkedIntangible,
            IFramedBlockEntity::setIntangible,
            ItemPredicate.ofItem(Utils.PHANTOM_PASTE),
            () -> ItemResource.of(Utils.PHANTOM_PASTE),
            BlueprintCopyBehaviour::getIntangibleCount
    ),
    REINFORCED(
            IFramedBlockEntity::isReinforced,
            IFramedBlockEntity::setReinforced,
            ItemPredicate.ofItem(Utils.FRAMED_REINFORCEMENT),
            () -> ItemResource.of(Utils.FRAMED_REINFORCEMENT),
            BlueprintCopyBehaviour::getReinforcementCount
    ),
    EMISSIVE(
            IFramedBlockEntity::isEmissive,
            IFramedBlockEntity::setEmissive,
            ItemPredicate.ofItem(Utils.GLOW_PASTE),
            () -> ItemResource.of(Utils.GLOW_PASTE),
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

    public boolean isActive(IFramedBlockEntity be) {
        return flagGetter.getFlag(be);
    }

    public void setActive(IFramedBlockEntity be, boolean active) {
        flagSetter.setFlag(be, active);
    }

    public boolean matches(ItemResource resource) {
        return itemPredicate.test(resource);
    }

    public boolean matches(ItemStack stack) {
        return itemPredicate.test(stack);
    }

    public ItemResource getDefaultResource() {
        return defaultResourceProvider.get();
    }

    public ItemStack getDefaultStack() {
        return defaultResourceProvider.get().toStack();
    }

    public ItemStack getDefaultStack(int count) {
        return defaultResourceProvider.get().toStack(count);
    }

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

    public static boolean matchesAny(ItemResource resource) {
        return findMatching(resource) != null;
    }

    public static boolean matchesAny(ItemStack stack) {
        return findMatching(stack) != null;
    }

    public static @Nullable FrameModifier findMatching(ItemResource resource) {
        for (FrameModifier modifier : MODIFIERS) {
            if (modifier.itemPredicate.test(resource)) {
                return modifier;
            }
        }
        return null;
    }

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
