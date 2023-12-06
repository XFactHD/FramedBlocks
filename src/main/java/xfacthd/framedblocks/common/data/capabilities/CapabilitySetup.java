package xfacthd.framedblocks.common.data.capabilities;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import xfacthd.framedblocks.common.FBContent;

public final class CapabilitySetup
{
    public static void onRegisterCapabilities(final RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                FBContent.BE_TYPE_FRAMED_SECRET_STORAGE.value(),
                (be, side) -> be.getItemHandler()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                FBContent.BE_TYPE_FRAMED_CHEST.value(),
                (be, side) -> be.getItemHandler()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                FBContent.BE_TYPE_POWERED_FRAMING_SAW.value(),
                (be, side) -> side != Direction.UP ? be.getExternalItemHandler() : null
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                FBContent.BE_TYPE_POWERED_FRAMING_SAW.value(),
                (be, side) -> side != Direction.UP ? be.getEnergyStorage() : null
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                FBContent.BE_TYPE_FRAMED_CHISELED_BOOKSHELF.value(),
                (be, side) -> be.getItemHandler()
        );
    }



    private CapabilitySetup() { }
}
