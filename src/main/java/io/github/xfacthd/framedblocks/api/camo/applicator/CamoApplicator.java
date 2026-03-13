package io.github.xfacthd.framedblocks.api.camo.applicator;

import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jetbrains.annotations.ApiStatus;

public interface CamoApplicator
{
    ItemCapability<CamoApplicator, Void> CAPABILITY = ItemCapability.createVoid(Utils.id("camo_applicator"), CamoApplicator.class);

    boolean apply(IFramedBlockEntity be, Player player, InteractionHand hand, CamoHandler camoHandler, ModifierHandler modHandler);

    @FunctionalInterface
    @ApiStatus.NonExtendable
    interface CamoHandler
    {
        boolean accept(CamoContainerFactory<?> factory, ItemAccess itemAccess);
    }

    @FunctionalInterface
    @ApiStatus.NonExtendable
    interface ModifierHandler
    {
        boolean accept(ItemAccess itemAccess);
    }
}
