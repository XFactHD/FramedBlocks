package io.github.xfacthd.framedblocks.api.model.geometry;

import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/// Generates model parts with overlay quads based on the parts produced from camo quad transformation.
@ApiStatus.NonExtendable
public interface OverlayPartGenerator {
    /// Generate overlay quads with the given texture based on all quads of the given cull faces.
    ///
    /// @param cullFaces   The cull faces whose quads shall be operated on
    /// @param material    The texture to be applied to the generated overlay quads
    /// @param shaderState The blockstate that's visually closest to the generated overlay or `null` if no dedicated state should be used, for use by shader mods
    default void generate(@Nullable Direction[] cullFaces, Material.Baked material, @Nullable BlockState shaderState) {
        generate(cullFaces, _ -> material, material, _ -> true, shaderState);
    }

    /// Generate overlay quads with the given texture based on all quads of the given cull faces filtered by the given normal filter.
    ///
    /// @param cullFaces    The cull faces whose quads shall be operated on
    /// @param material     The texture to be applied to the generated overlay quads
    /// @param normalFilter A predicate to filter the quads by their nearest normal direction
    /// @param shaderState  The blockstate that's visually closest to the generated overlay or `null` if no dedicated state should be used, for use by shader mods
    default void generate(@Nullable Direction[] cullFaces, Material.Baked material, NormalFilter normalFilter, @Nullable BlockState shaderState) {
        generate(cullFaces, _ -> material, material, normalFilter, shaderState);
    }

    /// Generate overlay quads with the given texture based on all quads of the given cull faces.
    ///
    /// @param cullFaces       The cull faces whose quads shall be operated on
    /// @param materialGetter  A function returning the texture to be applied to the overlay quad generated from a quad with the given nearest normal direction
    /// @param primaryMaterial The primary sprite, to be used as the part's particle sprite
    /// @param shaderState     The blockstate that's visually closest to the generated overlay or `null` if no dedicated state should be used, for use by shader mods
    default void generate(@Nullable Direction[] cullFaces, MaterialGetter materialGetter, Material.Baked primaryMaterial, @Nullable BlockState shaderState) {
        generate(cullFaces, materialGetter, primaryMaterial, _ -> true, shaderState);
    }

    /// Generate overlay quads with the given texture based on all quads of the given cull faces filtered by the given normal filter.
    ///
    /// @param cullFaces       The cull faces whose quads shall be operated on
    /// @param materialGetter  A function returning the texture to be applied to the overlay quad generated from a quad with the given nearest normal direction
    /// @param primaryMaterial The primary sprite, to be used as the part's particle sprite
    /// @param normalFilter    A predicate to filter the quads by their nearest normal direction
    /// @param shaderState     The blockstate that's visually closest to the generated overlay or `null` if no dedicated state should be used, for use by shader mods
    void generate(@Nullable Direction[] cullFaces, MaterialGetter materialGetter, Material.Baked primaryMaterial, NormalFilter normalFilter, @Nullable BlockState shaderState);

    /// Predicate over the source quad's normal direction.
    interface NormalFilter extends Predicate<Direction> {
        @Override
        boolean test(Direction normal);
    }

    /// Function providing the overlay material based on the source quad's normal direction.
    interface MaterialGetter extends Function<Direction, Material.Baked> {
        @Override
        Material.Baked apply(Direction side);
    }
}
