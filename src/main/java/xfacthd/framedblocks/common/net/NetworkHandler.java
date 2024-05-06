package xfacthd.framedblocks.common.net;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import xfacthd.framedblocks.common.data.cullupdate.ClientCullingUpdateTracker;
import xfacthd.framedblocks.common.net.payload.*;

public final class NetworkHandler
{
    private static final String PROTOCOL_VERSION = "3";

    public static void onRegisterPayloads(final RegisterPayloadHandlersEvent event)
    {
        event.registrar(PROTOCOL_VERSION)
                .executesOn(HandlerThread.NETWORK)
                .playToServer(
                        ServerboundSignUpdatePayload.TYPE,
                        ServerboundSignUpdatePayload.CODEC,
                        ServerboundSignUpdatePayload::handle
                )
                .playToClient(
                        ClientboundOpenSignScreenPayload.TYPE,
                        ClientboundOpenSignScreenPayload.CODEC,
                        ClientboundOpenSignScreenPayload::handle
                )
                .playToClient(
                        ClientboundCullingUpdatePayload.TYPE,
                        ClientboundCullingUpdatePayload.CODEC,
                        ClientCullingUpdateTracker::handleCullingUpdates
                )
                .playToServer(
                        ServerboundSelectFramingSawRecipePayload.TYPE,
                        ServerboundSelectFramingSawRecipePayload.CODEC,
                        ServerboundSelectFramingSawRecipePayload::handle
                )
                .playToServer(
                        ServerboundEncodeFramingSawPatternPayload.TYPE,
                        ServerboundEncodeFramingSawPatternPayload.CODEC,
                        ServerboundEncodeFramingSawPatternPayload::handle
                );
    }



    private NetworkHandler() { }
}
