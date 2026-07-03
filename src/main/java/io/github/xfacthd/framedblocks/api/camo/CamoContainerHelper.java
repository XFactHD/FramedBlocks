package io.github.xfacthd.framedblocks.api.camo;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import io.github.xfacthd.framedblocks.api.FramedBlocksAPI;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.internal.InternalAPI;
import io.github.xfacthd.framedblocks.api.util.network.ValidatingDecoder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

/// Various helpers for interacting with camo containers, hiding ugly casts.
public final class CamoContainerHelper {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Registry<CamoContainerFactory<?>> REGISTRY = FramedBlocksAPI.INSTANCE.getCamoContainerFactoryRegistry();
    /// Type-dispatched codec for (de)serializing arbitrary camos.
    public static final Codec<CamoContainer<?, ?>> CODEC = REGISTRY.byNameCodec()
            .dispatch(CamoContainer::getFactory, CamoContainerFactory::codec);
    /// Type-dispatched stream codec for (de)serializing arbitrary camos from/to a network buffer.
    public static final StreamCodec<RegistryFriendlyByteBuf, CamoContainer<?, ?>> STREAM_CODEC = ByteBufCodecs.registry(REGISTRY.key())
            .<CamoContainer<?, ?>>dispatch(CamoContainer::getFactory, CamoContainerFactory::streamCodec)
            .apply(ValidatingDecoder.of(CamoContainerHelper::validateFromNetwork));

    /// Save the given the [CamoContainer] to an NBT tag for sync over the network.
    ///
    /// @param output The output to write the camo to
    /// @param camo   The camo to serialize
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void writeToNetwork(ValueOutput output, CamoContainer<?, ?> camo) {
        CamoContainerFactory factory = camo.getFactory();
        int id = REGISTRY.getId(factory);
        Preconditions.checkState(id != -1, "Attempted to get sync ID for unregistered CamoContainerFactory");

        output.putInt("type", REGISTRY.getId(factory));
        factory.writeToNetwork(output, camo);
    }

    /// Reconstruct the [CamoContainer] from the given NBT tag from a network packet.
    ///
    /// @param input The input to read the camo from
    /// @return the reconstructed camo container
    public static CamoContainer<?, ?> readFromNetwork(Optional<ValueInput> input) {
        if (input.isEmpty()) {
            return EmptyCamoContainer.EMPTY;
        }

        ValueInput valueInput = input.get();
        int id = valueInput.getIntOr("type", -1);
        CamoContainerFactory<?> factory = REGISTRY.byId(id);
        if (factory == null) {
            LOGGER.error("Received unknown CamoContainer with ID {} from network, dropping!", id);
            return EmptyCamoContainer.EMPTY;
        }
        return validateFromNetwork(factory.readFromNetwork(valueInput));
    }

    private static CamoContainer<?, ?> validateFromNetwork(CamoContainer<?, ?> container) {
        return validateCamo(container) ? container : EmptyCamoContainer.EMPTY;
    }

    /// Validate the given [CamoContainer] after loading from disk or network
    ///
    /// @param camo The camo to validate
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static boolean validateCamo(CamoContainer<?, ?> camo) {
        CamoContainerFactory factory = camo.getFactory();
        return factory.validateCamo(camo);
    }

    /// Remove the camo from a framed block and refund the resources to the player.
    ///
    /// Called on server and client side.
    ///
    /// @param camo       The camo to remove
    /// @param level      The level the framed block is in
    /// @param pos        The position of the framed block
    /// @param player     The player interacting with the framed block
    /// @param itemAccess The item access to pass refunded resources to
    /// @return true if the camo was successfully given to the player and can be removed
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static boolean removeCamo(CamoContainer<?, ?> camo, Level level, BlockPos pos, Player player, ItemAccess itemAccess) {
        CamoContainerFactory factory = camo.getFactory();
        return factory.removeCamo(level, pos, player, itemAccess, camo);
    }

    /// Construct an [ItemStack] of the item the given camo was made from, to be dropped when the enclosing
    /// block is destroyed or the material list of a blueprint is being computed if the camo can be trivially
    /// converted to a stack, otherwise returns an empty stack.
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static ItemStack dropCamo(CamoContainer<?, ?> camo) {
        CamoContainerFactory factory = camo.getFactory();
        return factory.dropCamo(camo);
    }

    /// {@return the camo container factory to use for applying the given stack as a camo or `null` if none exists}
    ///
    /// @param stack The stack to use for applying a camo
    public static @Nullable CamoContainerFactory<?> findCamoFactory(ItemStack stack) {
        return stack.isEmpty() ? null : InternalAPI.INSTANCE.findCamoFactory(stack);
    }

    /// {@return the crafting handler to use for applying the given stack as a camo in a crafting recipe or `null` if none exists}
    ///
    /// @param stack The stack to use for applying a camo
    public static @Nullable CamoCraftingHandler<?> findCraftingHandler(ItemStack stack) {
        CamoContainerFactory<?> factory = findCamoFactory(stack);
        return factory != null ? factory.getCraftingHandler() : null;
    }

    /// {@return whether the given stack can be used to remove the camo container from a framed block}
    ///
    /// @param container The camo container to remove
    /// @param stack     The item to use for removing the camo
    public static boolean isValidRemovalTool(CamoContainer<?, ?> container, ItemStack stack) {
        return !container.isEmpty() && !stack.isEmpty() && InternalAPI.INSTANCE.isValidRemovalTool(container, stack);
    }

    /// {@return whether the given plant can survive on the camo(s) of framed blocks potentially surrounding it}
    ///
    /// @param camoState The state used as camo on the framed block
    /// @param level     The level the blocks are in
    /// @param pos       The position of the framed block
    /// @param side      The side of the framed block the plant is on
    /// @param plant     The state of the plant block
    public static TriState canPlantSurviveOnCamo(BlockState camoState, BlockGetter level, BlockPos pos, Direction side, BlockState plant) {
        if (!camoState.isAir() && level instanceof LevelReader reader) {
            BlockPos plantPos = pos.relative(side);
            if (reader instanceof CamoResolvingLevelReader) {
                LOGGER.warn("Encountered unexpected recursion in plant sustainability check for plant '{}' at {} on side {} of a framed block", plant, plantPos, side);
                return TriState.DEFAULT;
            }
            LevelReader camoResolvingLevel = new CamoResolvingLevelReader(reader, plantPos);
            if (plant.canSurvive(camoResolvingLevel, plantPos)) {
                return TriState.TRUE;
            }
        }
        return TriState.DEFAULT;
    }

    /// Handle interactions with the given camo in the provided context. If the interaction changes the camo data,
    /// then a new camo container with the new data will be returned, otherwise the given camo should be returned.
    ///
    /// @param level  The level the framed block holding the camo is in
    /// @param pos    The position of the framed block holding the camo
    /// @param player The player interacting with the framed block
    /// @param camo   The camo container the player is interacting with
    /// @param stack  The stack used to interact with the framed block
    /// @param hand   The hand holding the stack used to interact with the framed block
    /// @return a new camo container if the camo data changes from this interaction, otherwise the given one
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static CamoContainer<?, ?> handleCamoInteraction(Level level, BlockPos pos, Player player, CamoContainer<?, ?> camo, ItemStack stack, InteractionHand hand) {
        if (!camo.isEmpty() && !stack.isEmpty()) {
            CamoContainerFactory factory = camo.getFactory();
            return factory.handleInteraction(level, pos, player, camo, stack, hand);
        }
        return camo;
    }

    public static final class Client {
        /// {@return the {@link BlockStateModel} to be used as geometry source for the given camo}
        ///
        /// @param content The camo to query the model of
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static BlockStateModel getOrCreateModel(CamoContent<?> content) {
            CamoContentClientHandler clientHandler = content.getClientHandler();
            return clientHandler.getOrCreateModel(content);
        }

        /// {@return the amount of tint "layers" used by the given camo}
        ///
        /// @param container The camo to query the tint count for
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static int getTintCount(CamoContainer<?, ?> container) {
            CamoContainerClientHandler clientHandler = container.getClientHandler();
            return clientHandler.getTintCount(container);
        }

        /// Collect the tint values of the provided camo when rendered as part of a block.
        ///
        /// @param container The camo whose tint values are being queried
        /// @param level     The level in which the camo is being rendered
        /// @param pos       The position at which the camo is being rendered
        /// @param tintList  The list to append the tint values to
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static void collectTintValues(CamoContainer<?, ?> container, BlockAndTintGetter level, BlockPos pos, IntList tintList) {
            CamoContainerClientHandler clientHandler = container.getClientHandler();
            clientHandler.collectTintValues(container, level, pos, tintList);
        }

        /// Collect the tint values of the provided camo when rendered as part of an item.
        ///
        /// @param container The camo whose tint values are being queried
        /// @param stack     The stack holding the camo
        /// @param tintList  The list to append the tint values to
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public static void collectTintValues(CamoContainer<?, ?> container, ItemStack stack, IntList tintList) {
            CamoContainerClientHandler clientHandler = container.getClientHandler();
            clientHandler.collectTintValues(container, stack, tintList);
        }

        private Client() { }
    }

    private CamoContainerHelper() { }
}
