package io.github.xfacthd.framedblocks.common.item.applicator;

import io.github.xfacthd.framedblocks.api.block.blockentity.FrameModifier;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.camo.applicator.CamoApplicator;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class CamoApplicatorImpl implements CamoApplicator {
    private static final FrameModifier[] MODIFIERS = FrameModifier.values();
    private final ItemStack stack;

    public CamoApplicatorImpl(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean apply(IFramedBlockEntity be, Player player, InteractionHand hand, CamoHandler camoHandler, ModifierHandler modHandler) {
        if (!canApplyCamo() || !canApplyModifiers()) {
            return false;
        }

        if (!applyCamo(camoHandler, player)) {
            return false;
        }

        applyModifiers(modHandler, player);

        return true;
    }

    private boolean canApplyCamo() {
        CamoApplicatorConfig config = CamoApplicatorConfig.of(stack);
        CamoApplicatorContent content = CamoApplicatorContent.of(stack);

        ItemStack camoStack = content.getCamoStack(config.selectedSlot());
        return CamoContainerHelper.findCamoFactory(camoStack) != null;
    }

    private boolean applyCamo(CamoHandler camoHandler, Player player) {
        CamoApplicatorConfig config = CamoApplicatorConfig.of(stack);

        ItemStack camoStack = CamoApplicatorContent.of(stack).getCamoStack(config.selectedSlot());
        CamoContainerFactory<?> factory = CamoContainerHelper.findCamoFactory(camoStack);
        if (factory == null) {
            return false;
        }

        if (camoHandler.accept(factory, makeCamoAccess(stack, config, player))) {
            CamoApplicatorUtils.updateConfigAfterApplication(stack, player);
            return true;
        }
        return false;
    }

    private boolean canApplyModifiers() {
        CamoApplicatorConfig config = CamoApplicatorConfig.of(stack);
        CamoApplicatorContent content = CamoApplicatorContent.of(stack);

        for (FrameModifier modifier : MODIFIERS) {
            if (config.applyModifier(modifier) && !content.hasModifier(modifier)) {
                return false;
            }
        }
        return true;
    }

    private void applyModifiers(ModifierHandler modHandler, Player player) {
        CamoApplicatorConfig config = CamoApplicatorConfig.of(stack);
        CamoApplicatorContent content = CamoApplicatorContent.of(stack);

        for (FrameModifier modifier : MODIFIERS) {
            if (config.applyModifier(modifier) && content.hasModifier(modifier)) {
                modHandler.accept(makeModifierAccess(stack, player, modifier));
            }
        }
    }

    private static ItemAccess makeCamoAccess(ItemStack stack, CamoApplicatorConfig config, Player player) {
        int slot = config.selectedSlot();
        Item originalItem = CamoApplicatorContent.of(stack).getCamoStack(slot).getItem();
        PlayerInventoryWrapper invWrapper = PlayerInventoryWrapper.of(player);
        SnapshotJournal<ItemStack> amountJournal = new SnapshotJournal<>() {
            @Override
            protected ItemStack createSnapshot() {
                return CamoApplicatorContent.of(stack).getCamoStack(slot);
            }

            @Override
            protected void revertToSnapshot(ItemStack snapshot) {
                CamoApplicatorUtils.setCamoInContent(stack, slot, snapshot);
            }
        };

        return new ItemAccess() {
            @Override
            public ItemResource getResource() {
                return ItemResource.of(CamoApplicatorContent.of(stack).getCamoStack(slot));
            }

            @Override
            public int getAmount() {
                return CamoApplicatorContent.of(stack).getCamoStack(slot).getCount();
            }

            @Override
            public int insert(ItemResource resource, int amount, TransactionContext transaction) {
                int inserted = 0;
                ItemStack camoStack = CamoApplicatorContent.of(stack).getCamoStack(slot);
                // Ensure dynamic fluid containers (i.e. tanks) can be re-inserted with modified contents
                if (resource.matches(camoStack) || (camoStack.isEmpty() && resource.is(originalItem))) {
                    int existing = getAmount();
                    int maxSize = camoStack.isEmpty() ? resource.getMaxStackSize() : camoStack.getMaxStackSize();
                    inserted = Math.min(amount, maxSize - existing);
                    if (inserted > 0) {
                        amountJournal.updateSnapshots(transaction);
                        CamoApplicatorUtils.setCamoInContent(stack, slot, resource.toStack(existing + inserted));
                    }
                }
                if (amount > inserted) {
                    invWrapper.placeItemBackInInventory(getResource(), amount - inserted, transaction);
                }
                // Any leftover is dropped, so the full amount can always be accepted
                return amount;
            }

            @Override
            public int extract(ItemResource resource, int amount, TransactionContext transaction) {
                if (!resource.equals(getResource())) {
                    return 0;
                }

                int toExtract = Math.min(amount, getAmount());
                // Don't check for creative mode to make auto-increment mode work in creative and avoid edge cases with dynamic fluid containers
                if (toExtract > 0) {
                    amountJournal.updateSnapshots(transaction);
                    CamoApplicatorUtils.updateCamoInContent(stack, slot, stack -> {
                        stack.shrink(toExtract);
                        return stack;
                    });
                }
                return toExtract;
            }
        };
    }

    private static ItemAccess makeModifierAccess(ItemStack stack, Player player, FrameModifier modifier) {
        ItemResource resource = modifier.getDefaultResource();
        SnapshotJournal<Integer> amountJournal = new SnapshotJournal<>() {
            @Override
            protected Integer createSnapshot() {
                return CamoApplicatorContent.of(stack).getModifierStack(modifier);
            }

            @Override
            protected void revertToSnapshot(Integer snapshot) {
                CamoApplicatorUtils.setModifierInContent(stack, modifier, snapshot);
            }
        };

        return new ItemAccess() {
            @Override
            public ItemResource getResource() {
                return resource;
            }

            @Override
            public int getAmount() {
                return CamoApplicatorContent.of(stack).getModifierStack(modifier);
            }

            @Override
            public int insert(ItemResource resource, int amount, TransactionContext transaction) {
                return 0;
            }

            @Override
            public int extract(ItemResource resource, int amount, TransactionContext transaction) {
                int toExtract = Math.min(amount, getAmount());
                if (toExtract > 0 && !player.hasInfiniteMaterials()) {
                    amountJournal.updateSnapshots(transaction);
                    CamoApplicatorUtils.updateModifierInContent(stack, modifier, -toExtract);
                }
                return toExtract;
            }
        };
    }
}
