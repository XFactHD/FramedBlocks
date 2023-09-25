package xfacthd.framedblocks.api.model.data;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;

import java.util.*;

public interface QuadMap
{
    ArrayList<BakedQuad> get(Direction side);

    ArrayList<BakedQuad> put(Direction side, ArrayList<BakedQuad> quadList);
}
