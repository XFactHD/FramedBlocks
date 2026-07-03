package io.github.xfacthd.framedblocks.api.block.overlay;

import com.mojang.serialization.Codec;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/// Holds the block from which a [BlockOverlay]s tint value is pulled from and the lazily resolved [BlockTintSource].
public final class TintSource {
    public static final Codec<TintSource> CODEC = BuiltInRegistries.BLOCK.holderByNameCodec()
            .xmap(TintSource::new, src -> src.block);

    private final Holder<Block> block;
    @Nullable
    @SuppressWarnings("OptionalAssignedToNull")
    private Optional<BlockTintSource> tintSource = null;

    public TintSource(Holder<Block> block) {
        this.block = block;
    }

    /// {@return the block the tint value is pulled from}
    public Block value() {
        return block.value();
    }

    /// {@return the default blockstate of the block from which the tint value is pulled}
    public BlockState defaultBlockState() {
        return block.value().defaultBlockState();
    }

    /// Resolves the [BlockTintSource] of the block.
    ///
    /// @param resolver The function to use for resolving the tint source
    /// @return The resolved tint source
    public Optional<BlockTintSource> resolveTintSource(Function<BlockState, List<BlockTintSource>> resolver) {
        //noinspection OptionalAssignedToNull
        if (tintSource == null) {
            List<BlockTintSource> sources = resolver.apply(defaultBlockState());
            tintSource = sources.isEmpty() ? Optional.empty() : Optional.of(sources.getFirst());
        }
        return tintSource;
    }
}
