package xfacthd.framedblocks.api;

import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.util.Utils;

@SuppressWarnings("unused")
public interface FramedBlocksAPI
{
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
    IForgeRegistry<CamoContainer.Factory> getCamoContainerFactoryRegistry();

    /**
     * Register the given factory to be used when the given item is used to apply a camo
     */
    void registerCamoContainerFactory(Item item, CamoContainer.Factory factory);

    /**
     * Returns the camo container factory to use for the given {@link ItemStack}
     */
    CamoContainer.Factory getCamoContainerFactory(ItemStack stack);

    /**
     * Register a custom {@link BlueprintCopyBehaviour} for the given {@link Block}s
     */
    void registerBlueprintCopyBehaviour(BlueprintCopyBehaviour behaviour, Block... blocks);
}