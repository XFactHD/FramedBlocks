package io.github.xfacthd.framedblocks.common.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

@FunctionalInterface
public interface SlotFactory {
    Slot create(Container container, int slot, int x, int y);
}
