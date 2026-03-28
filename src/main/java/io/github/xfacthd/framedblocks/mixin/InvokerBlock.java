package io.github.xfacthd.framedblocks.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Block.class)
public interface InvokerBlock {
    @Invoker("registerDefaultState")
    void framedblocks$callRegisterDefaultState(BlockState state);
}
