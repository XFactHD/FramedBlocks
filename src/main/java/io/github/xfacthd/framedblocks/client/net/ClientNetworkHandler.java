package io.github.xfacthd.framedblocks.client.net;

import io.github.xfacthd.framedblocks.client.screen.FramedSignScreen;
import io.github.xfacthd.framedblocks.common.block.sign.FramedCeilingHangingSignBlock;
import io.github.xfacthd.framedblocks.common.block.sign.FramedStandingSignBlock;
import io.github.xfacthd.framedblocks.common.block.sign.FramedWallHangingSignBlock;
import io.github.xfacthd.framedblocks.common.block.sign.FramedWallSignBlock;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedSignBlockEntity;
import io.github.xfacthd.framedblocks.common.data.cullupdate.ClientCullingUpdateTracker;
import io.github.xfacthd.framedblocks.common.net.payload.clientbound.ClientboundCullingUpdatePayload;
import io.github.xfacthd.framedblocks.common.net.payload.clientbound.ClientboundOpenSignScreenPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientNetworkHandler
{
    public static void onRegisterPayloadHandlers(RegisterClientPayloadHandlersEvent event)
    {
        event.register(ClientboundOpenSignScreenPayload.TYPE, ClientNetworkHandler::handleOpenSignScreen);
        event.register(ClientboundCullingUpdatePayload.TYPE, ClientNetworkHandler::handleCullingUpdate);
    }

    private static void handleOpenSignScreen(ClientboundOpenSignScreenPayload payload, IPayloadContext ctx)
    {
        //noinspection ConstantConditions
        if (Minecraft.getInstance().level.getBlockEntity(payload.pos()) instanceof FramedSignBlockEntity be)
        {
            Minecraft.getInstance().setScreen(switch (be.getBlockState().getBlock())
            {
                case FramedStandingSignBlock block -> FramedSignScreen.standing(be, payload.frontText());
                case FramedWallSignBlock block -> FramedSignScreen.wall(be, payload.frontText());
                case FramedCeilingHangingSignBlock block -> FramedSignScreen.hanging(be, payload.frontText());
                case FramedWallHangingSignBlock block -> FramedSignScreen.hanging(be, payload.frontText());
                default -> throw new IllegalStateException("Unsupported sign block: " + be.getBlockState());
            });
        }
    }

    private static void handleCullingUpdate(ClientboundCullingUpdatePayload payload, IPayloadContext ctx)
    {
        ClientCullingUpdateTracker.handleCullingUpdates(payload.chunk(), payload.positions());
    }

    private ClientNetworkHandler() { }
}
