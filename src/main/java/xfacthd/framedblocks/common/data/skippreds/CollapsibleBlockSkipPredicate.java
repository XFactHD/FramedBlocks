package xfacthd.framedblocks.common.data.skippreds;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.data.CollapseFace;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class CollapsibleBlockSkipPredicate implements SideSkipPredicate
{
    private static final Table<CollapseFace, Direction, VertexPair> EDGE_MAPPING = makeEdgeMappings();

    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        CollapseFace face = state.getValue(PropertyHolder.COLLAPSED_FACE);
        if (face == CollapseFace.NONE || side == face.toDirection().getOpposite())
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }
        else if (side == face.toDirection() || !adjState.is(FBContent.blockFramedCollapsibleBlock.get()) || adjState.getValue(PropertyHolder.COLLAPSED_FACE) != face)
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
                return SideSkipPredicate.compareState(level, pos, side);
            }
        }

        return false;
    }



    private static Table<CollapseFace, Direction, VertexPair> makeEdgeMappings()
    {
        Table<CollapseFace, Direction, VertexPair> table = HashBasedTable.create(6, 4);

        table.put(CollapseFace.UP, Direction.NORTH, new VertexPair(0, 3));
        table.put(CollapseFace.UP, Direction.EAST,  new VertexPair(3, 2));
        table.put(CollapseFace.UP, Direction.SOUTH, new VertexPair(2, 1));
        table.put(CollapseFace.UP, Direction.WEST,  new VertexPair(1, 0));

        table.put(CollapseFace.DOWN, Direction.NORTH, new VertexPair(1, 2));
        table.put(CollapseFace.DOWN, Direction.EAST,  new VertexPair(2, 3));
        table.put(CollapseFace.DOWN, Direction.SOUTH, new VertexPair(3, 0));
        table.put(CollapseFace.DOWN, Direction.WEST,  new VertexPair(0, 1));

        table.put(CollapseFace.NORTH, Direction.UP,   new VertexPair(0, 3));
        table.put(CollapseFace.NORTH, Direction.WEST, new VertexPair(3, 2));
        table.put(CollapseFace.NORTH, Direction.DOWN, new VertexPair(2, 1));
        table.put(CollapseFace.NORTH, Direction.EAST, new VertexPair(1, 0));

        table.put(CollapseFace.EAST, Direction.UP,    new VertexPair(0, 3));
        table.put(CollapseFace.EAST, Direction.NORTH, new VertexPair(3, 2));
        table.put(CollapseFace.EAST, Direction.DOWN,  new VertexPair(2, 1));
        table.put(CollapseFace.EAST, Direction.SOUTH, new VertexPair(1, 0));

        table.put(CollapseFace.SOUTH, Direction.UP,   new VertexPair(0, 3));
        table.put(CollapseFace.SOUTH, Direction.EAST, new VertexPair(3, 2));
        table.put(CollapseFace.SOUTH, Direction.DOWN, new VertexPair(2, 1));
        table.put(CollapseFace.SOUTH, Direction.WEST, new VertexPair(1, 0));

        table.put(CollapseFace.WEST, Direction.UP,    new VertexPair(0, 3));
        table.put(CollapseFace.WEST, Direction.SOUTH, new VertexPair(3, 2));
        table.put(CollapseFace.WEST, Direction.DOWN,  new VertexPair(2, 1));
        table.put(CollapseFace.WEST, Direction.NORTH, new VertexPair(1, 0));

        return table;
    }

    private record VertexPair(int v1, int v2) { }
}