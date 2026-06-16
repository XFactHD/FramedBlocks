package io.github.xfacthd.framedblocks.api.camo.empty;

import io.github.xfacthd.framedblocks.api.FramedBlocksAPI;
import io.github.xfacthd.framedblocks.api.camo.CamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import org.jspecify.annotations.Nullable;

public final class EmptyCamoContent extends CamoContent<EmptyCamoContent> {
    public static final EmptyCamoContent EMPTY = new EmptyCamoContent();

    @Override
    public boolean propagatesSkylightDown() {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion) {
        return FramedBlocksAPI.INSTANCE.getDefaultModelState().getBlock().getExplosionResistance();
    }

    @Override
    public boolean isFlammable(BlockGetter level, BlockPos pos, Direction face) {
        return false;
    }

    @Override
    public int getFlammability(BlockGetter level, BlockPos pos, Direction face) {
        return 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockGetter level, BlockPos pos, Direction face) {
        return 0;
    }

    @Override
    public boolean isIgnitedByLava(BlockGetter level, BlockPos pos, Direction face) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockGetter level, BlockPos pos, float frameShade) {
        return frameShade;
    }

    @Override
    public int getLightEmission() {
        return 0;
    }

    @Override
    public boolean isEmissive() {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSoundType() {
        return FramedBlocksAPI.INSTANCE.getDefaultModelState().getSoundType();
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockAndLightGetter level, BlockPos pos, FluidState fluid) {
        return true;
    }

    @Override
    public float getFriction(LevelReader level, BlockPos pos, @Nullable Entity entity, float frameFriction) {
        return frameFriction;
    }

    @Override
    public TriState canSustainPlant(BlockGetter level, BlockPos pos, Direction side, BlockState plant) {
        return TriState.DEFAULT;
    }

    @Override
    public boolean canEntityDestroy(BlockGetter level, BlockPos pos, Entity entity) {
        return true;
    }

    @Override
    public @Nullable MapColor getMapColor(BlockGetter level, BlockPos pos) {
        return null;
    }

    @Override
    public @Nullable Integer getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return null;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canOcclude() {
        return false;
    }

    @Override
    public float getBounceRestitution(Level level, BlockPos pos, Entity entity) {
        return 0F;
    }

    @Override
    public BlockState getAsBlockState() {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockState getAppearanceState() {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isOccludedBy(BlockState adjState, BlockGetter level, BlockPos pos, BlockPos adjPos, Direction side) {
        return false;
    }

    @Override
    public boolean isOccludedBy(CamoContent<?> adjCamo, BlockGetter level, BlockPos pos, BlockPos adjPos, Direction side) {
        return false;
    }

    @Override
    public boolean occludes(BlockState adjState, BlockGetter level, BlockPos pos, BlockPos adjPos, Direction side) {
        return false;
    }

    @Override
    public ParticleOptions makeRunningLandingParticles(BlockPos pos) {
        return new BlockParticleOption(ParticleTypes.BLOCK, FramedBlocksAPI.INSTANCE.getDefaultModelState());
    }

    @Override
    public String getCamoId() {
        return "empty";
    }

    @Override
    public MutableComponent getCamoName() {
        return EmptyCamoContainer.CAMO_NAME;
    }

    @Override
    public CamoContentClientHandler<EmptyCamoContent> getClientHandler() {
        return EmptyCamoContentClientHandler.INSTANCE;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj == this;
    }

    @Override
    public String toString() {
        return "EmptyCamoContent{}";
    }
}
