package io.github.xfacthd.framedblocks.mixin;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockItem.class)
public interface InvokerBlockItem {
    @Invoker("getPlacementState")
    @Nullable BlockState framedblocks$callGetPlacementState(BlockPlaceContext ctx);
}
