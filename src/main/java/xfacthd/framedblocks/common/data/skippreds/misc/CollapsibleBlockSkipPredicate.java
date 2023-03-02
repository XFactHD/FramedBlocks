package xfacthd.framedblocks.common.data.skippreds.misc;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.data.property.NullableDirection;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class CollapsibleBlockSkipPredicate implements SideSkipPredicate
{
    private static final Table<NullableDirection, Direction, VertexPair> EDGE_MAPPING = makeEdgeMappings();

    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        if (face == NullableDirection.NONE || side == face.toDirection().getOpposite())
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }
        else if (side == face.toDirection() || !adjState.is(FBContent.blockFramedCollapsibleBlock.get()) || adjState.getValue(PropertyHolder.NULLABLE_FACE) != face)
        {
            return false;
        }

        BlockEntity be = Utils.getBlockEntitySafe(level, pos);
        BlockEntity adjBe = Utils.getBlockEntitySafe(level, pos.relative(side));

        if (be instanceof FramedCollapsibleBlockEntity cbe && adjBe instanceof FramedCollapsibleBlockEntity adjCbe)
        {
            VertexPair verts = Preconditions.checkNotNull(EDGE_MAPPING.get(face, side));
            VertexPair adjVerts = Preconditions.checkNotNull(EDGE_MAPPING.get(face, side.getOpposite()));

            byte[] offsets = cbe.getVertexOffsets();
            byte[] adjOffsets = adjCbe.getVertexOffsets();

            if (offsets[verts.v1] == adjOffsets[adjVerts.v2] && offsets[verts.v2] == adjOffsets[adjVerts.v1])
            {
                return SideSkipPredicate.compareState(level, pos, side, state, adjState);
            }
        }

        return false;
    }



    private static Table<NullableDirection, Direction, VertexPair> makeEdgeMappings()
    {
        Table<NullableDirection, Direction, VertexPair> table = HashBasedTable.create(6, 4);

        table.put(NullableDirection.UP, Direction.NORTH, new VertexPair(0, 3));
        table.put(NullableDirection.UP, Direction.EAST,  new VertexPair(3, 2));
        table.put(NullableDirection.UP, Direction.SOUTH, new VertexPair(2, 1));
        table.put(NullableDirection.UP, Direction.WEST,  new VertexPair(1, 0));

        table.put(NullableDirection.DOWN, Direction.NORTH, new VertexPair(1, 2));
        table.put(NullableDirection.DOWN, Direction.EAST,  new VertexPair(2, 3));
        table.put(NullableDirection.DOWN, Direction.SOUTH, new VertexPair(3, 0));
        table.put(NullableDirection.DOWN, Direction.WEST,  new VertexPair(0, 1));

        table.put(NullableDirection.NORTH, Direction.UP,   new VertexPair(0, 3));
        table.put(NullableDirection.NORTH, Direction.WEST, new VertexPair(3, 2));
        table.put(NullableDirection.NORTH, Direction.DOWN, new VertexPair(2, 1));
        table.put(NullableDirection.NORTH, Direction.EAST, new VertexPair(1, 0));

        table.put(NullableDirection.EAST, Direction.UP,    new VertexPair(0, 3));
        table.put(NullableDirection.EAST, Direction.NORTH, new VertexPair(3, 2));
        table.put(NullableDirection.EAST, Direction.DOWN,  new VertexPair(2, 1));
        table.put(NullableDirection.EAST, Direction.SOUTH, new VertexPair(1, 0));

        table.put(NullableDirection.SOUTH, Direction.UP,   new VertexPair(0, 3));
        table.put(NullableDirection.SOUTH, Direction.EAST, new VertexPair(3, 2));
        table.put(NullableDirection.SOUTH, Direction.DOWN, new VertexPair(2, 1));
        table.put(NullableDirection.SOUTH, Direction.WEST, new VertexPair(1, 0));

        table.put(NullableDirection.WEST, Direction.UP,    new VertexPair(0, 3));
        table.put(NullableDirection.WEST, Direction.SOUTH, new VertexPair(3, 2));
        table.put(NullableDirection.WEST, Direction.DOWN,  new VertexPair(2, 1));
        table.put(NullableDirection.WEST, Direction.NORTH, new VertexPair(1, 0));

        return table;
    }

    private record VertexPair(int v1, int v2) { }
}