package io.github.xfacthd.framedblocks.api.camo;

import com.mojang.serialization.MapCodec;
import io.github.xfacthd.framedblocks.api.util.CamoMessageVerbosity;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/// Base class for camo container factories. Handles network and disk serialization of camos as well
/// as their application to and removal from framed blocks and interactions with framed blocks
/// holding a camo of the type handled by this factory.
public abstract class CamoContainerFactory<T extends CamoContainer<?, T>> {
    public static final Component MSG_BLACKLISTED = Utils.translate("msg", "camo.blacklisted");

    /// Save the given the [CamoContainer] to the given [CompoundTag] for sync over the network.
    ///
    /// @param valueOutput The output to serialize the camo to
    /// @param container   The camo container to serialize
    /// @apiNote Must be called via [CamoContainerHelper#writeToNetwork(ValueOutput, CamoContainer)]
    @ApiStatus.OverrideOnly
    protected abstract void writeToNetwork(ValueOutput valueOutput, T container);

    /// Reconstruct the [CamoContainer] from the given [CompoundTag] from a network packet
    ///
    /// @param valueInput The input to read the camo from
    /// @return the reconstructed camo container
    /// @apiNote Must be called via [CamoContainerHelper#readFromNetwork(Optional)]
    @ApiStatus.OverrideOnly
    protected abstract T readFromNetwork(ValueInput valueInput);

    /// Construct a camo container from the stack accessible via the given [ItemAccess] and consume the
    /// resources. Must take [ConfigView.Server#shouldConsumeCamoItem()] into account.
    ///
    /// Called on server and client side.
    ///
    /// @param level      The level containing the target framed block
    /// @param pos        The position of the target framed block
    /// @param player     The player interacting with the framed block
    /// @param itemAccess The item access to read the camo source item from
    /// @return A new `CamoContainer` if successful, otherwise null
    public abstract @Nullable T applyCamo(Level level, BlockPos pos, Player player, ItemAccess itemAccess);

    /// Remove the camo and refund the resources to the player. Must take
    /// [ConfigView.Server#shouldConsumeCamoItem()] into account.
    ///
    /// Called on server and client side.
    ///
    /// @param level      The level containing the target framed block
    /// @param pos        The position of the target framed block
    /// @param player     The player interacting with the framed block
    /// @param itemAccess The item access to read the camo removal item from
    /// @param container  The camo container being removed
    /// @return true if the camo was successfully given to the player and can be removed
    public abstract boolean removeCamo(Level level, BlockPos pos, Player player, ItemAccess itemAccess, T container);

    /// {@return whether this camo can be converted to an {@link ItemStack} without consuming another item}
    public abstract boolean canTriviallyConvertToItemStack();

    /// Construct an [ItemStack] of the item the given camo was made from, to be dropped when the enclosing
    /// block is destroyed or the material list of a blueprint is being computed. If the camo cannot be
    /// trivially converted to a stack then this method must return [ItemStack#EMPTY].
    ///
    /// @param container The camo container being dropped
    /// @return the stack to drop
    public abstract ItemStack dropCamo(T container);

    /// Validate the given [CamoContainer] after loading from disk or network.
    ///
    /// @param container The camo container to validate
    /// @return true to keep the camo, false to discard it
    public abstract boolean validateCamo(T container);

    /// Display a validation error message to the player if present and their verbosity setting allows it
    ///
    /// @param player    The player to display the message to
    /// @param message   The message to display
    /// @param verbosity The importance of the message
    protected static void displayValidationMessage(@Nullable Player player, Component message, CamoMessageVerbosity verbosity) {
        if (player == null || !player.level().isClientSide()) {
            return;
        }
        if (ConfigView.Client.INSTANCE.getCamoMessageVerbosity().isAtLeast(verbosity)) {
            player.sendOverlayMessage(message);
        }
    }

    /// Handle interactions with the given camo in the provided context. If the interaction changes the camo data,
    /// then a new camo container with the new data must be returned, otherwise the given camo should be returned.
    ///
    /// @param level  The level the framed block holding the camo is in
    /// @param pos    The position of the framed block holding the camo
    /// @param player The player interacting with the framed block
    /// @param camo   The camo container the player is interacting with
    /// @param stack  The stack used to interact with the framed block
    /// @param hand   The hand holding the stack used to interact with the framed block
    /// @return a new camo container if the camo data changes from this interaction, otherwise the given one
    public T handleInteraction(Level level, BlockPos pos, Player player, T camo, ItemStack stack, InteractionHand hand) {
        return camo;
    }

    /// {@return a {@link CamoCraftingHandler} if the camo containers handled by this factory may be applied in a crafting recipe, else `null`}
    public @Nullable CamoCraftingHandler<T> getCraftingHandler() {
        return null;
    }

    /// {@return A {@link MapCodec} for reading and writing the {@link CamoContainer}}
    public abstract MapCodec<T> codec();

    /// {@return A {@link StreamCodec} for reading and writing the {@link CamoContainer} from and to network packets}
    public abstract StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();

    /// Called at startup to capture all items which can be used to apply and remove a camo with this factory.
    ///
    /// @param registrar The registrar to register items and predicates to
    public abstract void registerTriggerItems(TriggerRegistrar registrar);
}
