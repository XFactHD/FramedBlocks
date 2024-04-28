package xfacthd.framedblocks.common.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.data.cullupdate.ClientCullingUpdateTracker;
import xfacthd.framedblocks.common.net.payload.*;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class NetworkHandler
{
    private static final String PROTOCOL_VERSION = "3";

    private static <T extends CustomPacketPayload> StreamCodec<RegistryFriendlyByteBuf, T> codec(BiConsumer<T, RegistryFriendlyByteBuf> writer, StreamDecoder<RegistryFriendlyByteBuf, T> reader) {
        return StreamCodec.of((buf, p) -> writer.accept(p, buf), reader);
    }

    public static void onRegisterPayloads(final RegisterPayloadHandlersEvent event)
    {
        event.registrar(FramedConstants.MOD_ID)
                .versioned(PROTOCOL_VERSION)
                .playToServer(
                        SignUpdatePayload.ID,
                        codec(SignUpdatePayload::write, SignUpdatePayload::decode),
                        SignUpdatePayload::handle
                )
                .playToClient(
                        OpenSignScreenPayload.ID,
                        codec(OpenSignScreenPayload::write, OpenSignScreenPayload::new),
                        OpenSignScreenPayload::handle
                )
                .playToClient(
                        CullingUpdatePayload.ID,
                        codec(CullingUpdatePayload::write, CullingUpdatePayload::decode),
                        ClientCullingUpdateTracker::handleCullingUpdates
                )
                .playToServer(
                        SelectFramingSawRecipePayload.ID,
                        codec(SelectFramingSawRecipePayload::write, SelectFramingSawRecipePayload::new),
                        SelectFramingSawRecipePayload::handle
                )
                .playToServer(
                        EncodeFramingSawPatternPayload.ID,
                        codec(EncodeFramingSawPatternPayload::write, EncodeFramingSawPatternPayload::new),
                        EncodeFramingSawPatternPayload::handle
                );
    }



    private NetworkHandler() { }
}
