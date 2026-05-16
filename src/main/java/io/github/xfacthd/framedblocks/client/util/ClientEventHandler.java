package io.github.xfacthd.framedblocks.client.util;

import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import io.github.xfacthd.framedblocks.api.render.fakelevel.ColorResolvingLevel;
import io.github.xfacthd.framedblocks.common.crafting.saw.FramingSawRecipeCache;
import io.github.xfacthd.framedblocks.common.data.dynreg.BlockOverlayCache;
import io.github.xfacthd.framedblocks.common.net.payload.serverbound.ServerboundStateCycleActionPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public final class ClientEventHandler {
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        if (event.getConnection() == null) {
            // For some reason, vanilla performs a disconnect before joining an SP world or a server or closing the game
            return;
        }

        FramingSawRecipeCache.get(true).clear();
        BlockOverlayCache.get(true).clear();
        CacheCleaner.clearModelCaches(CacheCleaner.Reason.DISCONNECT);
        ColorResolvingLevel.clearBiomeReference();
    }

    public static void onScrollInput(InputEvent.MouseScrollingEvent event) {
        double deltaY = event.getScrollDeltaY();
        if (Mth.equal(deltaY, 0F)) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof IFramedBlockItem item) || !item.isStateCyclingActive(player)) {
            return;
        }

        if (KeyMappings.UNLOCK_STATE_CYCLE.get().isDown()) {
            boolean forward = deltaY < 0;
            ClientPacketDistributor.sendToServer(ServerboundStateCycleActionPayload.cycle(forward));
            event.setCanceled(true);
        }
    }

    private ClientEventHandler() { }
}
