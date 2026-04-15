package io.github.xfacthd.framedblocks.common.menu;

import io.github.xfacthd.framedblocks.api.block.blockentity.FrameModifier;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.item.applicator.CamoApplicatorConfig;
import io.github.xfacthd.framedblocks.common.item.applicator.CamoApplicatorContent;
import io.github.xfacthd.framedblocks.common.item.applicator.CamoApplicatorUtils;
import io.github.xfacthd.framedblocks.common.menu.slot.FilteredSlot;
import io.github.xfacthd.framedblocks.common.menu.slot.LockedSlot;
import io.github.xfacthd.framedblocks.common.util.FramedUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.world.inventory.StackCopySlot;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public final class CamoApplicatorMenu extends AbstractContainerMenu {
    public static final Component TITLE = Utils.translate("title", "camo_applicator");
    public static final int CAMO_INV_X = 8;
    public static final int CAMO_INV_Y = 17;
    public static final int CAMO_INV_ROWS_COLS = 4;
    public static final int MOD_INPUT_X = 152;
    public static final int MOD_INPUT_Y = 17;
    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 113;
    private static final int MOD_INGEST_INTERVAL = 2;
    private static final int MOD_INGEST_SIZE = 8;
    private static final FrameModifier[] MODIFIERS = FrameModifier.values();

    @Nullable
    private final ServerPlayer player;
    private final ContainerLevelAccess levelAccess;
    private final int hotbarSlot;
    @Nullable
    private final ItemStack applicatorStack;
    private final Slot modInputSlot;
    private final DataSlot modeDataSlot;
    private final DataSlot selectedSlotDataSlot;
    private final ModifierData modifierData;
    private long lastIngestStart = -1L;
    @Nullable
    private FrameModifier lastIngestModifier = null;
    @Nullable
    private Item lastIngestItem = null;

    public static CamoApplicatorMenu createServer(int windowId, Inventory inventory) {
        return new CamoApplicatorMenu(windowId, inventory, inventory.getSelectedSlot(), inventory.getSelectedItem());
    }

    public static CamoApplicatorMenu createClient(int windowId, Inventory inventory, FriendlyByteBuf buf) {
        int slot = buf.readVarInt();
        CamoApplicatorConfig.Mode mode = CamoApplicatorConfig.Mode.STREAM_CODEC.decode(buf);
        CamoApplicatorMenu menu = new CamoApplicatorMenu(windowId, inventory, slot, null);
        menu.modeDataSlot.set(mode.ordinal());
        menu.modifierData.deserialize(buf);
        return menu;
    }

    private CamoApplicatorMenu(int windowId, Inventory inventory, int slot, @Nullable ItemStack applicatorStack) {
        super(FBContent.MENU_TYPE_CAMO_APPLICATOR.value(), windowId);
        this.player = inventory.player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        this.levelAccess = player != null ? ContainerLevelAccess.create(player.level(), player.getOnPos()) : ContainerLevelAccess.NULL;
        this.hotbarSlot = slot;
        this.applicatorStack = applicatorStack;
        CamoSlotFactory slotFactory = CamoSlotFactory.of(applicatorStack);
        for (int i = 0; i < CamoApplicatorContent.CAMO_COUNT; i++) {
            int x = CAMO_INV_X + (i % CAMO_INV_ROWS_COLS) * SLOT_SIZE;
            int y = CAMO_INV_Y + (i / CAMO_INV_ROWS_COLS) * SLOT_SIZE;
            addSlot(slotFactory.create(i, x, y));
        }
        this.modInputSlot = addSlot(new FilteredSlot(new SimpleContainer(1), 0, MOD_INPUT_X, MOD_INPUT_Y, FrameModifier::matchesAny));
        FramedUtils.addPlayerInvSlots(this::addSlot, inventory, PLAYER_INV_X, PLAYER_INV_Y, this::makeInventorySlot);
        this.modeDataSlot = addDataSlot(DataSlot.standalone());
        this.selectedSlotDataSlot = addDataSlot(DataSlot.standalone());
        this.modifierData = new ModifierData();
        updateDataSlots();
    }

    private Slot makeInventorySlot(Container container, int slot, int x, int y) {
        return slot == hotbarSlot ? new LockedSlot(container, slot, x, y) : new Slot(container, slot, x, y);
    }

    @Override
    public void broadcastChanges() {
        tryIngestModifierItem();
        updateDataSlots();
        super.broadcastChanges();
    }

    private void tryIngestModifierItem() {
        if (applicatorStack == null || player == null) {
            return;
        }

        if (!modInputSlot.hasItem()) {
            if (lastIngestStart != -1) {
                lastIngestStart = -1;
                lastIngestModifier = null;
                lastIngestItem = null;
            }
            return;
        }

        if (lastIngestItem != null && lastIngestItem != modInputSlot.getItem().getItem()) {
            lastIngestStart = -1;
            lastIngestModifier = null;
            lastIngestItem = null;
        }

        long gameTime = player.level().getGameTime();
        if (lastIngestStart == -1) {
            lastIngestStart = gameTime;
            lastIngestModifier = FrameModifier.findMatching(modInputSlot.getItem());
            lastIngestItem = modInputSlot.getItem().getItem();
        }
        if (lastIngestModifier != null && (gameTime - lastIngestStart) % MOD_INGEST_INTERVAL == 0) {
            CamoApplicatorContent content = CamoApplicatorContent.of(applicatorStack);
            int modStackSize = content.getModifierStack(lastIngestModifier);
            if (modStackSize < CamoApplicatorContent.MODIFIER_MAX_STACK_SIZE) {
                int inputCount = modInputSlot.getItem().getCount();
                int space = CamoApplicatorContent.MODIFIER_MAX_STACK_SIZE - modStackSize;
                int ingestCount = Math.min(MOD_INGEST_SIZE, Math.min(inputCount, space));

                CamoApplicatorUtils.updateModifierInContent(applicatorStack, lastIngestModifier, ingestCount);
                modInputSlot.remove(ingestCount);
            }

            if (!modInputSlot.hasItem()) {
                lastIngestStart = -1;
                lastIngestModifier = null;
                lastIngestItem = null;
            }
        }
    }

    private void updateDataSlots() {
        if (applicatorStack != null) {
            CamoApplicatorConfig config = CamoApplicatorConfig.of(applicatorStack);
            modeDataSlot.set(config.mode().ordinal());
            selectedSlotDataSlot.set(config.selectedSlot());

            CamoApplicatorContent content = CamoApplicatorContent.of(applicatorStack);
            modifierData.update(content.modifierStacks(), config);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack remainder = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            remainder = stack.copy();
            if (index <= CamoApplicatorContent.CAMO_COUNT) {
                if (!moveItemStackTo(stack, CamoApplicatorContent.CAMO_COUNT + 1, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, CamoApplicatorContent.CAMO_COUNT + 1, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return remainder;
    }

    @Override
    public boolean stillValid(Player player) {
        Objects.requireNonNull(applicatorStack, "CamoApplicatorMenu#stillValid() called on the client");

        Inventory inv = player.getInventory();
        return inv.getSelectedSlot() == hotbarSlot && inv.getSelectedItem() == applicatorStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        levelAccess.execute((_, _) -> clearContainer(player, modInputSlot.container));
    }

    public int getHotbarSlot() {
        return hotbarSlot;
    }

    public CamoApplicatorConfig.Mode getMode() {
        return CamoApplicatorConfig.Mode.byId(modeDataSlot.get());
    }

    public int getSelectedSlot() {
        return selectedSlotDataSlot.get();
    }

    public int getModifierStack(FrameModifier modifier) {
        return modifierData.get(modifier);
    }

    public boolean isModifierActive(FrameModifier modifier) {
        return modifierData.isActive(modifier);
    }

    public void setMode(CamoApplicatorConfig.Mode mode) {
        Objects.requireNonNull(applicatorStack, "CamoApplicatorMenu#setMode() called on the client");

        CamoApplicatorUtils.updateConfig(applicatorStack, mode, CamoApplicatorConfig::withMode);
    }

    public void setSelectedSlot(int slot) {
        Objects.requireNonNull(applicatorStack, "CamoApplicatorMenu#setMode() called on the client");
        Objects.checkIndex(slot, CamoApplicatorContent.CAMO_COUNT);

        CamoApplicatorUtils.updateConfig(applicatorStack, slot, CamoApplicatorConfig::withSlot);
    }

    public void configureModifier(FrameModifier modifier, boolean active) {
        Objects.requireNonNull(applicatorStack, "CamoApplicatorMenu#setMode() called on the client");

        CamoApplicatorUtils.updateModifierInConfig(applicatorStack, modifier, active);
    }

    public void serializeModifierData(FriendlyByteBuf buf) {
        modifierData.serialize(buf);
    }

    @FunctionalInterface
    private interface CamoSlotFactory {
        Slot create(int slot, int x, int y);

        static CamoSlotFactory of(@Nullable ItemStack applicatorStack) {
            if (applicatorStack != null) {
                return (slot, x, y) -> new ServerSlot(slot, x, y, applicatorStack);
            }

            Container container = new SimpleContainer(CamoApplicatorContent.CAMO_COUNT);
            return (slot, x, y) -> new Slot(container, slot, x, y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return CamoContainerHelper.findCamoFactory(stack) != null;
                }
            };
        }
    }

    private static final class ServerSlot extends StackCopySlot {
        private final int slot;
        private final ItemStack applicatorStack;

        public ServerSlot(int slot, int x, int y, ItemStack applicatorStack) {
            super(slot, x, y);
            this.slot = slot;
            this.applicatorStack = applicatorStack;
        }

        @Override
        protected ItemStack getStackCopy() {
            return CamoApplicatorContent.of(applicatorStack).getCamoStack(slot).copy();
        }

        @Override
        protected void setStackCopy(ItemStack stack) {
            CamoApplicatorUtils.setCamoInContent(applicatorStack, slot, stack);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return CamoContainerHelper.findCamoFactory(stack) != null;
        }
    }

    private final class ModifierData {
        private final int[] stacks;
        private final DataSlot[] stackSlots;
        private final DataSlot[] activeSlots;

        ModifierData() {
            this.stacks = new int[MODIFIERS.length];
            this.stackSlots = new DataSlot[MODIFIERS.length];
            this.activeSlots = new DataSlot[MODIFIERS.length];
            Arrays.setAll(stackSlots, i -> addDataSlot(DataSlot.shared(stacks, i)));
            Arrays.setAll(activeSlots, _ -> addDataSlot(DataSlot.standalone()));
        }

        int get(FrameModifier modifier) {
            return stacks[modifier.ordinal()];
        }

        void set(FrameModifier modifier, int count) {
            stackSlots[modifier.ordinal()].set(count);
        }

        void update(int[] stacks, CamoApplicatorConfig config) {
            for (FrameModifier modifier : MODIFIERS) {
                set(modifier, stacks[modifier.ordinal()]);
                setActive(modifier, config.applyModifier(modifier));
            }
        }

        boolean isActive(FrameModifier modifier) {
            return activeSlots[modifier.ordinal()].get() > 0;
        }

        void setActive(FrameModifier modifier, boolean active) {
            activeSlots[modifier.ordinal()].set(active ? 1 : 0);
        }

        void serialize(FriendlyByteBuf buf) {
            for (FrameModifier modifier : MODIFIERS) {
                buf.writeVarInt(get(modifier));
                buf.writeBoolean(isActive(modifier));
            }
        }

        void deserialize(FriendlyByteBuf buf) {
            for (FrameModifier modifier : MODIFIERS) {
                set(modifier, buf.readVarInt());
                setActive(modifier, buf.readBoolean());
            }
        }
    }
}
