package xfacthd.framedblocks.common.capability;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import xfacthd.framedblocks.common.menu.FramedStorageMenu;

public interface IStorageBlockItemHandler extends IItemHandlerModifiable
{
    FramedStorageMenu createMenu(int windowId, Inventory inv);

    boolean stillValid(Player player);

    void open();

    void close();
}
