package xfacthd.framedblocks.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.screen.FramedSignScreen;
import xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;

import java.lang.invoke.MethodHandle;
import java.util.Objects;

public final class ClientAccess
{
    private static final MethodHandle MPGM_DESTROY_DELAY = Utils.unreflectFieldSetter(MultiPlayerGameMode.class, "f_105195_");

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
        try
        {
            // 5 ticks is the delay used for continuous block breaking in creative
            MPGM_DESTROY_DELAY.invokeExact(gameMode, 5);
        }
        catch (Throwable e)
        {
            throw new RuntimeException("An error occured while resetting destroy delay", e);
        }
    }



    private ClientAccess() { }
}
