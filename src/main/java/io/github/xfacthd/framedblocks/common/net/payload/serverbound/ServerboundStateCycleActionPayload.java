package io.github.xfacthd.framedblocks.common.net.payload.serverbound;

import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.data.attachment.PlacementStateCycleStorage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ByIdMap;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.IntFunction;

public record ServerboundStateCycleActionPayload(Action action) implements CustomPacketPayload {
    public static final Type<ServerboundStateCycleActionPayload> TYPE = Utils.payloadType("toggle_state_cycle");
    public static final StreamCodec<ByteBuf, ServerboundStateCycleActionPayload> STREAM_CODEC = Action.STREAM_CODEC
            .map(ServerboundStateCycleActionPayload::new, ServerboundStateCycleActionPayload::action);

    public static final ServerboundStateCycleActionPayload TOGGLE = new ServerboundStateCycleActionPayload(Action.TOGGLE);
    public static final ServerboundStateCycleActionPayload CYCLE_FORWARD = new ServerboundStateCycleActionPayload(Action.CYCLE_FORWARD);
    public static final ServerboundStateCycleActionPayload CYCLE_BACKWARD = new ServerboundStateCycleActionPayload(Action.CYCLE_BACKWARD);

    @Override
    public Type<ServerboundStateCycleActionPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();
        switch (action) {
            case TOGGLE -> PlacementStateCycleStorage.toggleActive(player);
            case CYCLE_FORWARD -> PlacementStateCycleStorage.cycleSelectedState(player, true);
            case CYCLE_BACKWARD -> PlacementStateCycleStorage.cycleSelectedState(player, false);
        }
    }

    public static ServerboundStateCycleActionPayload cycle(boolean forward) {
        return forward ? CYCLE_FORWARD : CYCLE_BACKWARD;
    }

    public enum Action {
        TOGGLE,
        CYCLE_FORWARD,
        CYCLE_BACKWARD,
        ;

        private static final IntFunction<Action> BY_ID = ByIdMap.continuous(Action::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        private static final StreamCodec<ByteBuf, Action> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Action::ordinal);
    }
}
