package io.github.xfacthd.framedblocks.api.ghost;

import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

public interface SimpleGhostRenderBehaviour extends GhostRenderBehaviour<Unit> {
    @Override
    @ApiStatus.NonExtendable
    default Unit getRenderContext(ItemStack stack) {
        return Unit.INSTANCE;
    }
}
