package xfacthd.framedblocks.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

// TODO: 1.21.2: remove in favor of vanilla's EmptyBlockAndTintGetter
public enum EmptyBlockAndTintGetter implements BlockAndTintGetter
{
    INSTANCE;

    private static final LightChunkGetter EMPTY_LIGHT_CHUNK_GETTER = new LightChunkGetter()
    {
        @Override
        @Nullable
        public LightChunk getChunkForLighting(int pChunkX, int pChunkZ)
        {
            return null;
        }

        @Override
        public BlockGetter getLevel()
        {
            return EmptyBlockAndTintGetter.INSTANCE;
        }
    };
    private static final LevelLightEngine EMPTY_LIGHT_ENGINE = new LevelLightEngine(EMPTY_LIGHT_CHUNK_GETTER, false, false);

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pPos)
    {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pPos)
    {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos pPos)
    {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public LevelLightEngine getLightEngine()
    {
        return EMPTY_LIGHT_ENGINE;
    }

    @Override
    public int getBlockTint(BlockPos pBlockPos, ColorResolver pColorResolver)
    {
        return -1;
    }

    @Override
    public int getMinBuildHeight()
    {
        return 0;
    }

    @Override
    public int getHeight()
    {
        return 0;
    }

    @Override
    public float getShade(Direction pDirection, boolean pShade)
    {
        return 1F;
    }
}
