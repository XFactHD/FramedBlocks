package xfacthd.framedblocks.common.compat.jade;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("ConstantConditions")
public record SingleBlockFakeLevel(BlockPos realPos, BlockState state, BlockEntity blockEntity) implements BlockAndTintGetter
{
    @Override
    public float getShade(Direction side, boolean shade)
    {
        return blockEntity.getLevel().getShade(side, shade);
    }

    @Override
    public float getShade(float normalX, float normalY, float normalZ, boolean shade)
    {
        return blockEntity.getLevel().getShade(normalX, normalY, normalZ, shade);
    }

    @Override
    public LevelLightEngine getLightEngine()
    {
        return blockEntity.getLevel().getLightEngine();
    }

    @Override
    public int getBrightness(LightLayer layer, BlockPos pos)
    {
        return 15;
    }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver resolver)
    {
        return blockEntity.getLevel().getBlockTint(realPos, resolver);
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos)
    {
        if (pos == BlockPos.ZERO)
        {
            return blockEntity;
        }
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos)
    {
        if (pos == BlockPos.ZERO)
        {
            return state;
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos pos)
    {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public int getHeight()
    {
        return blockEntity.getLevel().getHeight();
    }

    @Override
    public int getMinBuildHeight()
    {
        return blockEntity.getLevel().getMinBuildHeight();
    }
}
