package io.github.xfacthd.framedblocks.api.render.fakelevel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public final class ColorResolvingLevel implements BlockAndTintGetter {
    public static final ColorResolvingLevel INSTANCE = new ColorResolvingLevel();
    private static final Lazy<Biome> LAZY_PLAINS = Lazy.of(() -> {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            throw new IllegalStateException("ColorResolvingLevel used outside of a world");
        }
        return level.registryAccess().getOrThrow(Biomes.PLAINS).value();
    });

    private ColorResolvingLevel() { }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver color) {
        return color.getColor(LAZY_PLAINS.get(), 0, 0);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return LevelLightEngine.EMPTY;
    }

    @Override
    public CardinalLighting cardinalLighting() {
        return CardinalLighting.DEFAULT;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @ApiStatus.Internal
    public static void clearBiomeReference() {
        LAZY_PLAINS.invalidate();
    }
}
