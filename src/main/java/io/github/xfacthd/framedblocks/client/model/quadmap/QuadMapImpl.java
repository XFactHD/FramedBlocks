package io.github.xfacthd.framedblocks.client.model.quadmap;

import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class QuadMapImpl extends QuadMap implements QuadMapBuilderInternal {
    private static final int SIDE_COUNT = Direction.values().length + 1;

    @SuppressWarnings("unchecked")
    private final @Nullable List<BakedQuad>[] quads = new List[SIDE_COUNT];
    private int materialFlags = -1;

    @Override
    public List<BakedQuad> get(@Nullable Direction side) {
        int idx = DirUtils.maskNullDirection(side);
        return Objects.requireNonNull(quads[idx]);
    }

    @Override
    public ArrayList<BakedQuad> getOrCreate(@Nullable Direction side) {
        int idx = DirUtils.maskNullDirection(side);
        ArrayList<BakedQuad> list = (ArrayList<BakedQuad>) quads[idx];
        if (list == null) {
            quads[idx] = list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public @Nullable ArrayList<BakedQuad> tryGet(@Nullable Direction side) {
        int idx = DirUtils.maskNullDirection(side);
        return (ArrayList<BakedQuad>) quads[idx];
    }

    /// Forcefully insert an existing list into this map. Must only be used if the list for the provided side
    /// is known to never be retrieved via [#getOrCreate(Direction)] after this operation!
    @Override
    public void set(@Nullable Direction side, List<BakedQuad> list) {
        quads[DirUtils.maskNullDirection(side)] = list;
    }

    @Override
    public boolean isEmpty() {
        for (List<BakedQuad> list : quads) {
            if (list != null && !list.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public QuadMap build() {
        for (int i = 0; i < quads.length; i++) {
            List<BakedQuad> list = quads[i];
            if (list == null || list.isEmpty()) {
                quads[i] = Collections.emptyList();
            }
        }
        return this;
    }

    @Override
    public int materialFlags() {
        if (materialFlags == -1) {
            materialFlags = computeMaterialFlags();
        }
        return materialFlags;
    }

    private int computeMaterialFlags() {
        int flags = 0;
        for (List<BakedQuad> list : quads) {
            for (BakedQuad quad : Objects.requireNonNull(list)) {
                flags |= quad.materialInfo().flags();
            }
        }
        return flags;
    }
}
