package xfacthd.framedblocks.client.render.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;

public final class GhostVertexConsumer extends VertexConsumerWrapper
{
    private final int alpha;

    public GhostVertexConsumer(VertexConsumer wrapped, int alpha)
    {
        super(wrapped);
        this.alpha = alpha;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha)
    {
        return parent.color(red, green, blue, (alpha * this.alpha) / 0xFF);
    }
}