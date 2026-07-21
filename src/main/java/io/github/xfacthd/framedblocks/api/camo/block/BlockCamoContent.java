package io.github.xfacthd.framedblocks.api.camo.block;

import io.github.xfacthd.framedblocks.api.camo.CamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.model.util.BlockTintSourceCache;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/// Camo content implementation for block-based camos.
public final class BlockCamoContent extends CamoContent<BlockCamoContent> implements BlockTintSourceCache {
    private final BlockState state;
    @Nullable
    private List<BlockTintSource> tintSources = null;

    public BlockCamoContent(BlockState state) {
        this.state = state;
    }

    /// {@return the block state stored in this camo content}
    public BlockState getState() {
        return state;
    }

    @Override
    public boolean propagatesSkylightDown() {
        return state.propagatesSkylightDown();
    }

    @Override
    public float getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion) {
        return state.getExplosionResistance(level, pos, explosion);
    }

    @Override
    public boolean isFlammable(BlockGetter level, BlockPos pos, Direction face) {
        return state.isFlammable(level, pos, face);
    }

    @Override
    public int getFlammability(BlockGetter level, BlockPos pos, Direction face) {
        return state.getFlammability(level, pos, face);
    }

    @Override
    public int getFireSpreadSpeed(BlockGetter level, BlockPos pos, Direction face) {
        return state.getFireSpreadSpeed(level, pos, face);
    }

    @Override
    public boolean isIgnitedByLava(BlockGetter level, BlockPos pos, Direction face) {
        return state.ignitedByLava(level, pos, face);
    }

    @Override
    public float getShadeBrightness(BlockGetter level, BlockPos pos, float frameShade) {
        return Math.max(state.getShadeBrightness(level, pos), frameShade);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightEmission() {
        return state.getLightEmission();
    }

    @Override
    public boolean isEmissive() {
        // TODO: undo when level-awareness is reintroduced
        return state.emissiveRendering(/*EmptyBlockGetter.INSTANCE, BlockPos.ZERO*/);
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSoundType() {
        return state.getSoundType();
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockAndLightGetter level, BlockPos pos, FluidState fluid) {
        return state.shouldDisplayFluidOverlay(level, pos, fluid);
    }

    @Override
    public float getFriction(LevelReader level, BlockPos pos, @Nullable Entity entity, float frameFriction) {
        return state.getFriction(level, pos, entity);
    }

    @Override
    public TriState canSustainPlant(BlockGetter level, BlockPos pos, Direction side, BlockState plant) {
        return CamoContainerHelper.canPlantSurviveOnCamo(state, level, pos, side, plant);
    }

    @Override
    public boolean canEntityDestroy(BlockGetter level, BlockPos pos, Entity entity) {
        return state.canEntityDestroy(level, pos, entity);
    }

    @Override
    public MapColor getMapColor(BlockGetter level, BlockPos pos) {
        return state.getMapColor(level, pos);
    }

    @Override
    public @Nullable Integer getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return state.getBeaconColorMultiplier(level, pos, beaconPos);
    }

    @Override
    public boolean isSolid() {
        return state.isSolidRender();
    }

    @Override
    public boolean canOcclude() {
        return state.canOcclude();
    }

    @Override
    public float getBounceRestitution(Level level, BlockPos pos, Entity entity) {
        return state.getBounceRestitution(level, pos, entity);
    }

    @Override
    public BlockState getAsBlockState() {
        return state;
    }

    @Override
    public BlockState getAppearanceState() {
        return state;
    }

    @Override
    public boolean isOccludedBy(BlockState adjState, BlockGetter level, BlockPos pos, BlockPos adjPos, Direction side) {
        if (adjState.isSolidRender()) {
            return true;
        }
        if (adjState.getBlock() == state.getBlock()) {
            return !adjState.is(FramedConstants.Tags.NON_OCCLUDEABLE);
        }
        return state.skipRendering(adjState, side);
    }

    @Override
    public boolean isOccludedBy(CamoContent<?> adjCamo, BlockGetter level, BlockPos pos, BlockPos adjPos, Direction side) {
        if (adjCamo instanceof BlockCamoContent blockCamo) {
            return isOccludedBy(blockCamo.state, level, pos, adjPos, side);
        }
        return adjCamo.isSolid();
    }

    @Override
    public boolean occludes(BlockState adjState, BlockGetter level, BlockPos pos, BlockPos adjPos, Direction side) {
        if (state.isSolidRender()) {
            return true;
        }
        if (adjState.getBlock() == state.getBlock()) {
            return !adjState.is(FramedConstants.Tags.NON_OCCLUDEABLE);
        }
        return adjState.skipRendering(state, side.getOpposite());
    }

    @Override
    public String getCamoId() {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
    }

    @Override
    public MutableComponent getCamoName() {
        return state.getBlock().getName();
    }

    @Override
    public CamoContentClientHandler<BlockCamoContent> getClientHandler() {
        return BlockCamoContentClientHandler.INSTANCE;
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj == this || (obj instanceof BlockCamoContent camo && state == camo.state);
    }

    @Override
    public String toString() {
        return "BlockCamoContent{state=" + state + "}";
    }

    @Override
    @ApiStatus.Internal
    public List<BlockTintSource> resolveTintSources(Function<BlockState, List<BlockTintSource>> resolver) {
        if (tintSources == null) {
            tintSources = resolver.apply(state);
        }
        return tintSources;
    }
}
