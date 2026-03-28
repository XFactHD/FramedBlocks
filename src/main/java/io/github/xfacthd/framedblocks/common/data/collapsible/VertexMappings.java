package io.github.xfacthd.framedblocks.common.data.collapsible;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public final class VertexMappings {
    private static final VertexPair[][] EDGE_MAPPING = makeEdgeMapping();
    private static final NeighborVertex[][] NEIGHBOR_MAPPING = makeNeighborMapping();

    public static VertexPair getEdgeVertices(Direction collapsedFace, Direction edge) {
        VertexPair pair = EDGE_MAPPING[collapsedFace.ordinal()][edge.ordinal()];
        return Preconditions.checkNotNull(pair, "Invalid face-edge pair: %s %s", collapsedFace, edge);
    }

    public static NeighborVertex[] getNeighbors(Direction collapsedFace, int srcVert) {
        return NEIGHBOR_MAPPING[getNeighborMappingIndex(collapsedFace, srcVert)];
    }

    private static VertexPair[][] makeEdgeMapping() {
        VertexPair[][] table = new VertexPair[6][6];

        table[Direction.UP.ordinal()][Direction.NORTH.ordinal()] = new VertexPair(0, 3, Direction.EAST);
        table[Direction.UP.ordinal()][Direction.EAST.ordinal()]  = new VertexPair(3, 2, Direction.SOUTH);
        table[Direction.UP.ordinal()][Direction.SOUTH.ordinal()] = new VertexPair(2, 1, Direction.WEST);
        table[Direction.UP.ordinal()][Direction.WEST.ordinal()]  = new VertexPair(1, 0, Direction.NORTH);

        table[Direction.DOWN.ordinal()][Direction.NORTH.ordinal()] = new VertexPair(1, 2, Direction.EAST);
        table[Direction.DOWN.ordinal()][Direction.EAST.ordinal()]  = new VertexPair(2, 3, Direction.SOUTH);
        table[Direction.DOWN.ordinal()][Direction.SOUTH.ordinal()] = new VertexPair(3, 0, Direction.WEST);
        table[Direction.DOWN.ordinal()][Direction.WEST.ordinal()]  = new VertexPair(0, 1, Direction.NORTH);

        table[Direction.NORTH.ordinal()][Direction.UP.ordinal()]   = new VertexPair(0, 3, Direction.WEST);
        table[Direction.NORTH.ordinal()][Direction.WEST.ordinal()] = new VertexPair(3, 2, Direction.DOWN);
        table[Direction.NORTH.ordinal()][Direction.DOWN.ordinal()] = new VertexPair(2, 1, Direction.EAST);
        table[Direction.NORTH.ordinal()][Direction.EAST.ordinal()] = new VertexPair(1, 0, Direction.UP);

        table[Direction.EAST.ordinal()][Direction.UP.ordinal()]    = new VertexPair(0, 3, Direction.NORTH);
        table[Direction.EAST.ordinal()][Direction.NORTH.ordinal()] = new VertexPair(3, 2, Direction.DOWN);
        table[Direction.EAST.ordinal()][Direction.DOWN.ordinal()]  = new VertexPair(2, 1, Direction.SOUTH);
        table[Direction.EAST.ordinal()][Direction.SOUTH.ordinal()] = new VertexPair(1, 0, Direction.UP);

        table[Direction.SOUTH.ordinal()][Direction.UP.ordinal()]   = new VertexPair(0, 3, Direction.EAST);
        table[Direction.SOUTH.ordinal()][Direction.EAST.ordinal()] = new VertexPair(3, 2, Direction.DOWN);
        table[Direction.SOUTH.ordinal()][Direction.DOWN.ordinal()] = new VertexPair(2, 1, Direction.WEST);
        table[Direction.SOUTH.ordinal()][Direction.WEST.ordinal()] = new VertexPair(1, 0, Direction.UP);

        table[Direction.WEST.ordinal()][Direction.UP.ordinal()]    = new VertexPair(0, 3, Direction.SOUTH);
        table[Direction.WEST.ordinal()][Direction.SOUTH.ordinal()] = new VertexPair(3, 2, Direction.DOWN);
        table[Direction.WEST.ordinal()][Direction.DOWN.ordinal()]  = new VertexPair(2, 1, Direction.NORTH);
        table[Direction.WEST.ordinal()][Direction.NORTH.ordinal()] = new VertexPair(1, 0, Direction.UP);

        return table;
    }

    private static NeighborVertex[][] makeNeighborMapping() {
        NeighborVertex[][] mappings = new NeighborVertex[FramedCollapsibleBlockEntity.DIRECTIONS * FramedCollapsibleBlockEntity.VERTEX_COUNT][];

        putMapping(mappings, Direction.UP, 0, new NeighborVertex(new Vec3i(-1, 0,  0), 3), new NeighborVertex(new Vec3i( 0, 0, -1), 1), new NeighborVertex(new Vec3i(-1, 0, -1), 2));
        putMapping(mappings, Direction.UP, 1, new NeighborVertex(new Vec3i(-1, 0,  0), 2), new NeighborVertex(new Vec3i( 0, 0,  1), 0), new NeighborVertex(new Vec3i(-1, 0,  1), 3));
        putMapping(mappings, Direction.UP, 2, new NeighborVertex(new Vec3i( 1, 0,  0), 1), new NeighborVertex(new Vec3i( 0, 0,  1), 3), new NeighborVertex(new Vec3i( 1, 0,  1), 0));
        putMapping(mappings, Direction.UP, 3, new NeighborVertex(new Vec3i( 1, 0,  0), 0), new NeighborVertex(new Vec3i( 0, 0, -1), 2), new NeighborVertex(new Vec3i( 1, 0, -1), 1));

        putMapping(mappings, Direction.DOWN, 0, new NeighborVertex(new Vec3i(-1, 0,  0), 3), new NeighborVertex(new Vec3i( 0, 0,  1), 1), new NeighborVertex(new Vec3i(-1, 0,  1), 2));
        putMapping(mappings, Direction.DOWN, 1, new NeighborVertex(new Vec3i(-1, 0,  0), 2), new NeighborVertex(new Vec3i( 0, 0, -1), 0), new NeighborVertex(new Vec3i(-1, 0, -1), 3));
        putMapping(mappings, Direction.DOWN, 2, new NeighborVertex(new Vec3i( 1, 0,  0), 1), new NeighborVertex(new Vec3i( 0, 0, -1), 3), new NeighborVertex(new Vec3i( 1, 0, -1), 0));
        putMapping(mappings, Direction.DOWN, 3, new NeighborVertex(new Vec3i( 1, 0,  0), 0), new NeighborVertex(new Vec3i( 0, 0,  1), 2), new NeighborVertex(new Vec3i( 1, 0,  1), 1));

        putMapping(mappings, Direction.NORTH, 0, new NeighborVertex(new Vec3i( 1,  0, 0), 3), new NeighborVertex(new Vec3i( 0,  1, 0), 1), new NeighborVertex(new Vec3i( 1,  1, 0), 2));
        putMapping(mappings, Direction.NORTH, 1, new NeighborVertex(new Vec3i( 1,  0, 0), 2), new NeighborVertex(new Vec3i( 0, -1, 0), 0), new NeighborVertex(new Vec3i( 1, -1, 0), 3));
        putMapping(mappings, Direction.NORTH, 2, new NeighborVertex(new Vec3i(-1,  0, 0), 1), new NeighborVertex(new Vec3i( 0, -1, 0), 3), new NeighborVertex(new Vec3i(-1, -1, 0), 0));
        putMapping(mappings, Direction.NORTH, 3, new NeighborVertex(new Vec3i(-1,  0, 0), 0), new NeighborVertex(new Vec3i( 0,  1, 0), 2), new NeighborVertex(new Vec3i(-1,  1, 0), 1));

        putMapping(mappings, Direction.SOUTH, 0, new NeighborVertex(new Vec3i(-1,  0, 0), 3), new NeighborVertex(new Vec3i( 0,  1, 0), 1), new NeighborVertex(new Vec3i(-1,  1, 0), 2));
        putMapping(mappings, Direction.SOUTH, 1, new NeighborVertex(new Vec3i(-1,  0, 0), 2), new NeighborVertex(new Vec3i( 0, -1, 0), 0), new NeighborVertex(new Vec3i(-1, -1, 0), 3));
        putMapping(mappings, Direction.SOUTH, 2, new NeighborVertex(new Vec3i( 1,  0, 0), 1), new NeighborVertex(new Vec3i( 0, -1, 0), 3), new NeighborVertex(new Vec3i( 1, -1, 0), 0));
        putMapping(mappings, Direction.SOUTH, 3, new NeighborVertex(new Vec3i( 1,  0, 0), 0), new NeighborVertex(new Vec3i( 0,  1, 0), 2), new NeighborVertex(new Vec3i( 1,  1, 0), 1));

        putMapping(mappings, Direction.EAST, 0, new NeighborVertex(new Vec3i(0,  0,  1), 3), new NeighborVertex(new Vec3i( 0,  1, 0), 1), new NeighborVertex(new Vec3i(0,  1,  1), 2));
        putMapping(mappings, Direction.EAST, 1, new NeighborVertex(new Vec3i(0,  0,  1), 2), new NeighborVertex(new Vec3i( 0, -1, 0), 0), new NeighborVertex(new Vec3i(0, -1,  1), 3));
        putMapping(mappings, Direction.EAST, 2, new NeighborVertex(new Vec3i(0,  0, -1), 1), new NeighborVertex(new Vec3i( 0, -1, 0), 3), new NeighborVertex(new Vec3i(0, -1, -1), 0));
        putMapping(mappings, Direction.EAST, 3, new NeighborVertex(new Vec3i(0,  0, -1), 0), new NeighborVertex(new Vec3i( 0,  1, 0), 2), new NeighborVertex(new Vec3i(0,  1, -1), 1));

        putMapping(mappings, Direction.WEST, 0, new NeighborVertex(new Vec3i(0,  0, -1), 3), new NeighborVertex(new Vec3i( 0,  1, 0), 1), new NeighborVertex(new Vec3i(0,  1, -1), 2));
        putMapping(mappings, Direction.WEST, 1, new NeighborVertex(new Vec3i(0,  0, -1), 2), new NeighborVertex(new Vec3i( 0, -1, 0), 0), new NeighborVertex(new Vec3i(0, -1, -1), 3));
        putMapping(mappings, Direction.WEST, 2, new NeighborVertex(new Vec3i(0,  0,  1), 1), new NeighborVertex(new Vec3i( 0, -1, 0), 3), new NeighborVertex(new Vec3i(0, -1,  1), 0));
        putMapping(mappings, Direction.WEST, 3, new NeighborVertex(new Vec3i(0,  0,  1), 0), new NeighborVertex(new Vec3i( 0,  1, 0), 2), new NeighborVertex(new Vec3i(0,  1,  1), 1));

        return mappings;
    }

    private static void putMapping(
            NeighborVertex[][] mappings,
            Direction face,
            int srcVert,
            NeighborVertex vertOne,
            NeighborVertex vertTwo,
            NeighborVertex vertBoth
    ) {
        mappings[getNeighborMappingIndex(face, srcVert)] = new NeighborVertex[] { vertOne, vertTwo, vertBoth };
    }

    private static int getNeighborMappingIndex(Direction face, int srcVert) {
        return face.ordinal() * FramedCollapsibleBlockEntity.VERTEX_COUNT + srcVert;
    }

    private VertexMappings() { }
}
