package xfacthd.framedblocks.common.data.camo.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.camo.*;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.common.particle.FluidParticleOptions;

public final class FluidCamoContent extends CamoContent<FluidCamoContent>
{
    private final Fluid fluid;

    public FluidCamoContent(Fluid fluid)
    {
        this.fluid = fluid;
    }

    public Fluid getFluid()
    {
        return fluid;
    }

    @Override
    public boolean propagatesSkylightDown(BlockGetter level, BlockPos pos)
    {
        return true;
    }

    @Override
    public float getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return fluid.getExplosionResistance(fluid.defaultFluidState(), level, pos, explosion);
    }

    @Override
    public boolean isFlammable(BlockGetter level, BlockPos pos, Direction face)
    {
        return false;
    }

    @Override
    public int getFlammability(BlockGetter level, BlockPos pos, Direction face)
    {
        return 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockGetter level, BlockPos pos, Direction face)
    {
        return 0;
    }

    @Override
    public float getShadeBrightness(BlockGetter level, BlockPos pos, float frameShade)
    {
        return 1F;
    }

    @Override
    public int getLightEmission()
    {
        return fluid.getFluidType().getLightLevel();
    }

    @Override
    public boolean isEmissive()
    {
        return false;
    }

    @Override
    public SoundType getSoundType()
    {
        return SoundType.WET_GRASS;
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockAndTintGetter level, BlockPos pos, FluidState fluidState)
    {
        return fluidState.getType() != fluid;
    }

    @Override
    public float getFriction(LevelReader level, BlockPos pos, @Nullable Entity entity, float frameFriction)
    {
        return frameFriction;
    }

    @Override
    public TriState canSustainPlant(BlockGetter level, BlockPos pos, Direction side, BlockState plant)
    {
        BlockState state = fluid.defaultFluidState().createLegacyBlock();
        return CamoContainerHelper.canPlantSurviveOnCamo(state, level, pos, side, plant);
    }

    @Override
    public boolean canEntityDestroy(BlockGetter level, BlockPos pos, Entity entity)
    {
        return true;
    }

    @Override
    @Nullable
    public MapColor getMapColor(BlockGetter level, BlockPos pos)
    {
        BlockState state = fluid.defaultFluidState().createLegacyBlock();
        return state.isAir() ? null : state.getMapColor(level, pos);
    }

    @Override
    public int getTintColor(BlockAndTintGetter level, BlockPos pos, int tintIdx)
    {
        if (FMLEnvironment.dist.isClient())
        {
            return ClientUtils.getFluidColor(level, pos, fluid.defaultFluidState());
        }
        throw new UnsupportedOperationException("Block color is not available on the server!");
    }

    @Override
    @Nullable
    public Integer getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        return null;
    }

    @Override
    public boolean isSolid(BlockGetter level, BlockPos pos)
    {
        return false;
    }

    @Override
    public boolean canOcclude()
    {
        return false;
    }

    @Override
    public BlockState getAsBlockState()
    {
        return fluid.defaultFluidState().createLegacyBlock();
    }

    @Override
    public BlockState getAppearanceState()
    {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isOccludedBy(BlockState adjState, BlockGetter level, BlockPos pos, BlockPos adjPos)
    {
        return adjState.isSolidRender(level, pos);
    }

    @Override
    public boolean isOccludedBy(CamoContent<?> adjCamo, BlockGetter level, BlockPos pos, BlockPos adjPos)
    {
        return adjCamo.isSolid(level, pos) || equals(adjCamo);
    }

    @Override
    public boolean occludes(BlockState adjState, BlockGetter level, BlockPos pos, BlockPos adjPos)
    {
        return false;
    }

    @Override
    public ParticleOptions makeRunningLandingParticles(BlockPos pos)
    {
        return new FluidParticleOptions(fluid);
    }

    @Override
    public String getCamoId()
    {
        return BuiltInRegistries.FLUID.getKey(fluid).toString();
    }

    @Override
    public MutableComponent getCamoName()
    {
        return (MutableComponent) fluid.getFluidType().getDescription();
    }

    @Override
    public CamoClientHandler<FluidCamoContent> getClientHandler()
    {
        return FluidCamoClientHandler.INSTANCE;
    }

    @Override
    public int hashCode()
    {
        return fluid.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != FluidCamoContent.class) return false;
        return fluid == ((FluidCamoContent) obj).fluid;
    }

    @Override
    public String toString()
    {
        return "FluidCamoContent{Fluid{" + BuiltInRegistries.FLUID.getKey(fluid) + "}}";
    }
}
