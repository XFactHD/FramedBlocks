package xfacthd.framedblocks.client.util;

import com.mojang.blaze3d.vertex.IVertexBuilder;

public class GhostVertexBuilder implements IVertexBuilder
{
    private final IVertexBuilder wrapped;
    private final int alpha;

    public GhostVertexBuilder(IVertexBuilder wrapped, int alpha)
    {
        this.wrapped = wrapped;
        this.alpha = alpha;
    }

    @Override
    public IVertexBuilder pos(double x, double y, double z) { return wrapped.pos(x, y, z); }

    @Override
    public IVertexBuilder color(int red, int green, int blue, int alpha)
    {
        return wrapped.color(red, green, blue, (alpha * this.alpha) / 0xFF);
    }

    @Override
    public IVertexBuilder tex(float u, float v) { return wrapped.tex(u, v); }

    @Override
    public IVertexBuilder overlay(int u, int v) { return wrapped.overlay(u, v); }

    @Override
    public IVertexBuilder lightmap(int u, int v) { return wrapped.lightmap(u, v); }

    @Override
    public IVertexBuilder normal(float x, float y, float z) { return wrapped.normal(x, y, z); }

    @Override
    public void endVertex() { wrapped.endVertex(); }
}