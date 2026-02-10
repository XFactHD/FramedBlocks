package io.github.xfacthd.framedblocks.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

/**
 * Delegating {@link BlockAndTintGetter} providing access to a single {@link BlockState} and associated {@link ModelData}.
 *
 * @param realLevel The real client level, if available, to delegate lookups of thread-safe data to
 * @param realPos   The {@link BlockPos} to use for lookups in the real level
 * @param fakePos   The {@link BlockPos} at which the {@link BlockState} and {@link ModelData} should "appear"
 * @param state     The {@link BlockState} to provide
 * @param modelData The {@link ModelData} to provide
 */
public record SingleBlockFakeLevel(
        BlockAndTintGetter realLevel,
        BlockPos realPos,
        BlockPos fakePos,
        BlockState state,
        ModelData modelData
) implements BlockAndTintGetter
{
    /**
     * {@return a new {@code SingleBlockFakeLevel} with the fake position equal to the real position}
     */
    public static SingleBlockFakeLevel atPos(BlockAndTintGetter realLevel, BlockPos realPos, BlockState state, ModelData modelData)
    {
        return new SingleBlockFakeLevel(realLevel, realPos, realPos, state, modelData);
    }

    /**
     * {@return a new {@code SingleBlockFakeLevel} with the fake position at (0, 0, 0)}
     */
    public static SingleBlockFakeLevel atZero(BlockAndTintGetter realLevel, BlockPos realPos, BlockState state, ModelData modelData)
    {
        return new SingleBlockFakeLevel(realLevel, realPos, BlockPos.ZERO, state, modelData);
    }

    /**
     * {@return a new {@code SingleBlockFakeLevel} without a real level and both real and fake position at (0, 0, 0)}
     */
    public static SingleBlockFakeLevel withoutRealLevel(BlockState state, ModelData modelData)
    {
        return new SingleBlockFakeLevel(EmptyBlockAndTintGetter.INSTANCE, BlockPos.ZERO, BlockPos.ZERO, state, modelData);
    }

    @Override
    public BlockState getBlockState(BlockPos pos)
    {
        if (pos.equals(fakePos))
        {
            return state;
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos)
    {
        return null;
    }

    @Override
    public FluidState getFluidState(BlockPos pos)
    {
        return getBlockState(pos).getFluidState();
    }

    @Override
    public LevelLightEngine getLightEngine()
    {
        return realLevel.getLightEngine();
    }

    @Override
    public int getBrightness(LightLayer layer, BlockPos pos)
    {
        return LightEngine.MAX_LEVEL;
    }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver resolver)
    {
        return realLevel.getBlockTint(realPos, resolver);
    }

    @Override
    public ModelData getModelData(BlockPos pos)
    {
        if (pos.equals(fakePos))
        {
            return modelData;
        }
        return ModelData.EMPTY;
    }

    @Override
    public float getShade(Direction side, boolean shade)
    {
        return realLevel.getShade(side, shade);
    }

    @Override
    public float getShade(float normalX, float normalY, float normalZ, boolean shade)
    {
        return realLevel.getShade(normalX, normalY, normalZ, shade);
    }

    @Override
    public int getHeight()
    {
        return realLevel.getHeight();
    }

    @Override
    public int getMinY()
    {
        return realLevel.getMinY();
    }
}
