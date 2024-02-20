package xfacthd.framedblocks.common.compat.ae2;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.blockentities.ICraftingMachine;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.*;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;

public final class AppliedEnergisticsCompat
{
    private static final boolean ENABLED = !FMLEnvironment.production;

    public static void init(IEventBus modBus)
    {
        if (ENABLED && ModList.get().isLoaded("ae2"))
        {
            GuardedAccess.init(modBus);
        }
    }



    static final class GuardedAccess
    {
        private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FramedConstants.MOD_ID);
        private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, FramedConstants.MOD_ID);

        static final Holder<Item> ITEM_FRAMING_SAW_PATTERN = ITEMS.register("framing_saw_pattern", FramingSawPatternItem::new);
        static final DeferredHolder<AttachmentType<?>, AttachmentType<FramingSawCraftingMachine>> ATTACHMENT_SAW_MACHINE = ATTACHMENTS.register(
                "framing_saw_machine", () -> AttachmentType.builder(FramingSawCraftingMachine::new).build()
        );
        private static final BlockCapability<ICraftingMachine, @Nullable Direction> CAPABILITY_CRAFTING_MACHINE = BlockCapability.createSided(
                new ResourceLocation("ae2", "crafting_machine"), ICraftingMachine.class
        );

        public static void init(IEventBus modBus)
        {
            ITEMS.register(modBus);
            ATTACHMENTS.register(modBus);
            modBus.addListener(GuardedAccess::onRegisterCapabilities);
            PatternDetailsHelper.registerDecoder(new FramingSawPatternDetailsDecoder());
        }

        private static void onRegisterCapabilities(final RegisterCapabilitiesEvent event)
        {
            event.registerBlockEntity(
                    CAPABILITY_CRAFTING_MACHINE,
                    FBContent.BE_TYPE_POWERED_FRAMING_SAW.value(),
                    (saw, side) -> saw.getData(ATTACHMENT_SAW_MACHINE)
            );
        }



        private GuardedAccess() { }
    }



    private AppliedEnergisticsCompat() { }
}
