package io.github.xfacthd.framedblocks.api.camo;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

/// Handles computation of client-only data for a camo content (i.e. tint values, particles and tint values).
public abstract class CamoContentClientHandler<T extends CamoContent<T>> {
    /// {@return the {@link BlockStateModel} to be rendered for the given {@link CamoContent}}
    ///
    /// @param camo The camo to query the model for
    /// @implNote this method must be backed by a cache
    public abstract BlockStateModel getOrCreateModel(T camo);

    /// {@return a {@link Particle} to be spawned when a block with the given {@link CamoContent} is punched or broken}
    ///
    /// @param level The level the owning framed block is in
    /// @param x     The X coordinate to spawn the particle at
    /// @param y     The Y coordinate to spawn the particle at
    /// @param z     The Z coordinate to spawn the particle at
    /// @param sx    The speed along the X axis to apply to the particle
    /// @param sy    The speed along the Y axis to apply to the particle
    /// @param sz    The speed along the Z axis to apply to the particle
    /// @param camo  The camo to create the particle for
    /// @param pos   The position of the owning framed block
    public abstract Particle makeHitDestroyParticle(ClientLevel level, double x, double y, double z, double sx, double sy, double sz, T camo, BlockPos pos);

    /// {@return the amount of tint layers the provided camo has}
    ///
    /// @param camo The camo whose tint layer count is being queried
    public abstract int getTintCount(T camo);

    /// Collect the tint values of the provided camo when rendered as part of a block.
    ///
    /// The amount of values added to the list must match the return value of [#getTintCount(CamoContent)].
    ///
    /// @param camo     The camo whose tint values are being queried
    /// @param level    The level in which the camo is being rendered
    /// @param pos      The position at which the camo is being rendered
    /// @param tintList The list to append the tint values to
    public abstract void collectTintValues(T camo, BlockAndTintGetter level, BlockPos pos, IntList tintList);

    /// Collect the tint values of the provided camo when rendered as part of an item.
    ///
    /// The amount of values added to the list must match the return value of [#getTintCount(CamoContent)].
    ///
    /// @param camo     The camo whose tint values are being queried
    /// @param stack    The stack holding the camo
    /// @param tintList The list to append the tint values to
    public abstract void collectTintValues(T camo, ItemStack stack, IntList tintList);

    /// {@return the tint value to use for the particle texture of the camo}
    ///
    /// @param camo The camo whose particle tint value is being queried
    public abstract int getParticleTintValue(T camo);
}
