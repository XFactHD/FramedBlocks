package io.github.xfacthd.framedblocks.common.item.applicator;

import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.FramedToolType;
import io.github.xfacthd.framedblocks.common.item.FramedToolItem;
import io.github.xfacthd.framedblocks.common.menu.CamoApplicatorMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public final class CamoApplicatorItem extends FramedToolItem {
    public CamoApplicatorItem(FramedToolType type, Properties props) {
        super(type, props
                .component(FBContent.DC_TYPE_APPLICATOR_CONTENT, CamoApplicatorContent.EMPTY)
                .component(FBContent.DC_TYPE_APPLICATOR_CONFIG, CamoApplicatorConfig.DEFAULT)
        );
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            player.openMenu(new MenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                    return CamoApplicatorMenu.createServer(containerId, inventory);
                }

                @Override
                public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
                    CamoApplicatorMenu applicatorMenu = (CamoApplicatorMenu) menu;
                    buffer.writeVarInt(applicatorMenu.getHotbarSlot());
                    CamoApplicatorConfig.Mode.STREAM_CODEC.encode(buffer, applicatorMenu.getMode());
                    applicatorMenu.serializeModifierData(buffer);
                }

                @Override
                public Component getDisplayName() {
                    return CamoApplicatorMenu.TITLE;
                }
            });
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    @Override
    public boolean canFitInsideContainerItems(ItemStack stack) {
        return stack.getOrDefault(FBContent.DC_TYPE_APPLICATOR_CONTENT, CamoApplicatorContent.EMPTY).isEmpty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> appender, TooltipFlag flag) {
        CamoApplicatorConfig.of(stack).addToTooltip(context, appender, flag, stack);
    }
}
