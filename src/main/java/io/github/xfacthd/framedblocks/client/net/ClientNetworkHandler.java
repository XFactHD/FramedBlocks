package io.github.xfacthd.framedblocks.client.net;

import io.github.xfacthd.framedblocks.client.render.block.FramedChestRenderer;
import io.github.xfacthd.framedblocks.client.screen.FramedSignScreen;
import io.github.xfacthd.framedblocks.common.block.sign.FramedCeilingHangingSignBlock;
import io.github.xfacthd.framedblocks.common.block.sign.FramedStandingSignBlock;
import io.github.xfacthd.framedblocks.common.block.sign.FramedWallHangingSignBlock;
import io.github.xfacthd.framedblocks.common.block.sign.FramedWallSignBlock;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;
import io.github.xfacthd.framedblocks.common.data.cullupdate.ClientCullingUpdateTracker;
import io.github.xfacthd.framedblocks.common.net.payload.clientbound.ClientboundChestClosedPayload;
import io.github.xfacthd.framedblocks.common.net.payload.clientbound.ClientboundCullingUpdatePayload;
import io.github.xfacthd.framedblocks.common.net.payload.clientbound.ClientboundOpenSignScreenPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientNetworkHandler {
    public static void onRegisterPayloadHandlers(RegisterClientPayloadHandlersEvent event) {
        event.register(ClientboundOpenSignScreenPayload.TYPE, ClientNetworkHandler::handleOpenSignScreen);
        event.register(ClientboundCullingUpdatePayload.TYPE, ClientNetworkHandler::handleCullingUpdate);
        event.register(ClientboundChestClosedPayload.TYPE, ClientNetworkHandler::handleChestClosed);
    }

    private static void handleOpenSignScreen(ClientboundOpenSignScreenPayload payload, IPayloadContext ctx) {
        //noinspection ConstantConditions
        if (Minecraft.getInstance().level.getBlockEntity(payload.pos()) instanceof FramedSignBlockEntity be) {
            Minecraft.getInstance().gui.setScreen(switch (be.getBlockState().getBlock()) {
                case FramedStandingSignBlock _ -> FramedSignScreen.standing(be, payload.textSlot());
                case FramedWallSignBlock _ -> FramedSignScreen.wall(be, payload.textSlot());
                case FramedCeilingHangingSignBlock _, FramedWallHangingSignBlock _ -> FramedSignScreen.hanging(be, payload.textSlot());
                default -> throw new IllegalStateException("Unsupported sign block: " + be.getBlockState());
            });
        }
    }

    private static void handleCullingUpdate(ClientboundCullingUpdatePayload payload, IPayloadContext ctx) {
        ClientCullingUpdateTracker.handleCullingUpdates(payload.chunk(), payload.positions());
    }

    private static void handleChestClosed(ClientboundChestClosedPayload payload, IPayloadContext ctx) {
        FramedChestRenderer.addClosedChest(payload.pos(), Util.getNanos());
    }

    private ClientNetworkHandler() { }
}
