package io.github.xfacthd.framedblocks.client.model.overlaygen;

import net.minecraft.core.Direction;
import io.github.xfacthd.framedblocks.api.util.Utils;

record UVInfo(int uIdx, int vIdx, boolean uInv, boolean vInv)
{
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final UVInfo[] INFO_PER_FACE = generateFaceInfos();
    private static final UVInfo[] INFO_PER_EDGE = generateEdgeInfos();

    public static UVInfo get(Direction face)
    {
        return INFO_PER_FACE[face.ordinal()];
    }

    public static UVInfo get(Direction face, Direction edge)
    {
        return INFO_PER_EDGE[makePerEdgeKey(face, edge)];
    }

    private static UVInfo[] generateFaceInfos()
    {
        UVInfo[] infos = new UVInfo[6];

        for (Direction face : DIRECTIONS)
        {
            int uIdx;
            int vIdx;
            boolean uInv;
            boolean vInv;
            if (Utils.isY(face))
            {
                uIdx = 0;
                vIdx = 2;

                uInv = false;
                vInv = face == Direction.DOWN;
            }
            else
            {
                uIdx = Utils.isX(face) ? 2 : 0;
                vIdx = 1;

                uInv = Utils.isPositive(face.getClockWise());
                vInv = true;
            }
            infos[face.ordinal()] = new UVInfo(uIdx, vIdx, uInv, vInv);
        }

        return infos;
    }

    private static UVInfo[] generateEdgeInfos()
    {
        UVInfo[] infos = new UVInfo[46];
        for (Direction face : DIRECTIONS)
        {
            for (Direction edge : DIRECTIONS)
            {
                if (edge.getAxis() == face.getAxis()) continue;

                int uIdx;
                int vIdx;
                boolean uInv;
                boolean vInv;
                if (Utils.isY(face))
                {
                    boolean xEdge = Utils.isX(edge);
                    uIdx = xEdge ? 2 : 0;
                    vIdx = xEdge ? 0 : 2;

                    uInv = edge == Direction.SOUTH || edge == Direction.WEST;
                    vInv = edge == Direction.EAST || edge == Direction.SOUTH;
                }
                else
                {
                    Direction faceCW = face.getClockWise();
                    boolean cwPositive = Utils.isPositive(faceCW);
                    if (Utils.isY(edge))
                    {
                        uIdx = Utils.isX(face) ? 2 : 0;
                        vIdx = 1;

                        uInv = cwPositive ^ edge == Direction.DOWN;
                        vInv = edge == Direction.UP || (edge != Direction.DOWN && (edge == faceCW) == cwPositive);
                    }
                    else
                    {
                        uIdx = 1;
                        vIdx = Utils.isX(face) ? 2 : 0;

                        uInv = cwPositive;
                        vInv = true;
                    }
                }
                infos[makePerEdgeKey(face, edge)] = new UVInfo(uIdx, vIdx, uInv, vInv);
            }
        }
        return infos;
    }

    private static int makePerEdgeKey(Direction face, Direction edge)
    {
        return face.ordinal() << 3 | edge.ordinal();
    }
}
