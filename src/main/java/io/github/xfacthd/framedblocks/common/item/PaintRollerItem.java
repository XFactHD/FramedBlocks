package io.github.xfacthd.framedblocks.common.item;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.FramedToolType;
import io.github.xfacthd.framedblocks.common.data.component.PaintRollerContents;
import io.github.xfacthd.framedblocks.common.data.dynreg.BlockOverlayCache;
import io.github.xfacthd.framedblocks.common.menu.PaintRollerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Optional;

public final class PaintRollerItem extends FramedToolItem {
    public PaintRollerItem(FramedToolType type, Properties props) {
        super(type, props.component(FBContent.DC_TYPE_PAINT_ROLLER_CONTENTS, PaintRollerContents.NO_OVERLAY));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            int slot = player.getInventory().getSelectedSlot();
            player.openMenu(new MenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                    return PaintRollerMenu.createServer(containerId, inventory, slot);
                }

                @Override
                public Component getDisplayName() {
                    return PaintRollerMenu.TITLE;
                }
            });
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        if (level.getBlockEntity(context.getClickedPos()) instanceof FramedBlockEntity be) {
            PaintRollerContents storage = PaintRollerContents.get(stack);
            boolean removeOverlay = !storage.hasOverlay();

            if (be.hasOverlay() != removeOverlay || (!removeOverlay && storage.isDepleted())) {
                return InteractionResult.FAIL;
            }

            if (!level.isClientSide()) {
                Player player = context.getPlayer();
                if (player != null) {
                    if (removeOverlay) {
                        BlockOverlay oldOverlay = Objects.requireNonNull(be.getOverlay()).value();
                        Utils.giveToPlayer(player, new ItemStack(oldOverlay.sourceItem()));
                    } else if (!player.hasInfiniteMaterials()) {
                        stack.set(FBContent.DC_TYPE_PAINT_ROLLER_CONTENTS, storage.shrink());
                    }
                }

                be.setOverlay(removeOverlay ? null : storage.overlay());
            }
            return InteractionResult.SUCCESS;
        }
        // TODO: switch to PASS when the PaintRollerMenu is removed in favor of a guidebook
        return InteractionResult.FAIL;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess carriedItem) {
        boolean client = player.level().isClientSide();
        BlockOverlayCache cache = BlockOverlayCache.get(client);
        if (clickAction == ClickAction.PRIMARY && cache.isValidItem(other)) {
            PaintRollerContents contents = PaintRollerContents.get(self);
            if (slot.allowModification(player) && contents.canInsert(other)) {
                self.set(FBContent.DC_TYPE_PAINT_ROLLER_CONTENTS, contents.insert(cache, other));
                player.containerMenu.slotsChanged(player.getInventory());
                playInsertSound(player);
            } else {
                playInsertFailSound(player);
            }
            return true;
        } else if (clickAction == ClickAction.SECONDARY && other.isEmpty()) {
            PaintRollerContents contents = PaintRollerContents.get(self);
            if (slot.allowModification(player) && contents.hasOverlay() && !contents.isDepleted()) {
                self.set(FBContent.DC_TYPE_PAINT_ROLLER_CONTENTS, contents.extract(carriedItem));
                player.containerMenu.slotsChanged(player.getInventory());
            }
            playRemoveSound(player);
            return true;
        }
        return false;
    }

    private static void playRemoveSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertFailSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.ofNullable(stack.get(FBContent.DC_TYPE_PAINT_ROLLER_CONTENTS));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return PaintRollerContents.get(stack).hasOverlay();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float fillPercent = PaintRollerContents.getFillPercent(stack);
        return Mth.clamp((int) Math.floor(13.0F * fillPercent), 0, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float fillPercent = PaintRollerContents.getFillPercent(stack);
        return Mth.hsvToRgb(fillPercent / 3.0F, 1.0F, 1.0F);
    }
}
