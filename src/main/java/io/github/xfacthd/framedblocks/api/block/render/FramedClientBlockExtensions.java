package io.github.xfacthd.framedblocks.api.block.render;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.block.overlay.TintSource;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.util.TintUtils;
import io.github.xfacthd.framedblocks.api.util.sound.SoundUtils;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/// Base [IClientBlockExtensions] required on all framed blocks.
public class FramedClientBlockExtensions implements IClientBlockExtensions {
    public static final FramedClientBlockExtensions INSTANCE = new FramedClientBlockExtensions();

    protected FramedClientBlockExtensions() { }

    @Override
    public boolean addHitEffects(BlockState state, Level level, @Nullable HitResult target, ParticleEngine engine) {
        BlockHitResult hit = (BlockHitResult) Objects.requireNonNull(target);
        boolean suppressed = suppressParticles(state, level, hit.getBlockPos());
        if (!suppressed && level.getBlockEntity(hit.getBlockPos()) instanceof IFramedBlockEntity be) {
            return addHitEffectsUnsuppressed(state, level, hit, be, engine);
        }
        return suppressed;
    }

    /// Spawn particles when the block is hit by a player and the effect is not suppressed by intangibility.
    ///
    /// @param state  The state of the block
    /// @param level  The level the block is in
    /// @param hit    The exact location the block was hit at
    /// @param be     The BE of the block that was hit
    /// @param engine The particle engine to use for spawning particles
    /// @return whether vanilla particle spawning should be suppressed
    protected boolean addHitEffectsUnsuppressed(BlockState state, Level level, BlockHitResult hit, IFramedBlockEntity be, ParticleEngine engine) {
        ParticleHelper.Client.addHitEffects(state, level, hit, be.getCamo(), be.getOverlay(), engine);
        return true;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine engine) {
        boolean suppressed = suppressParticles(state, level, pos);
        if (!suppressed && level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            return addDestroyEffectsUnsuppressed(state, level, pos, be, engine);
        }
        return suppressed;
    }

    /// Spawn particles when the block is destroyed by a player and the effect is not suppressed by intangibility.
    ///
    /// @param state  The state of the block
    /// @param level  The level the block is in
    /// @param pos    The position of the block
    /// @param be     The BE of the block that was hit
    /// @param engine The particle engine to use for spawning particles
    /// @return whether vanilla particle spawning should be suppressed
    protected boolean addDestroyEffectsUnsuppressed(BlockState state, Level level, BlockPos pos, IFramedBlockEntity be, ParticleEngine engine) {
        ParticleHelper.Client.addDestroyEffects(state, level, pos, be.getCamo(), be.getOverlay(), engine);
        return true;
    }

    @Override
    public boolean playHitSound(BlockState state, Level level, BlockPos pos, Direction hitFace, SoundManager soundManager) {
        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            SoundUtils.Client.playHitSound(soundManager, pos, be.getCamo().getContent().getSoundType());
            return true;
        }
        return false;
    }

    @Override
    public boolean playBreakSound(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            SoundUtils.Client.playBreakSound(level, pos, be.getCamo().getContent().getSoundType());
            return true;
        }
        return false;
    }

    /// {@return whether particles for the given block are suppressed by intangibility}
    ///
    /// @param state The state of the block
    /// @param level The level the block is in
    /// @param pos   The position of the block
    protected static boolean suppressParticles(BlockState state, Level level, BlockPos pos) {
        if (state.getBlock() instanceof IFramedBlock block && block.getBlockType().allowMakingIntangible()) {
            return block.isIntangible(state, level, pos, null);
        }
        return false;
    }

    @Override
    public final void collectDynamicTintValues(BlockState state, BlockAndTintGetter level, BlockPos pos, IntList tintValues) {
        ModelData modelData = level.getModelData(pos);
        AbstractFramedBlockData data = modelData.get(AbstractFramedBlockData.PROPERTY);
        if (data != null) {
            collectCamoTintValues(data, level, pos, tintValues);

            BlockOverlay overlay = data.getBlockOverlay();
            TintSource tintSource;
            if (overlay != null && (tintSource = overlay.tintSource()) != null) {
                tintValues.add(TintUtils.getOverlayTintSource(tintSource).colorInWorld(tintSource.defaultBlockState(), level, pos));
            }
        }
        collectAdditionalTintValues(state, level, pos, modelData, tintValues);
    }

    void collectCamoTintValues(AbstractFramedBlockData data, BlockAndTintGetter level, BlockPos pos, IntList tintValues) {
        CamoContainerHelper.Client.collectTintValues(data.unwrap(false).getCamoContainer(), level, pos, tintValues);
    }

    /// Collect tint value used by additional non-camo geometry of the given block.
    ///
    /// @param state      The state of the block
    /// @param level      The level the block is in
    /// @param pos        The position of the block
    /// @param modelData  The model data provided by the BE of the block
    /// @param tintValues The list to append the tint values to
    protected void collectAdditionalTintValues(BlockState state, BlockAndTintGetter level, BlockPos pos, ModelData modelData, IntList tintValues) { }
}
