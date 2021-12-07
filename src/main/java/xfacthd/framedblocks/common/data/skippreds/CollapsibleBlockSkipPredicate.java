package xfacthd.framedblocks.common.data.skippreds;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.data.CollapseFace;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedCollapsibleTileEntity;
import xfacthd.framedblocks.common.util.SideSkipPredicate;
import xfacthd.framedblocks.common.util.Utils;

public class CollapsibleBlockSkipPredicate implements SideSkipPredicate
{
    private static final Table<CollapseFace, Direction, VertexPair> EDGE_MAPPING = makeEdgeMappings();

    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        CollapseFace face = state.get(PropertyHolder.COLLAPSED_FACE);
        if (face == CollapseFace.NONE || side == face.toDirection().getOpposite())
        {
            return SideSkipPredicate.CTM.test(world, pos, state, adjState, side);
        }
        else if (side == face.toDirection())
        {
            return false;
        }

        TileEntity te = Utils.getTileEntitySafe(world, pos);
        TileEntity adjTe = Utils.getTileEntitySafe(world, pos.offset(side));

        if (te instanceof FramedCollapsibleTileEntity && adjTe instanceof FramedCollapsibleTileEntity)
        {
            VertexPair verts = Preconditions.checkNotNull(EDGE_MAPPING.get(face, side));
            VertexPair adjVerts = Preconditions.checkNotNull(EDGE_MAPPING.get(face, side.getOpposite()));

            byte[] offsets = ((FramedCollapsibleTileEntity) te).getVertexOffsets();
            byte[] adjOffsets = ((FramedCollapsibleTileEntity) adjTe).getVertexOffsets();

            if (offsets[verts.v1] == adjOffsets[adjVerts.v2] && offsets[verts.v2] == adjOffsets[adjVerts.v1])
            {
                return SideSkipPredicate.compareState(world, pos, side);
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

    private static final class VertexPair
    {
        private final int v1;
        private final int v2;

        private VertexPair(int v1, int v2)
        {
            this.v1 = v1;
            this.v2 = v2;
        }
    }
}