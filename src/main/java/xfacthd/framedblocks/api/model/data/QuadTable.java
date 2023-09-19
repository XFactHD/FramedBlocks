package xfacthd.framedblocks.api.model.data;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.util.Utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class QuadTable implements QuadMap
{
    private static final int LAYER_COUNT = RenderType.chunkBufferLayers().size();
    static final int SIDE_COUNT = Direction.values().length + 1;
    private static final Direction[] SIDES = Stream.concat(Arrays.stream(Direction.values()), Stream.of((Direction) null)).toArray(Direction[]::new);
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
        Arrays.setAll(quads, i -> new ArrayList<>());
    }

    public void bindRenderType(RenderType renderType)
    {
        boundBaseIdx = renderType != null ? renderType.getChunkLayerId() : -1;
    }



    @NotNull
    @Override
    @Deprecated
    public Set<Direction> keySet()
    {
        return Arrays.stream(SIDES).collect(Collectors.toSet());
    }

    @NotNull
    @Override
    @Deprecated
    public Collection<List<BakedQuad>> values()
    {
        return Arrays.stream(SIDES).map(this::get).map(l -> (List<BakedQuad>) l).toList();
    }

    @NotNull
    @Override
    @Deprecated
    public Set<Entry<Direction, List<BakedQuad>>> entrySet()
    {
        return Arrays.stream(SIDES).map(dir -> new QuadEntry(dir, get(dir))).collect(Collectors.toSet());
    }

    private record QuadEntry(Direction dir, List<BakedQuad> quads) implements Map.Entry<Direction, List<BakedQuad>>
    {
        @Override
        public Direction getKey()
        {
            return dir;
        }

        @Override
        public List<BakedQuad> getValue()
        {
            return quads;
        }

        @Override
        public List<BakedQuad> setValue(List<BakedQuad> value)
        {
            List<BakedQuad> oldQuads = new ArrayList<>(quads);
            quads.clear();
            quads.addAll(value);
            return oldQuads;
        }
    }
}
