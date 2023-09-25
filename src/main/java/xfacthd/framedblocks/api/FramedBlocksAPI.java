package xfacthd.framedblocks.api;

import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.util.Utils;

@SuppressWarnings({ "unused", "SameReturnValue" })
public interface FramedBlocksAPI
{
    FramedBlocksAPI INSTANCE = Utils.loadService(FramedBlocksAPI.class);



    /**
     * Returns the {@link BlockEntityType} used for all basic {@link xfacthd.framedblocks.api.block.AbstractFramedBlock}
     * implementations
     */
    BlockEntityType<FramedBlockEntity> defaultBlockEntity();

    /**
     * Returns the default {@link BlockState} used as a camo source when the block's camo state is set to air
     */
    BlockState defaultModelState();

    /**
     * Returns the {@link CreativeModeTab} that contains the FramedBlocks items
     */
    CreativeModeTab defaultCreativeTab();

    /**
     * Returns the current value of the {@code fireproofBlocks} setting in the common config
     */
    boolean areBlocksFireproof();

    /**
     * If true, all faces should be checked for interaction with neighboring blocks for culling purposes,
     * else only full faces should be checked against neighboring blocks
     */
    boolean detailedCullingEnabled();

    /**
     * If true, blocks with {@code BlockEntities} can be placed in Framed blocks
     */
    boolean allowBlockEntities();

    /**
     * If true, certain blocks can be made intangible
     */
    boolean enableIntangibility();

    /**
     * Get the item used to make blocks intangible
     */
    Item getIntangibilityMarkerItem();

    /**
     * Returns the registry of camo container factories
     */
    IForgeRegistry<CamoContainer.Factory> getCamoContainerFactoryRegistry();

    /**
     * Register the given factory to be used when the given item is used to apply a camo
     */
    void registerCamoContainerFactory(Item item, CamoContainer.Factory factory);

    /**
     * Returns the camo container factory for the empty container
     */
    CamoContainer.Factory emptyCamoContainerFactory();

    /**
     * Returns the camo container factory to use for the given {@link ItemStack}
     */
    CamoContainer.Factory getCamoContainerFactory(ItemStack stack);

    /**
     * Register a custom {@link BlueprintCopyBehaviour} for the given {@link Block}s
     */
    void registerBlueprintCopyBehaviour(BlueprintCopyBehaviour behaviour, Block... blocks);

    /**
     * Returns true if hiding faces on a neighboring block is allowed in the given {@linkplain BlockGetter level}
     */
    boolean canHideNeighborFaceInLevel(BlockGetter level);

    /**
     * Returns true of if faces on the given block can be culled with the given adjacent block
     * @param state The block whose face is to be culled
     * @param adjState The adjacent block
     */
    boolean canCullBlockNextTo(BlockState state, BlockState adjState);

    /**
     * {@return true if the camo item should be consumed on application and dropped on removal of the camo}
     */
    boolean shouldConsumeCamo();
}