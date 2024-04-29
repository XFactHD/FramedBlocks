package xfacthd.framedblocks.api.blueprint;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.util.CamoList;

import java.util.List;

/**
 * Provide custom behaviours when an {@link IFramedBlock} is copied and/or pasted with the Framed Blueprint
 * <p>
 * Must be registered via {@link FramedBlocksAPI#registerBlueprintCopyBehaviour(BlueprintCopyBehaviour, Block...)}
 * in {@link FMLCommonSetupEvent}
 * </p>
 */
public interface BlueprintCopyBehaviour
{
    /**
     * Allows semi-custom storage of blueprint data, i.e. storing camo data from a second block like the Framed Door does
     *
     * @param level The {@link Level} in which the {@link Block} to store is placed
     * @param pos The {@link BlockPos} at which the Block to store is placed
     * @param state The {@link BlockState} of the Block to store
     * @param be The {@link FramedBlockEntity} of the Block to store
     * @return the {@link BlueprintData} to be stored on the blueprint stack
     */
    default BlueprintData writeToBlueprint(Level level, BlockPos pos, BlockState state, FramedBlockEntity be)
    {
        return be.writeToBlueprint();
    }

    /**
     * Provide a custom {@link ItemStack} of the {@link Item} to consume instead of the {@link BlockItem}
     * used to place the block. Used by Framed Double Slabs and Framed Double Panels to consume two of the single
     * blocks instead of one double block
     */
    default ItemStack getBlockItem(BlueprintData data)
    {
        return new ItemStack(data.block());
    }

    /**
     * Provide a custom {@link CamoList} for item consumption. Used by double blocks and the Framed Door
     * to provide two camo containers
     *
     * @param data The {@link BlueprintData} stored on the held blueprint stack
     * @return The List of CamoContainers to consume when the {@link Block} is placed
     */
    default CamoList getCamos(BlueprintData data)
    {
        return data.camos();
    }

    /**
     * Provide a custom amount of Glowstone to consume when placing the block,
     * i.e. when placing a "multi-block" like doors
     *
     * @param data The {@link BlueprintData} stored on the held blueprint stack
     * @return The amount of items to consume
     */
    default int getGlowstoneCount(BlueprintData data)
    {
        return data.glowing() ? 1 : 0;
    }

    /**
     * Provide a custom amount of the intangibility marker item to consume when placing the block,
     * i.e. when placing a "multi-block" like doors
     *
     * @param data The {@link BlueprintData} stored on the held blueprint stack
     * @return The amount of items to consume
     */
    default int getIntangibleCount(BlueprintData data)
    {
        return data.intangible() ? 1 : 0;
    }

    /**
     * Provide a custom amount of the reinforcement item when placing the block,
     * i.e. when placing a "multi-block" like doors
     *
     * @param data The {@link BlueprintData} stored on the held blueprint stack
     * @return The amount of items to consume
     */
    default int getReinforcementCount(BlueprintData data)
    {
        return data.reinforced() ? 1 : 0;
    }

    /**
     * Add additional materials to be consumed when placing the block
     *
     * @param data The {@link BlueprintData} stored on the held blueprint stack
     * @return The list of additional materials to consume
     */
    default List<ItemStack> getAdditionalConsumedMaterials(BlueprintData data)
    {
        return List.of();
    }

    /**
     * Perform custom post-processing actions after the block was placed. Used by Framed Doors to copy the camo data
     * to the second half of the door
     *
     * @param level The {@link Level} in which the {@link Block} was placed
     * @param pos The {@link BlockPos} at which the Block was placed
     * @param player The {@link Player} that placed the Block
     * @param data The {@link BlueprintData} stored on the held blueprint stack
     * @param dummyStack The dummy {@link ItemStack} used to place the Block
     */
    default void postProcessPaste(Level level, BlockPos pos, Player player, BlueprintData data, ItemStack dummyStack) { }

    /**
     * Attach additional data stored in the given {@link BlueprintData} to the given {@link ItemStack}
     * to be used during placement preview rendering of the blueprint
     *
     * @param stack The dummy stack used for preview rendering
     * @param data The {@link BlueprintData} stored on the held blueprint stack
     */
    default void attachDataToDummyRenderStack(ItemStack stack, BlueprintData data) { }
}
