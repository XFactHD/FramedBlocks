package xfacthd.framedblocks.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import xfacthd.framedblocks.client.screen.FramedSignScreen;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;
import xfacthd.framedblocks.mixin.client.AccessorMultiPlayerGameMode;

import java.util.Objects;

public final class ClientAccess
{
    // For some reason vanilla does not have a constant for this???
    private static final int DEFAULT_DESTROY_DELAY = 5;

    public static void openSignScreen(BlockPos pos, boolean front)
    {
        //noinspection ConstantConditions
        if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof FramedSignBlockEntity be)
        {
            Minecraft.getInstance().setScreen(new FramedSignScreen(be, front));
        }
    }

    public static void resetDestroyDelay()
    {
        if (Objects.requireNonNull(Minecraft.getInstance().player).isCreative())
        {
            return;
        }

        MultiPlayerGameMode gameMode = Objects.requireNonNull(Minecraft.getInstance().gameMode);
        ((AccessorMultiPlayerGameMode) gameMode).framedblocks$setDestroyDelay(DEFAULT_DESTROY_DELAY);
    }



    private ClientAccess() { }
}
