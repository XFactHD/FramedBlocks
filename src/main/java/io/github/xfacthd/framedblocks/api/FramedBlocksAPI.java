package io.github.xfacthd.framedblocks.api;

import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
@SuppressWarnings("unused")
public interface FramedBlocksAPI {
    FramedBlocksAPI INSTANCE = Utils.loadService(FramedBlocksAPI.class);

    /**
     * Returns the default {@link BlockState} used as a camo source when the block's camo state is set to air
     */
    BlockState getDefaultModelState();

    /**
     * Returns the {@link CreativeModeTab} that contains the FramedBlocks items
     */
    CreativeModeTab getDefaultCreativeTab();

    /**
     * Returns the registry of camo container factories
     */
    Registry<CamoContainerFactory<?>> getCamoContainerFactoryRegistry();
}
