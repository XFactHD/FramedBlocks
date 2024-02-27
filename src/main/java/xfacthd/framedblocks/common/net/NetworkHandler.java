package xfacthd.framedblocks.common.net;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.data.cullupdate.ClientCullingUpdateTracker;
import xfacthd.framedblocks.common.net.payload.*;

public final class NetworkHandler
{
    private static final String PROTOCOL_VERSION = "3";

    public static void onRegisterPayloads(final RegisterPayloadHandlerEvent event)
    {
        event.registrar(FramedConstants.MOD_ID)
                .versioned(PROTOCOL_VERSION)
                .play(
                        SignUpdatePayload.ID,
                        SignUpdatePayload::decode,
                        handler -> handler.server(SignUpdatePayload::handle)
                )
                .play(
                        OpenSignScreenPayload.ID,
                        OpenSignScreenPayload::new,
                        handler -> handler.client(OpenSignScreenPayload::handle)
                )
                .play(
                        CullingUpdatePayload.ID,
                        CullingUpdatePayload::decode,
                        handler -> handler.client(ClientCullingUpdateTracker::handleCullingUpdates)
                )
                .play(
                        SelectFramingSawRecipePayload.ID,
                        SelectFramingSawRecipePayload::new,
                        handler -> handler.server(SelectFramingSawRecipePayload::handle)
                )
                .play(
                        EncodeFramingSawPatternPayload.ID,
                        EncodeFramingSawPatternPayload::new,
                        handler -> handler.server(EncodeFramingSawPatternPayload::handle)
                );
    }



    private NetworkHandler() { }
}
