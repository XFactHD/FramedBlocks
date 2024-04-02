package xfacthd.framedblocks.api.camo.empty;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.*;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.IPlantable;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.camo.CamoClientHandler;
import xfacthd.framedblocks.api.camo.CamoContent;

public final class EmptyCamoContent extends CamoContent<EmptyCamoContent>
{
    public static final EmptyCamoContent EMPTY = new EmptyCamoContent();

    @Override
    public boolean propagatesSkylightDown(BlockGetter level, BlockPos pos)
    {
        return false;
    }

    @Override
    public float getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return 0;
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
        return frameShade;
    }

    @Override
    public int getLightEmission()
    {
        return 0;
    }

    @Override
    public SoundType getSoundType()
    {
        return FramedBlocksAPI.INSTANCE.getDefaultModelState().getSoundType();
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockAndTintGetter level, BlockPos pos, FluidState fluid)
    {
        return true;
    }

    @Override
    public float getFriction(LevelReader level, BlockPos pos, @Nullable Entity entity, float frameFriction)
    {
        return frameFriction;
    }

    @Override
    public boolean canSustainPlant(BlockGetter level, BlockPos pos, Direction side, IPlantable plant)
    {
        return false;
    }

    @Override
    public boolean canEntityDestroy(BlockGetter level, BlockPos pos, Entity entity)
    {
        return true;
    }

    @Override
    public MapColor getMapColor(BlockGetter level, BlockPos pos)
    {
        return null;
    }

    @Override
    public int getTintColor(BlockAndTintGetter level, BlockPos pos, int tintIdx)
    {
        return -1;
    }

    @Override
    public float[] getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos)
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
    public BlockState getAppearanceState()
    {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isOccludedBy(BlockState adjState, BlockGetter level, BlockPos pos, BlockPos adjPos)
    {
        return false;
    }

    @Override
    public boolean isOccludedBy(CamoContent<?> adjCamo, BlockGetter level, BlockPos pos, BlockPos adjPos)
    {
        return false;
    }

    @Override
    public boolean occludes(BlockState adjState, BlockGetter level, BlockPos pos, BlockPos adjPos)
    {
        return false;
    }

    @Override
    public ParticleOptions makeRunningLandingParticles()
    {
        return new BlockParticleOption(ParticleTypes.BLOCK, FramedBlocksAPI.INSTANCE.getDefaultModelState());
    }

    @Override
    public String getCamoId()
    {
        return "empty";
    }

    @Override
    public MutableComponent getCamoName()
    {
        return EmptyCamoContainer.CAMO_NAME;
    }

    @Override
    public CamoClientHandler<EmptyCamoContent> getClientHandler()
    {
        return EmptyCamoClientHandler.INSTANCE;
    }

    @Override
    public int hashCode()
    {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj == this;
    }

    @Override
    public String toString()
    {
        return "EmptyCamoContent{}";
    }
}
