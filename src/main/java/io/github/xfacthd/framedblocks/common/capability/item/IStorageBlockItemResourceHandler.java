package io.github.xfacthd.framedblocks.common.capability.item;

import io.github.xfacthd.framedblocks.common.menu.FramedStorageMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public interface IStorageBlockItemResourceHandler extends ResourceHandler<ItemResource> {
    FramedStorageMenu createMenu(int windowId, Inventory inv);

    boolean stillValid(Player player);

    void open();

    void close();

    IndexModifier<ItemResource> getIndexModifier();
}
