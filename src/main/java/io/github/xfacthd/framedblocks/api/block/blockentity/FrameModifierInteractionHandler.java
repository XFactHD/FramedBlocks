package io.github.xfacthd.framedblocks.api.block.blockentity;

import io.github.xfacthd.framedblocks.api.util.ConfigView;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;

sealed class FrameModifierInteractionHandler {
    static final FrameModifierInteractionHandler DEFAULT = new FrameModifierInteractionHandler();

    boolean apply(FramedBlockEntity blockEntity, FrameModifier modifier, ItemAccess itemAccess, ItemResource resource) {
        boolean server = !blockEntity.level().isClientSide();
        if (Utils.extractOneFromItemAccess(itemAccess, server)) {
            if (server) {
                modifier.setActive(blockEntity, true);
            }
            return true;
        }
        return false;
    }

    boolean remove(FramedBlockEntity blockEntity, FrameModifier modifier, ItemStack stack, Player player, InteractionHand hand) {
        if (!blockEntity.level().isClientSide()) {
            modifier.setActive(blockEntity, false);
            Utils.giveToPlayer(player, modifier.getDefaultStack());
        }
        return true;
    }

    static final class Intangibility extends FrameModifierInteractionHandler {
        @Override
        public boolean apply(FramedBlockEntity blockEntity, FrameModifier modifier, ItemAccess itemAccess, ItemResource resource) {
            if (!ConfigView.Server.INSTANCE.enableIntangibility() || !blockEntity.getBlockType().allowMakingIntangible()) {
                return false;
            }
            return super.apply(blockEntity, modifier, itemAccess, resource);
        }

        @Override
        public boolean remove(FramedBlockEntity blockEntity, FrameModifier modifier, ItemStack stack, Player player, InteractionHand hand) {
            return player.isShiftKeyDown() && super.remove(blockEntity, modifier, stack, player, hand);
        }
    }

    static final class Reinforcement extends FrameModifierInteractionHandler {
        @Override
        public boolean remove(FramedBlockEntity blockEntity, FrameModifier modifier, ItemStack stack, Player player, InteractionHand hand) {
            if (super.remove(blockEntity, modifier, stack, player, hand)) {
                if (!player.level().isClientSide() && !player.isCreative()) {
                    stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
                }
                return true;
            }
            return false;
        }
    }
}
