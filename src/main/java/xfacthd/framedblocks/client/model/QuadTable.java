package xfacthd.framedblocks.client.model;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.util.Utils;

import java.util.*;

public final class QuadTable implements QuadMap
{
    private static final int LAYER_COUNT = RenderType.chunkBufferLayers().size();
    private static final int SIDE_COUNT = Direction.values().length + 1;
    private static final List<BakedQuad> EMPTY = List.of();

    @SuppressWarnings("unchecked")
    private final ArrayList<BakedQuad>[] quads = new ArrayList[LAYER_COUNT * SIDE_COUNT];
    private int boundBaseIdx = -1;

    public List<BakedQuad> getQuads(RenderType renderType, Direction side)
    {
        int idx = renderType.getChunkLayerId() * SIDE_COUNT + Utils.maskNullDirection(side);
        return Objects.requireNonNullElse(quads[idx], EMPTY);
    }

    public List<BakedQuad> getAllQuads(Direction side)
    {
        ArrayList<BakedQuad> allQuads = new ArrayList<>(LAYER_COUNT);
        int sideIdx = Utils.maskNullDirection(side);
        for (int i = 0; i < LAYER_COUNT; i++)
        {
            int idx = i * SIDE_COUNT + sideIdx;
            List<BakedQuad> layerQuads = quads[idx];
            if (layerQuads != null && !layerQuads.isEmpty())
            {
                Utils.copyAll(layerQuads, allQuads);
            }
        }
        return allQuads;
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
        Objects.requireNonNull(renderType, "Can't initialize for null layer");
        bindRenderType(renderType);
        int end = boundBaseIdx + SIDE_COUNT;
        for (int i = boundBaseIdx; i < end; i++)
        {
            if (quads[i] == null)
            {
                quads[i] = new ArrayList<>();
            }
        }
    }

    public void bindRenderType(RenderType renderType)
    {
        boundBaseIdx = renderType != null ? (renderType.getChunkLayerId() * SIDE_COUNT) : -1;
    }

    public void trim()
    {
        for (int i = 0; i < quads.length; i++)
        {
            List<BakedQuad> list = quads[i];
            if (list != null && list.isEmpty())
            {
                quads[i] = null;
            }
        }
    }
}
