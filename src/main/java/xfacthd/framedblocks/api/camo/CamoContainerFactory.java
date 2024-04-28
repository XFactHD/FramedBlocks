package xfacthd.framedblocks.api.camo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.util.*;

public abstract class CamoContainerFactory<T extends CamoContainer<?, T>>
{
    public static final Component MSG_BLACKLISTED = Utils.translate("msg", "camo.blacklisted");

    /**
     * Save the given {@link CamoContainer} to the given {@link CompoundTag} for saving to disk.
     *
     * @apiNote Must be called via {@link CamoContainerHelper#writeToDisk(CamoContainer)}
     */
    @ApiStatus.OverrideOnly
    protected abstract void writeToDisk(CompoundTag tag, T container);

    /**
     * Reconstruct the {@link CamoContainer} from the given {@link CompoundTag} from disk
     *
     * @apiNote Must be called via {@link CamoContainerHelper#readFromDisk(CompoundTag)}
     */
    @ApiStatus.OverrideOnly
    protected abstract T readFromDisk(CompoundTag tag);

    /**
     * Save the given the {@link CamoContainer} to the given {@link CompoundTag} for sync over the network
     *
     * @apiNote Must be called via {@link CamoContainerHelper#writeToNetwork(CamoContainer)}
     */
    @ApiStatus.OverrideOnly
    protected abstract void writeToNetwork(CompoundTag tag, T container);

    /**
     * Reconstruct the {@link CamoContainer} from the given {@link CompoundTag} from a network packet
     *
     * @apiNote Must be called via {@link CamoContainerHelper#readFromNetwork(CompoundTag)}
     */
    @ApiStatus.OverrideOnly
    protected abstract T readFromNetwork(CompoundTag tag);

    /**
     * Construct a {@link CamoContainer} from the given {@link ItemStack} and consume the resources. Must take
     * {@link ConfigView.Server#shouldConsumeCamoItem()} into account.
     * <p>
     * Called on server and client side
     *
     * @return A new {@code CamoContainer} if successful, otherwise null
     */
    @Nullable
    public abstract T applyCamo(Level level, BlockPos pos, Player player, ItemStack stack);

    /**
     * Remove the camo and refund the resources to the player. Must take
     * {@link ConfigView.Server#shouldConsumeCamoItem()} into account.
     * <p>
     * Called on server and client side
     *
     * @return true if the camo was successfully given to the player and can be removed
     */
    public abstract boolean removeCamo(Level level, BlockPos pos, Player player, ItemStack stack, T container);

    /**
     * {@return whether this camo can be converted to an {@link ItemStack} without consuming another item}
     */
    public abstract boolean canTriviallyConvertToItemStack();

    /**
     * Construct an {@link ItemStack} representation of this camo to be dropped when the enclosing block is destroyed
     * or the material list of a blueprint is being computed. If the camo cannot be trivially converted to an
     * {@link ItemStack} then this method must return {@link ItemStack#EMPTY}
     */
    public abstract ItemStack dropCamo(T container);

    /**
     * Validate the given {@link CamoContainer} after loading from disk
     * @return true to keep the camo, false to discard it
     */
    public abstract boolean validateCamo(T container);

    /**
     * Display a validation error message to the player if present and their verbosity setting allows it
     */
    protected static void displayValidationMessage(@Nullable Player player, Component message, CamoMessageVerbosity verbosity)
    {
        if (player == null || !player.level().isClientSide()) return;

        if (ConfigView.Client.INSTANCE.getCamoMessageVerbosity().isAtLeast(verbosity))
        {
            player.displayClientMessage(message, true);
        }
    }

    /**
     * {@return A {@link Codec} for reading and writing the {@link CamoContainer}}
     */
    public abstract Codec<T> codec();

    /* *
     * {@return A {@link StreamCodec} for reading and writing the {@link CamoContainer} from and to network packets}
     */
    //public abstract StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    /**
     * Called at startup to capture all items for which this factory should be used to apply and remove
     * a camo with this factory
     */
    public abstract void registerTriggerItems(TriggerRegistrar registrar);
}
