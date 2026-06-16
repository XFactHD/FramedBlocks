package io.github.xfacthd.framedblocks.common.data.camo.fluid;

import io.github.xfacthd.framedblocks.api.camo.CamoContentClientHandler;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoContent;
import io.github.xfacthd.framedblocks.api.camo.resource.ResourceCamoContent;
import io.github.xfacthd.framedblocks.common.particle.FluidParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

public final class FluidCamoContent extends ResourceCamoContent<FluidResource, FluidCamoContent> {
    private final Direction flowDirection;

    public FluidCamoContent(FluidResource fluid, Direction flowDirection) {
        super(fluid);
        this.flowDirection = flowDirection;
    }

    public Fluid getFluid() {
        return resource.getFluid();
    }

    public Direction getFlowDirection() {
        return flowDirection;
    }

    @Override
    public boolean propagatesSkylightDown() {
        return true;
    }

    @Override
    public float getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion) {
        return getFluid().getExplosionResistance(getFluid().defaultFluidState(), level, pos, explosion);
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
        return false;
    }

    @Override
    public float getShadeBrightness(BlockGetter level, BlockPos pos, float frameShade) {
        return 1F;
    }

    @Override
    public int getLightEmission() {
        return resource.getFluidType().getLightLevel();
    }

    @Override
    public boolean isEmissive() {
        return false;
    }

    @Override
    public SoundType getSoundType() {
        return SoundType.WET_GRASS;
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockAndLightGetter level, BlockPos pos, FluidState fluidState) {
        return fluidState.getFluidType() != resource.getFluidType();
    }

    @Override
    public float getFriction(LevelReader level, BlockPos pos, @Nullable Entity entity, float frameFriction) {
        return frameFriction;
    }

    @Override
    public TriState canSustainPlant(BlockGetter level, BlockPos pos, Direction side, BlockState plant) {
        BlockState state = getFluid().defaultFluidState().createLegacyBlock();
        return CamoContainerHelper.canPlantSurviveOnCamo(state, level, pos, side, plant);
    }

    @Override
    public boolean canEntityDestroy(BlockGetter level, BlockPos pos, Entity entity) {
        return true;
    }

    @Override
    public @Nullable MapColor getMapColor(BlockGetter level, BlockPos pos) {
        BlockState state = getFluid().defaultFluidState().createLegacyBlock();
        return state.isAir() ? null : state.getMapColor(level, pos);
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
        return getFluid().defaultFluidState().createLegacyBlock();
    }

    @Override
    public BlockState getAppearanceState() {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isOccludedBy(BlockState adjState, BlockGetter level, BlockPos pos, BlockPos adjPos, Direction side) {
        return adjState.isSolidRender();
    }

    @Override
    public boolean isOccludedBy(CamoContent<?> adjCamo, BlockGetter level, BlockPos pos, BlockPos adjPos, Direction side) {
        return adjCamo.isSolid() || equals(adjCamo);
    }

    @Override
    public boolean occludes(BlockState adjState, BlockGetter level, BlockPos pos, BlockPos adjPos, Direction side) {
        return false;
    }

    @Override
    public ParticleOptions makeRunningLandingParticles(BlockPos pos) {
        return new FluidParticleOptions(getFluid());
    }

    @Override
    public String getCamoId() {
        return resource.typeHolder().getRegisteredName();
    }

    @Override
    public MutableComponent getCamoName() {
        return (MutableComponent) resource.getFluidType().getDescription();
    }

    @Override
    public CamoContentClientHandler<FluidCamoContent> getClientHandler() {
        return FluidCamoContentClientHandler.INSTANCE;
    }

    @Override
    public int hashCode() {
        return resource.hashCode() * 31 + flowDirection.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj == this || (obj instanceof FluidCamoContent camo && resource.equals(camo.resource) && flowDirection == camo.flowDirection);
    }

    @Override
    public String toString() {
        return "FluidCamoContent{fluid=Fluid{" + resource.typeHolder().getRegisteredName() + "}}";
    }
}
