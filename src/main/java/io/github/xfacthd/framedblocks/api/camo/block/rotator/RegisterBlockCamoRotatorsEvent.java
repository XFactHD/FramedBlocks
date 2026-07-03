package io.github.xfacthd.framedblocks.api.camo.block.rotator;

import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

/// Registration event for custom block camo rotators.
///
/// Fired on the [main game event bus][NeoForge#EVENT_BUS] on both physical sides after datapacks are (re)loaded.
public final class RegisterBlockCamoRotatorsEvent extends Event {
    private final BiConsumer<Block, BlockCamoRotator> registrar;

    @ApiStatus.Internal
    public RegisterBlockCamoRotatorsEvent(BiConsumer<Block, BlockCamoRotator> registrar) {
        this.registrar = registrar;
    }

    /// Register the given rotator to the given block.
    ///
    /// @param block   The block to register to
    /// @param rotator The rotator to register
    public void register(Block block, BlockCamoRotator rotator) {
        registrar.accept(block, rotator);
    }
}
