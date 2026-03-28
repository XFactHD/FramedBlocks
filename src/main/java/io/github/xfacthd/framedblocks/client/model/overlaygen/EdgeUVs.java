package io.github.xfacthd.framedblocks.client.model.overlaygen;

import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;

record EdgeUVs(int uIdx, int vIdx, boolean uInv, boolean vInv) {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final EdgeUVs[] MAPPING = generateEdgeUVs();

    public static EdgeUVs get(Direction face, Direction edge) {
        return MAPPING[makeKey(face, edge)];
    }

    private static EdgeUVs[] generateEdgeUVs() {
        EdgeUVs[] infos = new EdgeUVs[46];
        for (Direction face : DIRECTIONS) {
            for (Direction edge : DIRECTIONS) {
                if (edge.getAxis() == face.getAxis()) {
                    continue;
                }

                int uIdx;
                int vIdx;
                boolean uInv;
                boolean vInv;
                if (DirUtils.isY(face)) {
                    boolean xEdge = DirUtils.isX(edge);
                    uIdx = xEdge ? 2 : 0;
                    vIdx = xEdge ? 0 : 2;

                    uInv = edge == Direction.SOUTH || edge == Direction.WEST;
                    vInv = edge == Direction.EAST || edge == Direction.SOUTH;
                } else {
                    Direction faceCW = face.getClockWise();
                    boolean cwPositive = DirUtils.isPositive(faceCW);
                    if (DirUtils.isY(edge)) {
                        uIdx = DirUtils.isX(face) ? 2 : 0;
                        vIdx = 1;

                        uInv = cwPositive ^ edge == Direction.DOWN;
                        vInv = edge == Direction.UP || (edge != Direction.DOWN && (edge == faceCW) == cwPositive);
                    } else {
                        uIdx = 1;
                        vIdx = DirUtils.isX(face) ? 2 : 0;

                        uInv = cwPositive;
                        vInv = true;
                    }
                }
                infos[makeKey(face, edge)] = new EdgeUVs(uIdx, vIdx, uInv, vInv);
            }
        }
        return infos;
    }

    private static int makeKey(Direction face, Direction edge) {
        return face.ordinal() << 3 | edge.ordinal();
    }
}
