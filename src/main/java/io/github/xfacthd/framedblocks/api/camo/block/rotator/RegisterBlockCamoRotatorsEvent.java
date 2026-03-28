package io.github.xfacthd.framedblocks.api.camo.block.rotator;

import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

public final class RegisterBlockCamoRotatorsEvent extends Event {
    private final BiConsumer<Block, BlockCamoRotator> registrar;

    @ApiStatus.Internal
    public RegisterBlockCamoRotatorsEvent(BiConsumer<Block, BlockCamoRotator> registrar) {
        this.registrar = registrar;
    }

    public void register(Block block, BlockCamoRotator rotator) {
        registrar.accept(block, rotator);
    }
}
