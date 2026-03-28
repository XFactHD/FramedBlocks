package io.github.xfacthd.framedblocks.common.apiimpl;

import io.github.xfacthd.framedblocks.api.FramedBlocksAPI;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.FramedRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public final class ApiImpl implements FramedBlocksAPI {
    @Override
    public BlockState getDefaultModelState() {
        return FBContent.BLOCK_FRAMED_CUBE.value().defaultBlockState();
    }

    @Override
    public CreativeModeTab getDefaultCreativeTab() {
        return FBContent.MAIN_TAB.value();
    }

    @Override
    public Registry<CamoContainerFactory<?>> getCamoContainerFactoryRegistry() {
        return FramedRegistries.CAMO_CONTAINER_FACTORIES;
    }
}
