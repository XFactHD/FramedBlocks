package io.github.xfacthd.framedblocks.common.data.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.util.FramedUtils;
import io.github.xfacthd.framedblocks.mixin.InvokerBlockItem;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class PlacementStateCycleStorage {
    private static final Codec<BlockItem> ITEM_CODEC = BuiltInRegistries.ITEM.byNameCodec()
            .comapFlatMap(FramedUtils.validateSubType(BlockItem.class), Function.identity());
    public static final MapCodec<PlacementStateCycleStorage> CODEC = Codec.unboundedMap(ITEM_CODEC, CycleState.CODEC)
            .fieldOf("states")
            .xmap(PlacementStateCycleStorage::fixMapType, Function.identity())
            .xmap(PlacementStateCycleStorage::new, storage -> storage.states);
    private static final StreamCodec<RegistryFriendlyByteBuf, BlockItem> ITEM_STREAM_CODEC = ByteBufCodecs.registry(Registries.ITEM)
            .map(FramedUtils.assertSubType(BlockItem.class), Function.identity());
    public static final Component MSG_ENABLED = Utils.translate("msg", "state_cycling.enabled");
    public static final Component MSG_DISABLED = Utils.translate("msg", "state_cycling.disabled");

    private final Reference2ObjectMap<BlockItem, CycleState> states;
    private final ReferenceSet<BlockItem> modified = new ReferenceOpenHashSet<>();

    public PlacementStateCycleStorage() {
        this(new Reference2ObjectOpenHashMap<>());
    }

    private PlacementStateCycleStorage(Reference2ObjectMap<BlockItem, CycleState> states) {
        this.states = states;
    }

    public static PlacementStateCycleStorage get(Player player) {
        return player.getData(FBContent.DA_TYPE_STATE_CYCLE_STORAGE);
    }

    public static boolean isActive(Player player, BlockItem item) {
        CycleState cycleState = get(player).getState(item);
        return cycleState != null && cycleState.active;
    }

    public static void toggleActive(ServerPlayer player) {
        if (player.getMainHandItem().getItem() instanceof BlockItem item && item instanceof IFramedBlockItem blockItem) {
            if (!blockItem.getStateCycleSpec().canCycle()) {
                return;
            }

            PlacementStateCycleStorage storage = get(player);
            boolean active = storage.states.computeIfAbsent(item, CycleState::create).toggleActive(player, item);
            storage.modified.add(item);
            player.syncData(FBContent.DA_TYPE_STATE_CYCLE_STORAGE);
            player.sendOverlayMessage(active ? MSG_ENABLED : MSG_DISABLED);
        }
    }

    public static @Nullable BlockState getSelectedState(Player player, BlockItem item) {
        CycleState cycleState = get(player).getState(item);
        return cycleState != null ? cycleState.state : null;
    }

    public static void cycleSelectedState(ServerPlayer player, boolean forward) {
        if (player.getMainHandItem().getItem() instanceof BlockItem item && item instanceof IFramedBlockItem framedItem) {
            PlacementStateCycleStorage storage = get(player);
            CycleState cycleState = storage.getState(item);
            if (cycleState == null || !cycleState.active) {
                return;
            }

            StateCycleSpec spec = framedItem.getStateCycleSpec();
            if (!spec.canCycle()) {
                return;
            }

            BlockState prevState = cycleState.state;
            BlockState newState = spec.cycle(prevState, forward);
            if (newState != prevState) {
                cycleState.state = newState;
                storage.modified.add(item);
                player.syncData(FBContent.DA_TYPE_STATE_CYCLE_STORAGE);
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private @Nullable CycleState getState(BlockItem item) {
        return states.get(item);
    }

    private static Reference2ObjectMap<BlockItem, CycleState> fixMapType(Map<BlockItem, CycleState> map) {
        return new Reference2ObjectOpenHashMap<>(map);
    }

    private static final class CycleState {
        private static final Codec<CycleState> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                BlockState.CODEC.fieldOf("state").forGetter(cycleState -> cycleState.state),
                Codec.BOOL.fieldOf("active").forGetter(cycleState -> cycleState.active)
        ).apply(inst, CycleState::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, CycleState> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY),
                cycleState -> cycleState.state,
                ByteBufCodecs.BOOL,
                cycleState -> cycleState.active,
                CycleState::new
        );
        private static final StreamCodec<RegistryFriendlyByteBuf, Optional<CycleState>> OPTIONAL_STREAM_CODEC = ByteBufCodecs.optional(STREAM_CODEC);

        private BlockState state;
        private boolean active;

        private static CycleState create(BlockItem item) {
            StateCycleSpec spec = ((IFramedBlockItem) item).getStateCycleSpec();
            return new CycleState(spec.getInitialState(null), false);
        }

        private CycleState(BlockState state, boolean active) {
            this.state = state;
            this.active = active;
        }

        private boolean toggleActive(ServerPlayer player, BlockItem item) {
            if (!active) {
                BlockHitResult hitResult = Item.getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.NONE);
                BlockPlaceContext ctx = new BlockPlaceContext(player, InteractionHand.MAIN_HAND, player.getMainHandItem(), hitResult);
                BlockState placementState = ((InvokerBlockItem) item).framedblocks$callGetPlacementState(ctx);
                if (placementState != null) {
                    StateCycleSpec spec = ((IFramedBlockItem) item).getStateCycleSpec();
                    state = spec.getInitialState(placementState);
                }
            }
            active = !active;
            return active;
        }
    }

    public static final class SyncHandler implements AttachmentSyncHandler<PlacementStateCycleStorage> {
        @Override
        public boolean sendToPlayer(IAttachmentHolder holder, ServerPlayer target) {
            return target == holder;
        }

        @Override
        public void write(RegistryFriendlyByteBuf buf, PlacementStateCycleStorage attachment, boolean initialSync) {
            ReferenceSet<BlockItem> toSend = initialSync ? attachment.states.keySet() : attachment.modified;

            buf.writeVarInt(toSend.size());
            if (!toSend.isEmpty()) {
                for (BlockItem item : toSend) {
                    ITEM_STREAM_CODEC.encode(buf, item);
                    CycleState.OPTIONAL_STREAM_CODEC.encode(buf, Optional.ofNullable(attachment.getState(item)));
                }
                attachment.modified.clear();
            }
        }

        @Override
        public @Nullable PlacementStateCycleStorage read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable PlacementStateCycleStorage attachment) {
            int count = buf.readVarInt();
            if (count == 0) {
                return attachment;
            }

            if (attachment == null) {
                attachment = new PlacementStateCycleStorage();
            }

            Reference2ObjectMap<BlockItem, CycleState> states = attachment.states;
            for (int i = 0; i < count; i++) {
                BlockItem item = ITEM_STREAM_CODEC.decode(buf);
                Optional<CycleState> cycleState = CycleState.OPTIONAL_STREAM_CODEC.decode(buf);
                if (cycleState.isPresent()) {
                    states.put(item, cycleState.get());
                } else {
                    states.remove(item);
                }
            }
            return attachment;
        }
    }
}
