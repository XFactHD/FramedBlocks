package io.github.xfacthd.framedblocks.common.item.applicator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.block.blockentity.FrameModifier;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.camo.CamoCraftingHandler;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContent;
import io.github.xfacthd.framedblocks.api.util.network.FramedByteBufCodecs;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.camo.CamoContainerFactories;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record CamoApplicatorContent(CamoEntry[] camoEntries, int[] modifierStacks) {
    public static final int CAMO_COUNT = 16;
    public static final int MODIFIER_MAX_STACK_SIZE = 1024;
    public static final Codec<CamoApplicatorContent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Slot.CODEC.sizeLimitedListOf(CAMO_COUNT).fieldOf("camo_stacks").forGetter(CamoApplicatorContent::packCamoStacks),
            ExtraCodecs.NON_NEGATIVE_INT.listOf(FrameModifier.COUNT, FrameModifier.COUNT).fieldOf("modifier_stacks").forGetter(CamoApplicatorContent::packModifierStacks)
    ).apply(inst, CamoApplicatorContent::unpack));
    public static final StreamCodec<ByteBuf, int[]> MODIFIER_STREAM_CODEC = FramedByteBufCodecs.fixedIntArray(FrameModifier.COUNT);
    public static final StreamCodec<RegistryFriendlyByteBuf, CamoApplicatorContent> STREAM_CODEC = StreamCodec.composite(
            FramedByteBufCodecs.array(ItemStack.OPTIONAL_STREAM_CODEC.map(CamoEntry::of, CamoEntry::getStack), CamoEntry[]::new, CAMO_COUNT),
            CamoApplicatorContent::camoEntries,
            MODIFIER_STREAM_CODEC,
            CamoApplicatorContent::modifierStacks,
            CamoApplicatorContent::new
    );
    private static final CamoEntry[] EMPTY_CAMO_ARRAY = Util.make(new CamoEntry[CAMO_COUNT], arr -> Arrays.fill(arr, CamoEntry.EMPTY));
    public static final CamoApplicatorContent EMPTY = new CamoApplicatorContent(EMPTY_CAMO_ARRAY, new int[FrameModifier.COUNT]);

    public boolean hasCamoStackInSlot(int slot) {
        return !getCamoStack(slot).isEmpty();
    }

    public ItemStack getCamoStack(int slot) {
        return camoEntries[slot].stack;
    }

    public CamoEntry getCamoEntry(int slot) {
        return camoEntries[slot];
    }

    public int findFirstNonEmptyCamoSlotAfter(int slot) {
        int newSlot = (slot + 1) % CAMO_COUNT;
        while (newSlot != slot) {
            if (hasCamoStackInSlot(slot)) {
                return newSlot;
            }
            newSlot = (newSlot + 1) % CAMO_COUNT;
        }
        return slot;
    }

    public int getModifierStack(FrameModifier slot) {
        return modifierStacks[slot.ordinal()];
    }

    public boolean hasGlowstone() {
        return getModifierStack(FrameModifier.GLOWING) != 0;
    }

    public boolean hasPhantomPaste() {
        return getModifierStack(FrameModifier.INTANGIBLE) != 0;
    }

    public boolean hasReinforcement() {
        return getModifierStack(FrameModifier.REINFORCED) != 0;
    }

    public boolean hasGlowPaste() {
        return getModifierStack(FrameModifier.EMISSIVE) != 0;
    }

    public boolean hasModifier(FrameModifier modifier) {
        return switch (modifier) {
            case GLOWING -> hasGlowstone();
            case INTANGIBLE -> hasPhantomPaste();
            case REINFORCED -> hasReinforcement();
            case EMISSIVE -> hasGlowPaste();
        };
    }

    public boolean isEmpty() {
        for (CamoEntry entry : camoEntries) {
            if (!entry.stack.isEmpty()) {
                return false;
            }
        }
        for (int stack : modifierStacks) {
            if (stack != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CamoApplicatorContent(CamoEntry[] otherCamoStacks, int[] otherModStacks)) {
            for (int i = 0; i < CAMO_COUNT; i++) {
                if (!ItemStack.matches(camoEntries[i].stack, otherCamoStacks[i].stack)) {
                    return false;
                }
            }
            return Arrays.equals(modifierStacks, otherModStacks);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (CamoEntry stack : camoEntries) {
            hash = hash * 31 + ItemStack.hashItemAndComponents(stack.stack);
        }
        return hash * 31 + Arrays.hashCode(modifierStacks);
    }

    private List<Slot> packCamoStacks() {
        List<Slot> slots = new ArrayList<>(CAMO_COUNT);
        for (int i = 0; i < CAMO_COUNT; i++) {
            ItemStack stack = getCamoStack(i);
            if (!stack.isEmpty()) {
                slots.add(new Slot(i, stack));
            }
        }
        return slots;
    }

    private List<Integer> packModifierStacks() {
        return IntList.of(modifierStacks);
    }

    private static CamoApplicatorContent unpack(List<Slot> slots, List<Integer> modStacks) {
        CamoEntry[] camoStacks = new CamoEntry[CAMO_COUNT];
        Arrays.fill(camoStacks, CamoEntry.EMPTY);
        for (Slot slot : slots) {
            camoStacks[slot.idx] = CamoEntry.of(slot.stack);
        }
        return new CamoApplicatorContent(camoStacks, new IntArrayList(modStacks).toArray(new int[FrameModifier.COUNT]));
    }

    public static CamoApplicatorContent of(DataComponentGetter componentGetter) {
        return componentGetter.getOrDefault(FBContent.DC_TYPE_APPLICATOR_CONTENT, EMPTY);
    }

    public static final class CamoEntry {
        public static final CamoEntry EMPTY = Util.make(new CamoEntry(ItemStack.EMPTY), e -> e.dummyContent = DummyContent.EMPTY);

        private final ItemStack stack;
        @Nullable
        private DummyContent dummyContent = null;

        public static CamoEntry of(ItemStack stack) {
            return stack.isEmpty() ? EMPTY : new CamoEntry(stack);
        }

        private CamoEntry(ItemStack stack) {
            this.stack = stack;
        }

        public ItemStack getStack() {
            return stack;
        }

        public DummyContent getDummyContent() {
            if (dummyContent == null) {
                dummyContent = computeDummyContent();
            }
            return dummyContent;
        }

        private DummyContent computeDummyContent() {
            if (stack.isEmpty()) {
                return DummyContent.EMPTY;
            }
            CamoContainerFactory<?> factory = CamoContainerFactories.findCamoFactory(stack);
            if (factory == null) {
                return DummyContent.EMPTY;
            }
            CamoCraftingHandler<?> craftingHandler = factory.getCraftingHandler();
            if (craftingHandler == null || !craftingHandler.canApply(stack, false)) {
                return DummyContent.UNKNOWN;
            }
            return DummyContent.of(craftingHandler.apply(stack, false).getContent());
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof CamoEntry other) {
                return Objects.equals(this.stack, other.stack);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return ItemStack.hashItemAndComponents(stack);
        }

        @Override
        public String toString() {
            return stack.toString();
        }

        public record DummyContent(@Nullable CamoContent<?> content) {
            public static final DummyContent EMPTY = new DummyContent(EmptyCamoContent.EMPTY);
            public static final DummyContent UNKNOWN = new DummyContent(null);

            @Override
            public CamoContent<?> content() {
                return Objects.requireNonNull(content, "Cannot resolve content of DummyContent.UNKNOWN");
            }

            public static DummyContent of(CamoContent<?> content) {
                return content.isEmpty() ? EMPTY : new DummyContent(content);
            }
        }
    }

    private record Slot(int idx, ItemStack stack) {
        public static final Codec<Slot> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.intRange(0, CAMO_COUNT - 1).fieldOf("index").forGetter(Slot::idx),
                ItemStack.CODEC.fieldOf("stack").forGetter(Slot::stack)
        ).apply(inst, Slot::new));
    }
}
