package xfacthd.framedblocks.api.model.data;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;

import java.util.*;

public final class QuadTable
{
    private static final int LAYER_COUNT = RenderType.chunkBufferLayers().size();
    private static final int SIDE_COUNT = Direction.values().length + 1;
    private static final Direction[] SIDES = Direction.values();

    private final QuadList[][] quads = new QuadList[LAYER_COUNT][];

    public List<BakedQuad> getQuads(RenderType renderType, Direction side)
    {
        QuadList[] bySide = quads[renderType.getChunkLayerId()];
        if (bySide != null)
        {
            return bySide[maskNull(side)].quads;
        }
        return Collections.emptyList();
    }

    public void put(RenderType renderType, Map<Direction, List<BakedQuad>> bySide)
    {
        QuadList[] bySideArr = new QuadList[SIDE_COUNT];

        bySideArr[maskNull(null)] = new QuadList(bySide.getOrDefault(null, List.of()));
        for (Direction side : SIDES)
        {
            bySideArr[maskNull(side)] = new QuadList(bySide.getOrDefault(side, List.of()));
        }

        quads[renderType.getChunkLayerId()] = bySideArr;
    }



    private static int maskNull(Direction side)
    {
        if (side == null)
        {
            return 6;
        }
        return side.get3DDataValue();
    }

    private record QuadList(List<BakedQuad> quads) { }
}
