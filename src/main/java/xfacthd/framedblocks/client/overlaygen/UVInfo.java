package xfacthd.framedblocks.client.overlaygen;

import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.util.Utils;

record UVInfo(int uIdx, int vIdx, boolean uInv, boolean vInv)
{
    private static final UVInfo[] INFO_PER_FACE = generateInfos();

    public static UVInfo get(Direction face)
    {
        return INFO_PER_FACE[face.ordinal()];
    }

    private static UVInfo[] generateInfos()
    {
        UVInfo[] infos = new UVInfo[6];

        for (Direction face : Direction.values())
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
}
