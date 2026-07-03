package io.github.xfacthd.framedblocks.api.camo;

import io.github.xfacthd.framedblocks.api.model.data.ModelDataEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/// Base class for camo containers, holding a [CamoContent] as well as any additional metadata which affects
/// the camo's behavior or appearance but is not required to determine its model (i.e. special tint values).
/// Additional data stored in this container must be immutable and either be singletons or properly implement
/// [Object#hashCode()] and [Object#equals(Object)] to make it eligible for use in [data components][DataComponentType].
@SuppressWarnings({ "unused", "MethodMayBeStatic" })
public abstract class CamoContainer<C extends CamoContent<C>, T extends CamoContainer<C, T>> {
    protected final C content;

    protected CamoContainer(C content) {
        this.content = content;
    }

    /// {@return this container's camo content}
    public final C getContent() {
        return content;
    }

    /// {@return the {@link MapColor} to use on the map for this camo container}
    ///
    /// @param level The level the framed block is in
    /// @param pos   The position of the framed block
    public @Nullable MapColor getMapColor(BlockGetter level, BlockPos pos) {
        return content.getMapColor(level, pos);
    }

    /// {@return the color multiplier to apply to a beacon beam}
    ///
    /// @param level     The level the framed block is in
    /// @param pos       The position of the framed block
    /// @param beaconPos The position of the beacon where the beam originates from
    public @Nullable Integer getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return content.getBeaconColorMultiplier(level, pos, beaconPos);
    }

    /// {@return whether this camo container can be rotated with the Framed Screwdriver}
    public abstract boolean canRotateCamo();

    /// Rotate the camo if possible. Must return `null` if the specific content or this container in general does
    /// not support camo rotation.
    ///
    /// @return A new container with the rotated camo if the rotation was successful, `null` otherwise
    public abstract @Nullable T rotateCamo();

    /// Mirror and rotate the camo to follow the framed block's horizontal rotation,
    /// i.e. via [BlockState#rotate(Rotation)] and [BlockState#mirror(Mirror)] on block camos.
    ///
    /// The mirror should be applied before the rotation to ensure the result matches the order
    /// of operations in structure placement.
    ///
    /// @param mirror   The mirroring to apply to the camo
    /// @param rotation The rotation to apply to the camo
    @SuppressWarnings("deprecation")
    public abstract T adjustForCarrierRotation(Mirror mirror, Rotation rotation);

    /// {@return whether this camo can be converted to an {@link ItemStack} without consuming another item}
    public final boolean canTriviallyConvertToItemStack() {
        return getFactory().canTriviallyConvertToItemStack();
    }

    /// Append additional lines to the Jade tooltip.
    ///
    /// Added lines will automatically be prefixed with four spaces.
    ///
    /// @param level  The level containing the framed block whose camo is shown
    /// @param pos    The position of the framed block whose camo is shown
    /// @param player The player looking at the framed block
    public void appendJadeTooltip(Level level, BlockPos pos, Player player, Consumer<Component> appender) { }

    /// Compute additional data to be included in the [ModelData] made available to the camo model during
    /// part collection.
    ///
    /// @param level The level containing the framed block this camo is applied to
    /// @param pos   The position of the framed block this camo is applied to
    public @Nullable ModelDataEntry<?> computeQueryData(Level level, BlockPos pos) {
        return null;
    }

    /// {@return whether this container represents a non-existent camo}
    public final boolean isEmpty() {
        return content.isEmpty();
    }

    /// {@return the client handler controlling the tint handling for this camo container}
    @SuppressWarnings("unchecked")
    public CamoContainerClientHandler<C, T> getClientHandler() {
        return (CamoContainerClientHandler<C, T>) CamoContainerClientHandler.Default.INSTANCE;
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    /// {@return the [CamoContainerFactory] used to load and save this container}
    public abstract CamoContainerFactory<T> getFactory();
}
