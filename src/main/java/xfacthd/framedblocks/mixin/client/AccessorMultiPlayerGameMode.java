package xfacthd.framedblocks.mixin.client;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiPlayerGameMode.class)
public interface AccessorMultiPlayerGameMode
{
    @Accessor("destroyDelay")
    void framedblocks$setDestroyDelay(int delay);
}
