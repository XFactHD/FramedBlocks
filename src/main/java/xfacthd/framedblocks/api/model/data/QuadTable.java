package xfacthd.framedblocks.api.model.data;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.util.Utils;

import java.util.*;

public final class QuadTable implements QuadMap
{
    private static final int LAYER_COUNT = RenderType.chunkBufferLayers().size();
    static final int SIDE_COUNT = Direction.values().length + 1;
    private static final List<BakedQuad> EMPTY = List.of();

    @SuppressWarnings("unchecked")
    private final ArrayList<BakedQuad>[] quads = new ArrayList[LAYER_COUNT * SIDE_COUNT];
    private int boundBaseIdx = -1;

    public List<BakedQuad> getQuads(RenderType renderType, Direction side)
    {
        int idx = renderType.getChunkLayerId() * SIDE_COUNT + Utils.maskNullDirection(side);
        return Objects.requireNonNullElse(quads[idx], EMPTY);
    }

    @Override
    public ArrayList<BakedQuad> get(Direction side)
    {
        Preconditions.checkState(boundBaseIdx > -1, "No RenderType bound");
        return quads[boundBaseIdx + Utils.maskNullDirection(side)];
    }

    @Override
    public ArrayList<BakedQuad> put(Direction side, ArrayList<BakedQuad> quadList)
    {
        Preconditions.checkState(boundBaseIdx > -1, "No RenderType bound");
        int idx = boundBaseIdx + Utils.maskNullDirection(side);
        ArrayList<BakedQuad> oldList = quads[idx];
        quads[idx] = quadList;
        return oldList;
    }

    public void initializeForLayer(RenderType renderType)
    {
        bindRenderType(renderType);
        int end = boundBaseIdx + SIDE_COUNT;
        for (int i = boundBaseIdx; i < end; i++)
        {
            quads[i] = new ArrayList<>();
        }
    }

    public void bindRenderType(RenderType renderType)
    {
        boundBaseIdx = renderType != null ? (renderType.getChunkLayerId() * SIDE_COUNT) : -1;
    }
}
