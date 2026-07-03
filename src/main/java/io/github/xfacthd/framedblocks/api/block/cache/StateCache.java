package io.github.xfacthd.framedblocks.api.block.cache;

import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.VisibleForTesting;
import org.jspecify.annotations.Nullable;

/// Cache for constant metadata related to a specific [BlockState].
///
/// @apiNote Custom implementations must override [#equals(Object)] and [#hashCode()]
/// in order for cache deduplication to work properly
public class StateCache {
    protected static final Direction[] DIRECTIONS = Direction.values();
    protected static final @Nullable Direction[] DIRECTIONS_WITH_NULL = Util.make(() -> {
        // Don't replace with Arrays.copyOf(DIRECTIONS, DIRECTIONS.length + 1), null is intentionally the first value in the array
        Direction[] directions = new Direction[DIRECTIONS.length + 1];
        System.arraycopy(DIRECTIONS, 0, directions, 1, DIRECTIONS.length);
        return directions;
    });
    protected static final int DIR_COUNT = DIRECTIONS.length;
    protected static final int DIR_COUNT_N = DIR_COUNT + 1;
    public static final StateCache EMPTY = new StateCache();

    private final byte fullFace;
    private final byte mayConnect;
    private final long conFullEdge;
    private final long conDetailed;
    private final byte solidOverlay;
    private final EdgeOverlayMask edgeOverlay;

    public StateCache(BlockState state, IBlockType type) {
        byte fullFace = 0;
        byte mayConnect = 0;
        long conFullEdge = 0;
        long conDetailed = 0;
        byte solidOverlay = 0;

        FullFacePredicate facePred = type.getFullFacePredicate();
        ConnectionPredicate conPred = type.getConnectionPredicate();
        BlockOverlayPredicate overlayPredicate = type.getBlockOverlayPredicate();
        boolean supportsCt = type.supportsConnectedTextures();
        boolean supportsOverlay = type.supportsBlockOverlays();

        for (Direction side : DIRECTIONS) {
            byte sideBit = (byte) (1 << side.ordinal());
            if (facePred.test(state, side)) {
                fullFace |= sideBit;
            }
            if (supportsOverlay && overlayPredicate.supportsSolid(state, side, false)) {
                solidOverlay |= sideBit;
            }

            if (!supportsCt) {
                continue;
            }

            boolean fullEdgeNull = conPred.canConnectFullEdge(state, side, null);
            if (fullEdgeNull) {
                conFullEdge |= getSideEdgeNullableMask(side, null);
                mayConnect |= sideBit;
            }

            for (Direction edge : DIRECTIONS) {
                long feMask = getSideEdgeNullableMask(side, edge);
                if (edge.getAxis() == side.getAxis()) {
                    if (fullEdgeNull) {
                        conFullEdge |= feMask;
                    }
                    continue;
                }

                if (conPred.canConnectFullEdge(state, side, edge)) {
                    conFullEdge |= feMask;
                    mayConnect |= sideBit;
                }

                if (conPred.canConnectDetailed(state, side, edge)) {
                    conDetailed |= getSideEdgeMask(side, edge);
                    mayConnect |= sideBit;
                }
            }
        }

        this.fullFace = fullFace;
        this.mayConnect = mayConnect;
        this.conFullEdge = conFullEdge;
        this.conDetailed = conDetailed;
        this.solidOverlay = solidOverlay;
        this.edgeOverlay = supportsOverlay ? EdgeOverlayMask.compute(state, overlayPredicate, false) : EdgeOverlayMask.NEVER;
    }

    private StateCache() {
        this.fullFace = 0;
        this.mayConnect = 0;
        this.conFullEdge = 0;
        this.conDetailed = 0;
        this.solidOverlay = 0;
        this.edgeOverlay = EdgeOverlayMask.NEVER;
    }

    /// {@return whether this block has any full face (i.e. a face covering the entire surface at the block volume's outer perimeter)}
    public final boolean hasAnyFullFace() {
        return fullFace != 0;
    }

    /// {@return whether the given side is a full face (i.e. a face covering the entire surface at the block volume's outer perimeter)}
    ///
    /// @param side The side to check against
    public final boolean isFullFace(@Nullable Direction side) {
        return side != null && fullFace != 0 && (fullFace & (1 << side.ordinal())) != 0;
    }

    /// {@return the mask of all full faces}
    public final byte getFullFaceMask() {
        return fullFace;
    }

    /// {@return whether this block may allow connected textures on the given side}
    ///
    /// @param side The side to check against
    public final boolean mayConnect(Direction side) {
        return mayConnect != 0 && (mayConnect & 1 << side.ordinal()) != 0;
    }

    /// Returns whether this block allows connected textures across a full edge (i.e. an edge covering the
    /// full width of the face at the block volume's outer perimeter) at the given edge of the given side.
    ///
    /// @param side The side to check against
    /// @param edge The edge of the given side to check against
    /// @return whether a full-edge connection is possible
    public final boolean canConnectFullEdge(Direction side, @Nullable Direction edge) {
        return conFullEdge != 0 && (conFullEdge & getSideEdgeNullableMask(side, edge)) != 0;
    }

    /// Returns whether this block allows connected textures across a non-full-width edge and/or
    /// one not on the block volume's outer perimeter at the given edge of the given side.
    ///
    /// @param side The side to check against
    /// @param edge The edge of the given side to check against
    /// @return whether a "detailed" connection is possible
    public final boolean canConnectDetailed(Direction side, Direction edge) {
        return conDetailed != 0 && (conDetailed & getSideEdgeMask(side, edge)) != 0;
    }

    /// {@return whether this block has any edges with support for "detailed" connections}
    @VisibleForTesting
    @ApiStatus.Internal
    public final boolean hasAnyDetailedConnections() {
        return conDetailed != 0;
    }

    /// {@return whether this block supports the {@linkplain BlockOverlay#solidTexture solid texture} of a {@link BlockOverlay} on the given face}
    ///
    /// @param side       The side to check against
    /// @param secondPart Which part of a double block the side is being checked for
    @ApiStatus.NonExtendable
    public boolean supportsSolidOverlay(Direction side, boolean secondPart) {
        return (solidOverlay & (1 << side.ordinal())) != 0;
    }

    /// {@return whether this block supports the {@linkplain BlockOverlay#edgeTexture edge texture} of a {@link BlockOverlay} on the given face}
    ///
    /// @param side         The side to check against
    /// @param edge         The edge to check against
    /// @param secondPart   Which part of a double block the side is being checked for
    /// @param nullCullFace Whether the quad attempting to be overlayed is uncullable (i.e. inset from the block volume's perimeter)
    /// @param unaligned    Whether the given edge is inset from the block volume's perimeter
    @ApiStatus.NonExtendable
    public boolean supportsEdgeOverlay(Direction side, Direction edge, boolean secondPart, boolean nullCullFace, boolean unaligned) {
        return edgeOverlay.isSet(side, edge, nullCullFace, unaligned);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        StateCache that = (StateCache) other;
        return fullFace == that.fullFace &&
               conFullEdge == that.conFullEdge &&
               conDetailed == that.conDetailed &&
               solidOverlay == that.solidOverlay &&
               edgeOverlay.equals(that.edgeOverlay);
    }

    @Override
    public int hashCode() {
        int result = Byte.hashCode(fullFace);
        result = 31 * result + Long.hashCode(conFullEdge);
        result = 31 * result + Long.hashCode(conDetailed);
        result = 31 * result + Byte.hashCode(solidOverlay);
        result = 31 * result + edgeOverlay.hashCode();
        return result;
    }

    /// Compute the side-edge mask for the given side and edge. The edge may be null to refer to the entire face.
    ///
    /// @param side The side to compute the mask for
    /// @param edge The edge to compute the mask for or null
    /// @return the resulting bit mask
    protected static long getSideEdgeNullableMask(Direction side, @Nullable Direction edge) {
        return 1L << (side.ordinal() * DIR_COUNT_N + DirUtils.maskNullDirection(edge));
    }

    /// Compute the side-edge mask for the given side and edge
    ///
    /// @param side The side to compute the mask for
    /// @param edge The edge to compute the mask for
    /// @return the resulting bit mask
    protected static long getSideEdgeMask(Direction side, Direction edge) {
        return 1L << (side.ordinal() * DIR_COUNT + edge.ordinal());
    }
}
