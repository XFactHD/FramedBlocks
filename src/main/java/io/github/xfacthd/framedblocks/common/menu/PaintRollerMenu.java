package io.github.xfacthd.framedblocks.common.menu;

import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class PaintRollerMenu extends AbstractContainerMenu {
    public static final Component TITLE = Utils.translate("title", "paint_roller");

    private final int hotbarSlot;
    @Nullable
    private final ItemStack rollerStack;

    public static PaintRollerMenu createServer(int containerId, Inventory inventory, int hotbarSlot) {
        return new PaintRollerMenu(containerId, hotbarSlot, inventory.getItem(hotbarSlot));
    }

    public static PaintRollerMenu createClient(int containerId, Inventory ignored) {
        return new PaintRollerMenu(containerId, -1, null);
    }

    private PaintRollerMenu(int containerId, int hotbarSlot, @Nullable ItemStack rollerStack) {
        super(FBContent.MENU_TYPE_PAINT_ROLLER.value(), containerId);
        this.hotbarSlot = hotbarSlot;
        this.rollerStack = rollerStack;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getInventory().getItem(hotbarSlot) == rollerStack;
    }
}
