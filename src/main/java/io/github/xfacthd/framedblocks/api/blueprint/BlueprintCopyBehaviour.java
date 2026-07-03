package io.github.xfacthd.framedblocks.api.blueprint;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.ShapeLockableBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// Specifies how a [IFramedBlock] is copied and/or pasted with the Framed Blueprint.
///
/// Must be registered in [RegisterBlueprintCopyBehavioursEvent].
public interface BlueprintCopyBehaviour {
    /// Compute the blueprint data of the block being copied. Allows semi-custom storage of blueprint data,
    /// i.e. storing camo data from a second block like the Framed Door does.
    ///
    /// @param level The level the block to store is in
    /// @param pos   The position of the block to store
    /// @param state The state of the block to store
    /// @param be    The block entity of the block to store
    /// @return the data to be stored on the blueprint stack
    default BlueprintData writeToBlueprint(Level level, BlockPos pos, BlockState state, IFramedBlockEntity be) {
        return be.writeToBlueprint();
    }

    /// {@return the stack of the block item to consume for placing the block}
    default ItemStack getBlockItem(BlueprintData data) {
        return new ItemStack(data.block());
    }

    /// {@return the properties to copy from the original block}
    ///
    /// @param state The state of the block being copied
    default Set<Property<?>> getPropertiesToCopy(BlockState state) {
        Set<Property<?>> properties = Set.of();
        if (state.hasProperty(FramedProperties.ALT_SLOPE)) {
            properties = Set.of(FramedProperties.ALT_SLOPE);
        }
        if (state.getBlock() instanceof ShapeLockableBlock lockable && lockable.isLocked(state)) {
            properties = new HashSet<>(properties);
            properties.addAll(lockable.getPropertiesToCopy());
            properties.add(FramedProperties.STATE_LOCKED);
            properties = Set.copyOf(properties);
        }
        return properties;
    }

    /// {@return the camos for which items need to be consumed to place the block}
    ///
    /// @param data The data stored on the held blueprint stack
    default CamoList getCamos(BlueprintData data) {
        return data.camos();
    }

    /// {@return how much Glowstone Dust should be consumed to place the block}
    ///
    /// @param data The data stored on the held blueprint stack
    default int getGlowstoneCount(BlueprintData data) {
        return data.glowing() ? 1 : 0;
    }

    /// {@return how much Phantom Paste should be consumed to place the block}
    ///
    /// @param data The data stored on the held blueprint stack
    default int getIntangibleCount(BlueprintData data) {
        return data.intangible() ? 1 : 0;
    }

    /// {@return how many Framed Reinforcements should be consumed to place the block}
    ///
    /// @param data The data stored on the held blueprint stack
    default int getReinforcementCount(BlueprintData data) {
        return data.reinforced() ? 1 : 0;
    }

    /// {@return how much Glow Paste should be consumed to place the block}
    ///
    /// @param data The data stored on the held blueprint stack
    default int getEmissiveCount(BlueprintData data) {
        return data.emissive() ? 1 : 0;
    }

    /// {@return additional materials to be consumed when placing the block}
    ///
    /// @param data The data stored on the held blueprint stack
    default List<ItemStack> getAdditionalConsumedMaterials(BlueprintData data) {
        return List.of();
    }

    /// Perform post-processing actions after the block was placed. Used by Framed Doors to copy the camo data
    /// to the second half of the door.
    ///
    /// @param level      The level the block was placed in
    /// @param pos        The position the block was placed at
    /// @param player     The player who pasted the block
    /// @param data       The data used to paste the block
    /// @param dummyStack The dummy stack used to place the block
    default void postProcessPaste(Level level, BlockPos pos, @Nullable Player player, BlueprintData data, ItemStack dummyStack) { }

    /// Attach additional data stored in the given [BlueprintData] to the given [ItemStack]
    /// to be used during placement preview rendering of the blueprint.
    ///
    /// @param stack The dummy stack used for preview rendering
    /// @param data  The data stored on the held blueprint stack
    default void attachDataToDummyRenderStack(ItemStack stack, BlueprintData data) { }
}
