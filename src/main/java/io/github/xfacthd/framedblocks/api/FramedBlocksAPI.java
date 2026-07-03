package io.github.xfacthd.framedblocks.api;

import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

/// The primary API entrypoint of FramedBlocks.
@ApiStatus.NonExtendable
@SuppressWarnings("unused")
public interface FramedBlocksAPI {
    FramedBlocksAPI INSTANCE = Utils.loadService(FramedBlocksAPI.class);

    /// {@return the default blockstate used as a camo source when the block's camo state is set to air}
    BlockState getDefaultModelState();

    /// {@return the creative tab that contains the FramedBlocks items}
    CreativeModeTab getDefaultCreativeTab();

    /// {@return the registry of camo container factories}
    Registry<CamoContainerFactory<?>> getCamoContainerFactoryRegistry();
}
