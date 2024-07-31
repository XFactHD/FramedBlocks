package xfacthd.framedblocks.common.data.skippreds.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.blockentity.special.ICollapsibleBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.property.NullableDirection;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_COLLAPSIBLE_BLOCK)
public final class CollapsibleBlockSkipPredicate implements SideSkipPredicate
{
    private static final VertexPair[][] EDGE_MAPPING = makeEdgeMappings();

    @Override
    @CullTest.TestTarget(BlockType.FRAMED_COLLAPSIBLE_BLOCK)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        if (face == NullableDirection.NONE || side.getAxis() == face.toDirection().getAxis())
        {
            return false;
        }

        if (adjState.getBlock() == state.getBlock() && adjState.getValue(PropertyHolder.NULLABLE_FACE) == face)
        {
            BlockEntity be = level.getBlockEntity(pos);
            BlockEntity adjBe = level.getBlockEntity(pos.relative(side));

            if (be instanceof ICollapsibleBlockEntity cbe && adjBe instanceof ICollapsibleBlockEntity adjCbe)
            {
                Direction faceDir = face.toDirection();
                VertexPair verts = EDGE_MAPPING[faceDir.ordinal()][side.ordinal()];
                VertexPair adjVerts = EDGE_MAPPING[faceDir.ordinal()][side.getOpposite().ordinal()];

                int offV1 = cbe.getVertexOffset(state, verts.v1);
                int offV2 = cbe.getVertexOffset(state, verts.v2);
                int adjOffV1 = adjCbe.getVertexOffset(adjState, adjVerts.v1);
                int adjOffV2 = adjCbe.getVertexOffset(adjState, adjVerts.v2);

                return offV1 == adjOffV2 && offV2 == adjOffV1;
            }
        }
        return false;
    }



    private static VertexPair[][] makeEdgeMappings()
    {
        VertexPair[][] table = new VertexPair[6][6];

        table[Direction.UP.ordinal()][Direction.NORTH.ordinal()] = new VertexPair(0, 3);
        table[Direction.UP.ordinal()][Direction.EAST.ordinal()]  = new VertexPair(3, 2);
        table[Direction.UP.ordinal()][Direction.SOUTH.ordinal()] = new VertexPair(2, 1);
        table[Direction.UP.ordinal()][Direction.WEST.ordinal()]  = new VertexPair(1, 0);

        table[Direction.DOWN.ordinal()][Direction.NORTH.ordinal()] = new VertexPair(1, 2);
        table[Direction.DOWN.ordinal()][Direction.EAST.ordinal()]  = new VertexPair(2, 3);
        table[Direction.DOWN.ordinal()][Direction.SOUTH.ordinal()] = new VertexPair(3, 0);
        table[Direction.DOWN.ordinal()][Direction.WEST.ordinal()]  = new VertexPair(0, 1);

        table[Direction.NORTH.ordinal()][Direction.UP.ordinal()]   = new VertexPair(0, 3);
        table[Direction.NORTH.ordinal()][Direction.WEST.ordinal()] = new VertexPair(3, 2);
        table[Direction.NORTH.ordinal()][Direction.DOWN.ordinal()] = new VertexPair(2, 1);
        table[Direction.NORTH.ordinal()][Direction.EAST.ordinal()] = new VertexPair(1, 0);

        table[Direction.EAST.ordinal()][Direction.UP.ordinal()]    = new VertexPair(0, 3);
        table[Direction.EAST.ordinal()][Direction.NORTH.ordinal()] = new VertexPair(3, 2);
        table[Direction.EAST.ordinal()][Direction.DOWN.ordinal()]  = new VertexPair(2, 1);
        table[Direction.EAST.ordinal()][Direction.SOUTH.ordinal()] = new VertexPair(1, 0);

        table[Direction.SOUTH.ordinal()][Direction.UP.ordinal()]   = new VertexPair(0, 3);
        table[Direction.SOUTH.ordinal()][Direction.EAST.ordinal()] = new VertexPair(3, 2);
        table[Direction.SOUTH.ordinal()][Direction.DOWN.ordinal()] = new VertexPair(2, 1);
        table[Direction.SOUTH.ordinal()][Direction.WEST.ordinal()] = new VertexPair(1, 0);

        table[Direction.WEST.ordinal()][Direction.UP.ordinal()]    = new VertexPair(0, 3);
        table[Direction.WEST.ordinal()][Direction.SOUTH.ordinal()] = new VertexPair(3, 2);
        table[Direction.WEST.ordinal()][Direction.DOWN.ordinal()]  = new VertexPair(2, 1);
        table[Direction.WEST.ordinal()][Direction.NORTH.ordinal()] = new VertexPair(1, 0);

        return table;
    }

    private record VertexPair(int v1, int v2) { }
}