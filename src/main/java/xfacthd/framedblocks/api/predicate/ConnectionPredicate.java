package xfacthd.framedblocks.api.predicate;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface ConnectionPredicate
{
    /** Always false for both full edge and detailed checks */
    ConnectionPredicate FALSE = new FalseConnectionPredicate();
    /** Always true for full edge checks */
    ConnectionPredicate FULL_EDGE = new FullEdgeConnectionPredicate();
    /** True for all faces where the block's {@link FullFacePredicate} returns true */
    ConnectionPredicate FULL_FACE = new FullFaceConnectionPredicate();

    /**
     * Test whether the given state of the block this predicate belongs to can connect on
     * the given side at the given full-width edge.
     *
     * @param state The state being holding the camo that is connecting or being connected to
     * @param side The side that is connecting or being connected to
     * @param edge The edge at which the connection is happening, will be null when trying to only connect to full faces
     * @return true if the given edge of the given side occupies the full width of the edge, resides at the outer
     *         bounds of the block and can be connected to by a neighboring block (including by non-framed blocks)
     */
    boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge);

    /**
     * Test whether the given state of the block this predicate belongs to can connect on
     * the given side at the given edge in {@link ConTexMode#DETAILED}.
     *
     * @param state The state being holding the camo that is connecting or being connected to
     * @param side The side that is connecting or being connected to
     * @param edge The edge at which the connection is happening
     * @return true if the given edge of the given side occupies the full width of the edge, resides at the outer
     *         bounds of the block and can be connected to by a neighboring block (including by non-framed blocks)
     */
    boolean canConnectDetailed(BlockState state, Direction side, Direction edge);
}
