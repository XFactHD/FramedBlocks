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
                        SignUpdatePayload.TYPE,
                        SignUpdatePayload.CODEC,
                        SignUpdatePayload::handle
                )
                .playToClient(
                        OpenSignScreenPayload.TYPE,
                        OpenSignScreenPayload.CODEC,
                        OpenSignScreenPayload::handle
                )
                .playToClient(
                        CullingUpdatePayload.TYPE,
                        CullingUpdatePayload.CODEC,
                        ClientCullingUpdateTracker::handleCullingUpdates
                )
                .playToServer(
                        SelectFramingSawRecipePayload.TYPE,
                        SelectFramingSawRecipePayload.CODEC,
                        SelectFramingSawRecipePayload::handle
                )
                .playToServer(
                        EncodeFramingSawPatternPayload.TYPE,
                        EncodeFramingSawPatternPayload.CODEC,
                        EncodeFramingSawPatternPayload::handle
                );
    }



    private NetworkHandler() { }
}
