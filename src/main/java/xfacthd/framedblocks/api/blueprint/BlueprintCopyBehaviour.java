package xfacthd.framedblocks.api.blueprint;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.camo.CamoContainer;

import java.util.Optional;
import java.util.Set;

/**
 * Provide custom behaviours when an {@link IFramedBlock} is copied and/or pasted with the Framed Blueprint
 * <p>
 * Must be registered via {@link FramedBlocksAPI#registerBlueprintCopyBehaviour(BlueprintCopyBehaviour, Block...)}
 * in {@link FMLCommonSetupEvent}
 * </p>
 */
public interface BlueprintCopyBehaviour
{
    String MAIN_CAMO_KEY = "camo_data";
    String CAMO_CONTAINER_KEY = "camo";
    String GLOWSTONE_KEY = "glowing";
    String INTANGIBLE_KEY = "intangible";
    String REINFORCEMENT_KEY = "reinforced";

    /**
     * Allows semi-custom storage of blueprint data, i.e. storing camo data from a second block like the Framed Door does
     *
     * @param level The {@link Level} in which the {@link Block} to store is placed
     * @param pos The {@link BlockPos} at which the Block to store is placed
     * @param state The {@link BlockState} of the Block to store
     * @param be The {@link FramedBlockEntity} of the Block to store
     * @param blueprintData The {@link CompoundTag} the camo data is stored in
     * @return true if the default handling should not be executed
     * @implNote The main blueprint data ({@link FramedBlockEntity#writeToBlueprint(net.minecraft.core.HolderLookup.Provider)}) must be stored in the tag with the key "camo_data"
     */
    default boolean writeToBlueprint(
            Level level, BlockPos pos, BlockState state, FramedBlockEntity be, CompoundTag blueprintData
    )
    {
        return false;
    }

    /**
     * Provide a custom {@link ItemStack} of the {@link Item} to consume instead of the {@link BlockItem}
     * used to place the block. Used by Framed Double Slabs and Framed Double Panels to consume two of the single
     * blocks instead of one double block
     */
    default Optional<ItemStack> getBlockItem()
    {
        return Optional.empty();
    }

    /**
     * Provide a custom {@link Set} of {@link CamoContainer}s for item consumption. Used by double blocks and the Framed Door
     * to provide two camo containers
     *
     * @param blueprintData The {@link CompoundTag} containing the full blueprint data (block to place, camo data as
     *                      written from {@link FramedBlockEntity#writeToBlueprint(net.minecraft.core.HolderLookup.Provider)}, any custom data added in
     *                      {@link BlueprintCopyBehaviour#writeToBlueprint(Level, BlockPos, BlockState, FramedBlockEntity, CompoundTag)})
     * @return The Set of CamoContainers to consume when the {@link Block} is placed. The set must contain at least one
     *         entry when a non-empty optional is returned. The returned set should retain insertion order.
     */
    default Optional<Set<CamoContainer<?, ?>>> getCamos(CompoundTag blueprintData)
    {
        return Optional.empty();
    }

    /**
     * Provide a custom amount of Glowstone to consume when placing the block. Used by Framed Doors to consume the
     * glowstone used on the second half of the door
     *
     * @param blueprintData The {@link CompoundTag} containing the full blueprint data (block to place, camo data as
     *                      written from {@link FramedBlockEntity#writeToBlueprint(net.minecraft.core.HolderLookup.Provider)}, any custom data added in
     *                      {@link BlueprintCopyBehaviour#writeToBlueprint(Level, BlockPos, BlockState, FramedBlockEntity, CompoundTag)})
     * @return The amount of items to consume
     */
    default int getGlowstoneCount(CompoundTag blueprintData)
    {
        return blueprintData.getCompound(MAIN_CAMO_KEY).getBoolean(GLOWSTONE_KEY) ? 1 : 0;
    }

    /**
     * Provide a custom amount of the intangibility marker item to consume when placing the block
     *
     * @param blueprintData The {@link CompoundTag} containing the full blueprint data (block to place, camo data as
     *                      written from {@link FramedBlockEntity#writeToBlueprint(net.minecraft.core.HolderLookup.Provider)}, any custom data added in
     *                      {@link BlueprintCopyBehaviour#writeToBlueprint(Level, BlockPos, BlockState, FramedBlockEntity, CompoundTag)})
     * @return The amount of items to consume
     */
    default int getIntangibleCount(CompoundTag blueprintData)
    {
        return blueprintData.getCompound(MAIN_CAMO_KEY).getBoolean(INTANGIBLE_KEY) ? 1 : 0;
    }

    /**
     * Provide a custom amount of the reinforcement item when placing the block
     *
     * @param blueprintData The {@link CompoundTag} containing the full blueprint data (block to place, camo data as
     *                      written from {@link FramedBlockEntity#writeToBlueprint(net.minecraft.core.HolderLookup.Provider)}, any custom data added in
     *                      {@link BlueprintCopyBehaviour#writeToBlueprint(Level, BlockPos, BlockState, FramedBlockEntity, CompoundTag)})
     * @return The amount of items to consume
     */
    default int getReinforcementCount(CompoundTag blueprintData)
    {
        return blueprintData.getCompound(MAIN_CAMO_KEY).getBoolean(REINFORCEMENT_KEY) ? 1 : 0;
    }

    /**
     * Perform custom post-processing actions after the block was placed. Used by Framed Doors to copy the camo data
     * to the second half of the door
     *
     * @param level The {@link Level} in which the {@link Block} was placed
     * @param pos The {@link BlockPos} at which the Block was placed
     * @param player The {@link Player} that placed the Block
     * @param blueprintData The {@link CompoundTag} containing the full blueprint data
     * @param dummyStack The dummy {@link ItemStack} used to place the Block
     */
    default void postProcessPaste(
            Level level, BlockPos pos, Player player, CompoundTag blueprintData, ItemStack dummyStack
    )
    { }
}
