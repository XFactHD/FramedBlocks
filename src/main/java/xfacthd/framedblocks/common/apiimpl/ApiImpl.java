package xfacthd.framedblocks.common.apiimpl;

import net.minecraft.core.Registry;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.blueprint.AuxBlueprintData;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.common.FBContent;

@SuppressWarnings("unused")
public final class ApiImpl implements FramedBlocksAPI
{
    @Override
    public BlockState getDefaultModelState()
    {
        return FBContent.BLOCK_FRAMED_CUBE.value().defaultBlockState();
    }

    @Override
    public CreativeModeTab getDefaultCreativeTab()
    {
        return FBContent.MAIN_TAB.value();
    }

    @Override
    public Registry<CamoContainerFactory<?>> getCamoContainerFactoryRegistry()
    {
        return FBContent.CAMO_CONTAINER_FACTORY_REGISTRY;
    }

    @Override
    public Registry<AuxBlueprintData.Type<?>> getAuxBlueprintDataTypeRegistry()
    {
        return FBContent.AUX_BLUEPRINT_DATA_TYPE_REGISTRY;
    }
}
