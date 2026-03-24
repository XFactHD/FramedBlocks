package io.github.xfacthd.framedblocks.common.compat.ae2;

import appeng.api.AECapabilities;
import appeng.api.crafting.PatternDetailsHelper;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.registration.DeferredDataComponentType;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.util.registration.DeferredDataComponentTypeRegister;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jspecify.annotations.Nullable;

public final class AppliedEnergisticsCompat
{
    public static final String MOD_ID = "ae2";
    public static final String SAW_PATTERN_ID = "framing_saw_pattern";
    private static boolean loaded = false;

    public static void init(IEventBus modBus)
    {
        if (ModList.get().isLoaded(MOD_ID))
        {
            GuardedAccess.init(modBus);
            loaded = true;
        }
    }

    public static boolean isLoaded()
    {
        return loaded;
    }

    public static ItemStack makeBlankPatternStack()
    {
        if (loaded)
        {
            return GuardedAccess.makeBlankPatternStack();
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack makeSawPatternStack()
    {
        if (loaded)
        {
            return GuardedAccess.makeSawPatternStack();
        }
        return ItemStack.EMPTY;
    }

    public static boolean isPattern(ItemStack stack, boolean encoded)
    {
        if (loaded)
        {
            return GuardedAccess.isPattern(stack, encoded);
        }
        return false;
    }

    @Nullable
    public static ItemStack tryEncodePattern(ItemStack input, ItemStack[] additives, ItemStack output)
    {
        if (loaded)
        {
            return GuardedAccess.tryEncodePattern(input, additives, output);
        }
        return null;
    }

    static final class GuardedAccess
    {
        private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FramedConstants.MOD_ID);
        private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, FramedConstants.MOD_ID);
        private static final DeferredDataComponentTypeRegister DATA_COMPONENTS = DeferredDataComponentTypeRegister.create(FramedConstants.MOD_ID);

        static final Holder<Item> ITEM_FRAMING_SAW_PATTERN = ITEMS.registerItem(SAW_PATTERN_ID, props ->
                PatternDetailsHelper.encodedPatternItemBuilder(FramingSawPatternDetails::new)
                        .invalidPatternTooltip(FramingSawPatternDetails::makeInvalidPatternTooltip)
                        .build(props)
        );
        static final DeferredHolder<AttachmentType<?>, AttachmentType<FramingSawCraftingMachine>> ATTACHMENT_SAW_MACHINE = ATTACHMENTS.register(
                "framing_saw_machine", () -> AttachmentType.builder(FramingSawCraftingMachine::new).build()
        );
        static final DeferredDataComponentType<EncodedFramingSawPattern> DC_TYPE_ENCODED_SAW_PATTERN = DATA_COMPONENTS.registerComponentType(
                "framing_saw_pattern", builder -> builder.persistent(EncodedFramingSawPattern.CODEC).networkSynchronized(EncodedFramingSawPattern.STREAM_CODEC)
        );

        static final Holder<Item> ITEM_BLANK_PATTERN = DeferredItem.createItem(Utils.id(MOD_ID, "blank_pattern"));

        public static void init(IEventBus modBus)
        {
            ITEMS.register(modBus);
            ATTACHMENTS.register(modBus);
            DATA_COMPONENTS.register(modBus);
            modBus.addListener(GuardedAccess::onRegisterCapabilities);
        }

        private static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
        {
            event.registerBlockEntity(
                    AECapabilities.CRAFTING_MACHINE,
                    FBContent.BE_TYPE_POWERED_FRAMING_SAW.value(),
                    (saw, _) -> saw.getData(ATTACHMENT_SAW_MACHINE)
            );
        }

        public static ItemStack makeBlankPatternStack()
        {
            return new ItemStack(ITEM_BLANK_PATTERN);
        }

        public static ItemStack makeSawPatternStack()
        {
            return new ItemStack(ITEM_FRAMING_SAW_PATTERN);
        }

        public static boolean isPattern(ItemStack stack, boolean encoded)
        {
            return stack.is(encoded ? ITEM_FRAMING_SAW_PATTERN : ITEM_BLANK_PATTERN);
        }

        public static ItemStack tryEncodePattern(ItemStack input, ItemStack[] additives, ItemStack output)
        {
            ItemStack stack = new ItemStack(ITEM_FRAMING_SAW_PATTERN);
            FramingSawPatternDetails.encode(stack, input, additives, output);
            return stack;
        }

        private GuardedAccess() { }
    }

    private AppliedEnergisticsCompat() { }
}
