package xfacthd.framedblocks.api.camo;

import net.minecraft.core.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;

final class CamoResolvingLevelReader implements LevelReader
{
    private final LevelReader wrapped;
    private final BlockPos origin;

    CamoResolvingLevelReader(LevelReader wrapped, BlockPos origin)
    {
        this.wrapped = wrapped;
        this.origin = origin;
    }

    @Override
    public BlockState getBlockState(BlockPos pos)
    {
        BlockState state = wrapped.getBlockState(pos);
        if (state.getBlock() instanceof IFramedBlock)
        {
            Direction side = Utils.dirByNormal(pos, origin);
            if (side == null || !state.isFaceSturdy(wrapped, pos, side, SupportType.FULL))
            {
                return Blocks.AIR.defaultBlockState();
            }
            if (wrapped.getBlockEntity(pos) instanceof FramedBlockEntity be)
            {
                return be.getCamo(side).getContent().getAsBlockState();
            }
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public FluidState getFluidState(BlockPos pos)
    {
        return wrapped.getFluidState(pos);
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos)
    {
        return wrapped.getBlockEntity(pos);
    }

    @Nullable
    @Override
    public ChunkAccess getChunk(int x, int z, ChunkStatus status, boolean required)
    {
        return wrapped.getChunk(x, z, status, required);
    }

    @Override
    @Deprecated
    public boolean hasChunk(int chunkX, int chunkZ)
    {
        return wrapped.hasChunk(chunkX, chunkZ);
    }

    @Override
    public int getHeight(Heightmap.Types pHeightmapType, int pX, int pZ)
    {
        return wrapped.getHeight();
    }

    @Override
    public int getSkyDarken()
    {
        return wrapped.getSkyDarken();
    }

    @Override
    public BiomeManager getBiomeManager()
    {
        return wrapped.getBiomeManager();
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z)
    {
        return wrapped.getUncachedNoiseBiome(x, y, z);
    }

    @Override
    public boolean isClientSide()
    {
        return wrapped.isClientSide();
    }

    @Override
    @Deprecated
    public int getSeaLevel()
    {
        return wrapped.getSeaLevel();
    }

    @Override
    public DimensionType dimensionType()
    {
        return wrapped.dimensionType();
    }

    @Override
    public RegistryAccess registryAccess()
    {
        return wrapped.registryAccess();
    }

    @Override
    public FeatureFlagSet enabledFeatures()
    {
        return wrapped.enabledFeatures();
    }

    @Override
    public float getShade(Direction side, boolean shade)
    {
        return wrapped.getShade(side, shade);
    }

    @Override
    public float getShade(float normalX, float normalY, float normalZ, boolean shade)
    {
        return wrapped.getShade(normalX, normalY, normalZ, shade);
    }

    @Override
    public LevelLightEngine getLightEngine()
    {
        return wrapped.getLightEngine();
    }

    @Override
    public WorldBorder getWorldBorder()
    {
        return wrapped.getWorldBorder();
    }

    @Override
    public List<VoxelShape> getEntityCollisions(@Nullable Entity entity, AABB box)
    {
        return wrapped.getEntityCollisions(entity, box);
    }
}
