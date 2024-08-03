package xfacthd.framedblocks.api.camo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.camo.*;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.api.util.Utils;

public final class BlockCamoContent extends CamoContent<BlockCamoContent>
{
    private final BlockState state;

    public BlockCamoContent(BlockState state)
    {
        this.state = state;
    }

    public BlockState getState()
    {
        return state;
    }

    @Override
    public boolean propagatesSkylightDown(BlockGetter level, BlockPos pos)
    {
        return state.propagatesSkylightDown(level, pos);
    }

    @Override
    public float getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return state.getExplosionResistance(level, pos, explosion);
    }

    @Override
    public boolean isFlammable(BlockGetter level, BlockPos pos, Direction face)
    {
        return state.isFlammable(level, pos, face);
    }

    @Override
    public int getFlammability(BlockGetter level, BlockPos pos, Direction face)
    {
        return state.getFlammability(level, pos, face);
    }

    @Override
    public int getFireSpreadSpeed(BlockGetter level, BlockPos pos, Direction face)
    {
        return state.getFireSpreadSpeed(level, pos, face);
    }

    @Override
    public float getShadeBrightness(BlockGetter level, BlockPos pos, float frameShade)
    {
        return Math.max(state.getShadeBrightness(level, pos), frameShade);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightEmission()
    {
        return state.getLightEmission();
    }

    @Override
    public boolean isEmissive()
    {
        return state.emissiveRendering(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSoundType()
    {
        return state.getSoundType();
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockAndTintGetter level, BlockPos pos, FluidState fluid)
    {
        return state.shouldDisplayFluidOverlay(level, pos, fluid);
    }

    @Override
    public float getFriction(LevelReader level, BlockPos pos, @Nullable Entity entity, float frameFriction)
    {
        return state.getFriction(level, pos, entity);
    }

    @Override
    public TriState canSustainPlant(BlockGetter level, BlockPos pos, Direction side, BlockState plant)
    {
        return CamoContainerHelper.canPlantSurviveOnCamo(state, level, pos, side, plant);
    }

    @Override
    public boolean canEntityDestroy(BlockGetter level, BlockPos pos, Entity entity)
    {
        return state.canEntityDestroy(level, pos, entity);
    }

    @Override
    @Nullable
    public MapColor getMapColor(BlockGetter level, BlockPos pos)
    {
        return state.getMapColor(level, pos);
    }

    @Override
    public int getTintColor(BlockAndTintGetter level, BlockPos pos, int tintIdx)
    {
        if (FMLEnvironment.dist.isClient())
        {
            return ClientUtils.getBlockColor(level, pos, state, tintIdx);
        }
        throw new UnsupportedOperationException("Block color is not available on the server!");
    }

    @Override
    @Nullable
    public Integer getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        return state.getBeaconColorMultiplier(level, pos, beaconPos);
    }

    @Override
    public boolean isSolid(BlockGetter level, BlockPos pos)
    {
        return state.isSolidRender(level, pos);
    }

    @Override
    public boolean canOcclude()
    {
        return state.canOcclude();
    }

    @Override
    public BlockState getAsBlockState()
    {
        return state;
    }

    @Override
    public BlockState getAppearanceState()
    {
        return state;
    }

    @Override
    public boolean isOccludedBy(BlockState adjState, BlockGetter level, BlockPos pos, BlockPos adjPos)
    {
        if (adjState.isSolidRender(level, adjPos))
        {
            return true;
        }
        return adjState.getBlock() == state.getBlock() && !adjState.is(Utils.NON_OCCLUDEABLE);
    }

    @Override
    public boolean isOccludedBy(CamoContent<?> adjCamo, BlockGetter level, BlockPos pos, BlockPos adjPos)
    {
        if (adjCamo instanceof BlockCamoContent blockCamo)
        {
            return isOccludedBy(blockCamo.state, level, pos, adjPos);
        }
        return adjCamo.isSolid(level, adjPos);
    }

    @Override
    public boolean occludes(BlockState adjState, BlockGetter level, BlockPos pos, BlockPos adjPos)
    {
        if (state.isSolidRender(level, pos))
        {
            return true;
        }
        return adjState.getBlock() == state.getBlock() && !adjState.is(Utils.NON_OCCLUDEABLE);
    }

    @Override
    public ParticleOptions makeRunningLandingParticles(BlockPos pos)
    {
        return new BlockParticleOption(ParticleTypes.BLOCK, state);
    }

    @Override
    public String getCamoId()
    {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
    }

    @Override
    public MutableComponent getCamoName()
    {
        return state.getBlock().getName();
    }

    @Override
    public CamoClientHandler<BlockCamoContent> getClientHandler()
    {
        return BlockCamoClientHandler.INSTANCE;
    }

    @Override
    public int hashCode()
    {
        return state.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != BlockCamoContent.class) return false;
        return state == ((BlockCamoContent) obj).state;
    }

    @Override
    public String toString()
    {
        return "BlockCamoContent{" + state + "}";
    }
}
