package xfacthd.framedblocks.client.util;

import com.mojang.blaze3d.vertex.VertexConsumer;

public record GhostVertexConsumer(VertexConsumer wrapped, int alpha) implements VertexConsumer
{
    @Override
    public VertexConsumer vertex(double x, double y, double z)
    {
        return wrapped.vertex(x, y, z);
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha)
    {
        return wrapped.color(red, green, blue, (alpha * this.alpha) / 0xFF);
    }

    @Override
    public VertexConsumer uv(float u, float v)
    {
        return wrapped.uv(u, v);
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v)
    {
        return wrapped.overlayCoords(u, v);
    }

    @Override
    public VertexConsumer uv2(int u, int v)
    {
        return wrapped.uv2(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z)
    {
        return wrapped.normal(x, y, z);
    }

    @Override
    public void endVertex()
    {
        wrapped.endVertex();
    }

    @Override
    public void defaultColor(int r, int g, int b, int a)
    {
        wrapped.defaultColor(r, g, b, a);
    }

    @Override
    public void unsetDefaultColor()
    {
        wrapped.unsetDefaultColor();
    }
}