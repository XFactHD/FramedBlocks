package io.github.xfacthd.framedblocks.common.capability;

import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.capability.fluid.TankFluidResourceHandler;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

import java.util.Objects;

public final class CapabilitySetup
{
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                FBContent.BE_TYPE_FRAMED_SECRET_STORAGE.value(),
                (be, _) -> be.getItemHandler()
        );

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                FBContent.BE_TYPE_FRAMED_CHEST.value(),
                (be, _) -> be.getChestItemHandler(true)
        );

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                FBContent.BE_TYPE_POWERED_FRAMING_SAW.value(),
                (be, side) -> side != Direction.UP ? be.getExternalItemHandler() : null
        );
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                FBContent.BE_TYPE_POWERED_FRAMING_SAW.value(),
                (be, side) -> side != Direction.UP ? be.getEnergyStorage() : null
        );

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                FBContent.BE_TYPE_FRAMED_CHISELED_BOOKSHELF.value(),
                (be, _) -> VanillaContainerWrapper.of(be)
        );

        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                FBContent.BE_TYPE_FRAMED_TANK.value(),
                (be, _) -> be.getFluidHandler()
        );

        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                FBContent.BE_TYPE_FRAMED_HOPPER.value(),
                (be, _) -> VanillaContainerWrapper.of(be)
        );

        event.registerItem(
                Capabilities.Fluid.ITEM,
                (_, itemAccess) -> new ItemAccessFluidHandler(
                        Objects.requireNonNull(itemAccess),
                        FBContent.DC_TYPE_TANK_CONTENTS.value(),
                        TankFluidResourceHandler.CAPACITY
                ),
                FBContent.BLOCK_FRAMED_TANK.value()
        );
    }

    private CapabilitySetup() { }
}
