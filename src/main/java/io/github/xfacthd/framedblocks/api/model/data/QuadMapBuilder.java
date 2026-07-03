package io.github.xfacthd.framedblocks.api.model.data;

import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

/// Builder for quad maps (per-cullface lists of quads).
@ApiStatus.NonExtendable
public interface QuadMapBuilder {
    /// {@return a list of quads for the given cullface, creating the list if missing}
    ///
    /// @param side The cullface to get the list for
    ArrayList<BakedQuad> getOrCreate(@Nullable Direction side);
}
