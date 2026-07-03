package io.github.xfacthd.framedblocks.common.data.collapsible;

import net.minecraft.core.Direction;

/// @param v1      The first vertex index
/// @param v2      The second vertex index
/// @param dirToV2 The direction from the first to the second vertex
public record VertexPair(int v1, int v2, Direction dirToV2) { }
