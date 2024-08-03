package xfacthd.framedblocks.api.camo;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.extensions.IBlockStateExtension;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.camo.empty.EmptyCamoContent;
import xfacthd.framedblocks.api.model.cache.QuadCacheKey;

public abstract class CamoContent<C extends CamoContent<C>> implements QuadCacheKey
{
    /**
     * {@return whether this camo propagates skylight downwards}
     * @see BlockBehaviour.BlockStateBase#propagatesSkylightDown(BlockGetter, BlockPos)
     */
    public abstract boolean propagatesSkylightDown(BlockGetter level, BlockPos pos);

    /**
     * {@return the explosion resistance of this camo}
     * @see IBlockStateExtension#getExplosionResistance(BlockGetter, BlockPos, Explosion)
     */
    public abstract float getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion);

    /**
     * {@return whether this camo is flammable on the given side}
     * @see IBlockStateExtension#isFlammable(BlockGetter, BlockPos, Direction)
     */
    public abstract boolean isFlammable(BlockGetter level, BlockPos pos, Direction face);

    /**
     * {@return how likely this camo is to catch fire on the given side}
     * @see IBlockStateExtension#getFlammability(BlockGetter, BlockPos, Direction)
     */
    public abstract int getFlammability(BlockGetter level, BlockPos pos, Direction face);

    /**
     * {@return how fast fire should spread when this camo is burning on the given side}
     * @see IBlockStateExtension#getFireSpreadSpeed(BlockGetter, BlockPos, Direction)
     */
    public abstract int getFireSpreadSpeed(BlockGetter level, BlockPos pos, Direction face);

    /**
     * {@return the shade brightness of this camo}
     * @see BlockBehaviour.BlockStateBase#getShadeBrightness(BlockGetter, BlockPos)
     */
    public abstract float getShadeBrightness(BlockGetter level, BlockPos pos, float frameShade);

    /**
     * {@return the amount of light emitted by this camo}
     * @see IBlockStateExtension#getLightEmission(BlockGetter, BlockPos)
     */
    public abstract int getLightEmission();

    /**
     * {@return whether this camo is emissive}
     * @see BlockBehaviour.BlockStateBase#emissiveRendering(BlockGetter, BlockPos)
     */
    public abstract boolean isEmissive();

    /**
     * {@return the sound type of this camo}
     * @see IBlockStateExtension#getSoundType(LevelReader, BlockPos, Entity)
     */
    public abstract SoundType getSoundType();

    /**
     * {@return whether fluids rendered next to this camo should display a fluid overlay}
     * @see IBlockStateExtension#shouldDisplayFluidOverlay(BlockAndTintGetter, BlockPos, FluidState)
     */
    public abstract boolean shouldDisplayFluidOverlay(BlockAndTintGetter level, BlockPos pos, FluidState fluid);

    /**
     * {@return the slipperiness of this camo}
     * @see IBlockStateExtension#getFriction(LevelReader, BlockPos, Entity)
     */
    public abstract float getFriction(LevelReader level, BlockPos pos, @Nullable Entity entity, float frameFriction);

    /**
     * {@return whether this camo can sustain the given plan on the given side}
     * @see IBlockStateExtension#canSustainPlant(BlockGetter, BlockPos, Direction, BlockState)
     */
    public abstract TriState canSustainPlant(BlockGetter level, BlockPos pos, Direction side, BlockState plant);

    /**
     * {@return whether this camo can be destroyed by the given entity}
     * @see IBlockStateExtension#canEntityDestroy(BlockGetter, BlockPos, Entity)
     */
    public abstract boolean canEntityDestroy(BlockGetter level, BlockPos pos, Entity entity);

    /**
     * {@return the {@link MapColor} of this camo}
     * @see BlockBehaviour.BlockStateBase#getMapColor(BlockGetter, BlockPos)
     */
    @Nullable
    public abstract MapColor getMapColor(BlockGetter level, BlockPos pos);

    /**
     * {@return the tint color corresponding to the given tint index for use with {@link BlockColor}}
     */
    public abstract int getTintColor(BlockAndTintGetter level, BlockPos pos, int tintIdx);

    /**
     * {@return the beacon color multiplier of this camo}
     * @see IBlockStateExtension#getBeaconColorMultiplier(LevelReader, BlockPos, BlockPos)
     */
    @Nullable
    public abstract Integer getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos);

    /**
     * {@return whether this camo is fully solid}
     * @see BlockBehaviour.BlockStateBase#isSolidRender(BlockGetter, BlockPos)
     */
    public abstract boolean isSolid(BlockGetter level, BlockPos pos);

    /**
     * {@return whether this camo can occlude other blocks}
     * @see BlockBehaviour.BlockStateBase#canOcclude()
     */
    public abstract boolean canOcclude();

    /**
     * {@return the {@link BlockState} representation for use in plant sustainability and other non-visual checks
     * if any is available such as for blocks or fluids with an associated block}
     */
    public abstract BlockState getAsBlockState();

    /**
     * {@return the underlying {@link BlockState} for use with the appearance API or air if this container holds a
     * non-block camo such as a fluid}
     */
    public abstract BlockState getAppearanceState();

    /**
     * {@return whether this camo can be occluded by the given adjacent non-framed block at the given adjacent position}
     */
    public abstract boolean isOccludedBy(BlockState adjState, BlockGetter level, BlockPos pos, BlockPos adjPos);

    /**
     * {@return whether this camo can be occluded by the given camo applied to an adjacent framed block at the given
     * adjacent position}
     */
    public abstract boolean isOccludedBy(CamoContent<?> adjCamo, BlockGetter level, BlockPos pos, BlockPos adjPos);

    /**
     * {@return whether this camo occludes the given adjacent non-framed block at the given adjacent position}
     */
    public abstract boolean occludes(BlockState adjState, BlockGetter level, BlockPos pos, BlockPos adjPos);

    /**
     * {@return {@link ParticleOptions} to be spawned when an entity runs over or lands on a block with this camo}
     */
    public abstract ParticleOptions makeRunningLandingParticles(BlockPos pos);

    /**
     * {@return the registry ID of this camo}
     */
    public abstract String getCamoId();

    /**
     * {@return the name of this camo to be displayed in tooltips}
     */
    public abstract MutableComponent getCamoName();

    /**
     * {@return whether this content represents a non-existent camo}
     */
    public final boolean isEmpty()
    {
        return this == EmptyCamoContent.EMPTY;
    }

    /**
     * {@return the {@link CamoClientHandler} for this camo content}
     * @apiNote This method must not be called on the server
     * @implNote This method must return a constant value
     */
    public abstract CamoClientHandler<C> getClientHandler();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract String toString();

    @Override
    public final CamoContent<?> camo()
    {
        return this;
    }

    @Override
    @Nullable
    public final Object ctCtx()
    {
        return null;
    }
}
