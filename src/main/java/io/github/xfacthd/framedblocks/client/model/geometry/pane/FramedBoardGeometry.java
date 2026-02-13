package io.github.xfacthd.framedblocks.client.model.geometry.pane;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMap;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class FramedBoardGeometry extends Geometry
{
    private static final float DEPTH = 1F/16F;
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Direction[][] EDGES = Util.make(new Direction[3][4], arr ->
    {
        for (Direction.Axis axis : Direction.Axis.values())
        {
            int idx = 0;
            for (Direction dir : DIRECTIONS)
            {
                if (!axis.test(dir))
                {
                    arr[axis.ordinal()][idx++] = dir;
                }
            }
        }
    });

    private final int faces;
    private final boolean allFaces;

    public FramedBoardGeometry(GeometryFactory.Context ctx)
    {
        this.faces = ctx.state().getValue(PropertyHolder.FACES);
        this.allFaces = faces == 0b00111111;
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        if (hasFace(quadDir.getOpposite()) && !canCullInner(blockData))
        {
            QuadModifier modifier = QuadModifier.of(quad)
                    .apply(Modifiers.setPosition(DEPTH));
            for (Direction edge : EDGES[quadDir.getAxis().ordinal()])
            {
                if (hasFace(edge))
                {
                    modifier.apply(Modifiers.cut(edge, 1F - DEPTH));
                }
            }
            modifier.export(quadMap.get(null));
        }
        if (!hasFace(quadDir))
        {
            Direction[] edges = EDGES[quadDir.getAxis().ordinal()];
            for (int i = 0; i < edges.length; i++)
            {
                Direction edge = edges[i];
                if (!hasFace(edge)) continue;

                QuadModifier modifier = QuadModifier.of(quad)
                        .apply(Modifiers.cut(edge.getOpposite(), DEPTH));
                if (i % 2 != 0)
                {
                    Direction cornerOne = edges[i - 1];
                    Direction cornerTwo = edges[(i + 1) % 4];
                    if (hasFace(cornerOne))
                    {
                        modifier.apply(Modifiers.cut(cornerOne, 1F - DEPTH));
                    }
                    if (hasFace(cornerTwo))
                    {
                        modifier.apply(Modifiers.cut(cornerTwo, 1F - DEPTH));
                    }
                }
                modifier.export(quadMap.get(quadDir));
            }
        }
    }

    private boolean hasFace(Direction side)
    {
        return (faces & (1 << side.ordinal())) != 0;
    }

    private boolean canCullInner(FramedBlockData blockData)
    {
        return allFaces && blockData.getCamoContent().isSolid();
    }

    @Override
    public boolean transformAllQuads()
    {
        return true;
    }
}
