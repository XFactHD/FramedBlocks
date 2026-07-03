package io.github.xfacthd.framedblocks.api.camo.applicator;

import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/// Capability interface for items which can apply arbitrary camos and optionally modifiers from items in an internal inventory.
public interface CamoApplicator {
    ItemCapability<CamoApplicator, @Nullable Void> CAPABILITY = ItemCapability.createVoid(Utils.id("camo_applicator"), CamoApplicator.class);

    /// Apply the camo and modifiers to the given framed block.
    ///
    /// @param be          The BE to apply the camo and modifiers to
    /// @param player      The player using this item to interact with the framed block
    /// @param hand        The hand used for the interaction
    /// @param camoHandler The callback for applying a camo to the framed block
    /// @param modHandler  The callback for applying a modifier to the framed block
    /// @return whether the application succeeded
    boolean apply(IFramedBlockEntity be, Player player, InteractionHand hand, CamoHandler camoHandler, ModifierHandler modHandler);

    /// Functional interface for applying a camo to a framed block.
    @FunctionalInterface
    @ApiStatus.NonExtendable
    interface CamoHandler {
        /// Apply a camo produced by the given factory to a framed block.
        ///
        /// @param factory    The camo container factory to use for creating the camo
        /// @param itemAccess The item access to consume the source item(s) of the camo from
        /// @return whether the application succeeded
        boolean accept(CamoContainerFactory<?> factory, ItemAccess itemAccess);
    }

    /// Functional interface for applying a modifier to a framed block.
    @FunctionalInterface
    @ApiStatus.NonExtendable
    interface ModifierHandler {
        /// Apply a modifier to a framed block
        ///
        /// @param itemAccess The item access to consume the source item of the modifier from
        /// @return whether the application succeeded
        boolean accept(ItemAccess itemAccess);
    }
}
