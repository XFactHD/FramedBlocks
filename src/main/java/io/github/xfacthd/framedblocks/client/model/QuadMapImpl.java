package io.github.xfacthd.framedblocks.client.model;

import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class QuadMapImpl implements QuadMapBuilder
{
    private static final int SIDE_COUNT = Direction.values().length + 1;

    @SuppressWarnings("unchecked")
    private final @Nullable List<BakedQuad>[] quads = new List[SIDE_COUNT];

    @Override
    public ArrayList<BakedQuad> getOrCreate(@Nullable Direction side)
    {
        int idx = DirUtils.maskNullDirection(side);
        ArrayList<BakedQuad> list = (ArrayList<BakedQuad>) quads[idx];
        if (list == null)
        {
            quads[idx] = list = new ArrayList<>();
        }
        return list;
    }

    @Nullable
    public ArrayList<BakedQuad> tryGet(@Nullable Direction side)
    {
        int idx = DirUtils.maskNullDirection(side);
        return (ArrayList<BakedQuad>) quads[idx];
    }

    /**
     * Forcefully insert an existing list into this map. Must only be used if the list for the provided side
     * is known to never be retrieved via {@link #getOrCreate(Direction)} after this operation!
     */
    public void set(@Nullable Direction side, List<BakedQuad> list)
    {
        quads[DirUtils.maskNullDirection(side)] = list;
    }

    public boolean isEmpty()
    {
        for (List<BakedQuad> list : quads)
        {
            if (list != null && !list.isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    public List<BakedQuad>[] build()
    {
        for (int i = 0; i < quads.length; i++)
        {
            List<BakedQuad> list = quads[i];
            if (list == null || list.isEmpty())
            {
                quads[i] = Collections.emptyList();
            }
        }
        //noinspection NullableProblems - The null entries are replaced
        return quads;
    }
}
