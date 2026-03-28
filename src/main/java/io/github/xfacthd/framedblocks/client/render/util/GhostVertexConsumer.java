package io.github.xfacthd.framedblocks.client.render.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;

public final class GhostVertexConsumer extends VertexConsumerWrapper {
    private final int alpha;
    private final int white;

    public GhostVertexConsumer(VertexConsumer wrapped, int alpha) {
        super(wrapped);
        this.alpha = alpha;
        this.white = ARGB.color(alpha, 0xFFFFFF);
    }

    @Override
    public void addVertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx, float ny, float nz) {
        parent.addVertex(x, y, z, ARGB.multiply(white, color), u, v, overlay, light, nx, ny, nz);
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        return parent.setColor(red, green, blue, (alpha * this.alpha) / 0xFF);
    }
}
