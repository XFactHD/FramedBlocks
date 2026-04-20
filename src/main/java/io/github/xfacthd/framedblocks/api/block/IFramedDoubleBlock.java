package io.github.xfacthd.framedblocks.api.block;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.cache.DoubleBlockStateCache;
import io.github.xfacthd.framedblocks.api.block.cache.StateCache;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.block.render.ParticleHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.api.util.sound.SoundEventType;
import io.github.xfacthd.framedblocks.api.util.sound.SoundUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public interface IFramedDoubleBlock extends IFramedBlock {
    @Override
    FramedDoubleBlockEntity newBlockEntity(BlockPos pos, BlockState state);

    @ApiStatus.OverrideOnly
    DoubleBlockParts calculateParts(BlockState state);

    @ApiStatus.OverrideOnly
    DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state);

    @ApiStatus.OverrideOnly
    SolidityCheck calculateSolidityCheck(BlockState state, Direction side);

    @ApiStatus.OverrideOnly
    CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge);

    @Override
    default StateCache initCache(BlockState state) {
        return new DoubleBlockStateCache(state, getBlockType());
    }

    @Override
    default DoubleBlockStateCache getCache(BlockState state) {
        return (DoubleBlockStateCache) state.framedblocks$getCache();
    }

    @Override
    default @Nullable BlockState runOcclusionTestAndGetLookupState(
            SideSkipPredicate pred, BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side
    ) {
        DoubleBlockParts partStates = getCache(adjState).getParts();
        if (pred.test(level, pos, state, partStates.stateOne(), side)) {
            return partStates.stateOne();
        }
        if (pred.test(level, pos, state, partStates.stateTwo(), side)) {
            return partStates.stateTwo();
        }
        return null;
    }

    @Override
    default @Nullable BlockState getComponentAtEdge(
            BlockGetter level, BlockPos pos, BlockState state, Direction side, @Nullable Direction edge
    ) {
        DoubleBlockStateCache cache = getCache(state);
        return cache.getCamoGetter(side, edge).getComponent(cache.getParts());
    }

    @Override
    default @Nullable BlockState getComponentBySkipPredicate(
            BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction side
    ) {
        DoubleBlockParts parts = getCache(state).getParts();
        BlockState compA = parts.stateOne();
        if (testComponent(level, pos, compA, neighborState, side)) {
            return compA;
        }
        BlockState compB = parts.stateTwo();
        if (testComponent(level, pos, compB, neighborState, side)) {
            return compB;
        }
        return null;
    }

    static boolean testComponent(BlockGetter ctLevel, BlockPos pos, BlockState component, BlockState neighborState, Direction side) {
        IFramedBlock block = (IFramedBlock) component.getBlock();
        return block.getBlockType().getSideSkipPredicate().test(ctLevel, pos, component, neighborState, side);
    }

    @Override
    default boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be) {
            Holder<BlockOverlay> overlay = be.getOverlay();
            DoubleBlockTopInteractionMode mode = getCache(state).getTopInteractionMode();
            if (mode.applyFirst()) ParticleHelper.spawnRunningParticles(be.getCamo(), overlay, state, level, pos, entity);
            if (mode.applySecond()) ParticleHelper.spawnRunningParticles(be.getCamoTwo(), overlay, state, level, pos, entity);
            return true;
        }
        return false;
    }

    @Override
    default boolean addLandingEffects(BlockState state, ServerLevel level, BlockPos pos, BlockState sameState, LivingEntity entity, int count) {
        if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be) {
            Holder<BlockOverlay> overlay = be.getOverlay();
            DoubleBlockTopInteractionMode mode = getCache(state).getTopInteractionMode();
            if (mode.applyFirst()) ParticleHelper.spawnLandingParticles(be.getCamo(), overlay, state, level, pos, entity, count);
            if (mode.applySecond()) ParticleHelper.spawnLandingParticles(be.getCamoTwo(), overlay, state, level, pos, entity, count);
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    default void playStepSound(BlockState state, Level level, BlockPos pos, Entity entity, float volumeMult, float pitchMult) {
        if (!(level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)) {
            SoundUtils.playStepSound(entity, state.getSoundType(), volumeMult, pitchMult);
            return;
        }

        DoubleBlockTopInteractionMode mode = getCache(state).getTopInteractionMode();

        SoundType soundOne = null;
        if (mode.applyFirst()) {
            soundOne = be.getCamo().getContent().getSoundType();
            SoundUtils.playStepSound(entity, soundOne, volumeMult, pitchMult);
        }

        if (mode.applySecond()) {
            SoundType soundTwo = be.getCamoTwo().getContent().getSoundType();
            if (soundOne == null || !SoundUtils.isSameSound(soundOne, soundTwo, SoundEventType.STEP)) {
                SoundUtils.playStepSound(entity, soundTwo, volumeMult, pitchMult);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    default void playFallSound(BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        if (!(level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)) {
            SoundUtils.playFallSound(entity, state.getSoundType());
            return;
        }

        DoubleBlockTopInteractionMode mode = getCache(state).getTopInteractionMode();

        SoundType soundOne = null;
        if (mode.applyFirst()) {
            soundOne = be.getCamo().getContent().getSoundType();
            SoundUtils.playFallSound(entity, soundOne);
        }

        if (mode.applySecond()) {
            SoundType soundTwo = be.getCamoTwo().getContent().getSoundType();
            if (soundOne == null || !SoundUtils.isSameSound(soundOne, soundTwo, SoundEventType.FALL)) {
                SoundUtils.playFallSound(entity, soundTwo);
            }
        }
    }

    @Override
    default CamoContainer<?, ?> getCamo(BlockGetter level, BlockPos pos, BlockState state, Direction side) {
        AbstractFramedBlockData fbData = level.getModelData(pos).get(AbstractFramedBlockData.PROPERTY);
        return fbData != null ? getCache(state).getCamoGetter(side, null).getCamo(fbData) : EmptyCamoContainer.EMPTY;
    }

    @Override
    default boolean isSolidSide(BlockGetter level, BlockPos pos, BlockState state, Direction side) {
        AbstractFramedBlockData fbData = level.getModelData(pos).get(AbstractFramedBlockData.PROPERTY);
        return fbData != null && getCache(state).getSolidityCheck(side).isSolid(fbData);
    }
}
