package xfacthd.framedblocks.common.apiimpl;

import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.IForgeRegistry;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.camo.CamoContainerFactories;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;

@SuppressWarnings("unused")
public final class ApiImpl implements FramedBlocksAPI
{
    @Override
    public BlockState getDefaultModelState()
    {
        return FBContent.BLOCK_FRAMED_CUBE.get().defaultBlockState();
    }

    @Override
    public CreativeModeTab getDefaultCreativeTab()
    {
        return FBContent.MAIN_TAB.get();
    }

    @Override
    public IForgeRegistry<CamoContainerFactory> getCamoContainerFactoryRegistry()
    {
        return FBContent.CAMO_CONTAINER_FACTORY_REGISTRY.get();
    }

    @Override
    public CamoContainerFactory getCamoContainerFactory(ItemStack stack)
    {
        return CamoContainerFactories.getFactory(stack);
    }

    @Override
    public void registerBlueprintCopyBehaviour(BlueprintCopyBehaviour behaviour, Block... blocks)
    {
        FramedBlueprintItem.registerBehaviour(behaviour, blocks);
    }
}