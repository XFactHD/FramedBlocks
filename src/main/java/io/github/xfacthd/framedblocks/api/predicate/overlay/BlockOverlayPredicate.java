package io.github.xfacthd.framedblocks.api.predicate.overlay;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockOverlayPredicate {
    BlockOverlayPredicate ALWAYS = new AlwaysBlockOverlayPredicate();
    BlockOverlayPredicate NEVER = new NeverBlockOverlayPredicate();
    BlockOverlayPredicate ONLY_FIRST_PART = new OnlyFirstPartBlockOverlayPredicate();
    BlockOverlayPredicate DOUBLE_SLAB = new DoubleSlabBlockOverlayPredicate();
    BlockOverlayPredicate DOUBLE_PANEL = new DoublePanelBlockOverlayPredicate();

    /**
     * Check whether the specified side of the specified part supports solid overlay quads.
     * <p>
     * The result of this method is only used for surfaces with a null cull-face.
     *
     * @param state      The state of the block being checked
     * @param side       The side on which the solid overlay is placed
     * @param secondPart Whether the second part of the double block is being overlayed, always false for single-camo blocks
     */
    boolean supportsSolid(BlockState state, Direction side, boolean secondPart);

    /**
     * Check whether the specified side of the specified part supports edge overlay quads at the specified edge.
     * <p>
     * The result of this method is only used for surfaces with a null cull-face or where the specified edge is not
     * at the block volume's edge.
     *
     * @param state        The state of the block being checked
     * @param side         The side on which the edge overlay is placed
     * @param edge         The edge of the provided side to which the overlay is aligned
     * @param secondPart   Whether the second part of the double block is being overlayed, always false for single-camo blocks
     * @param nullCullFace Whether the edge is on a quad with a null cull-face
     * @param unaligned    Whether the edge is not aligned to the block volume's edge
     */
    boolean supportsEdge(BlockState state, Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned);
}
