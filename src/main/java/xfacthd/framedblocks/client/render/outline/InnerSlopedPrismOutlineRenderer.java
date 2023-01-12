package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.render.OutlineRenderer;

public final class InnerSlopedPrismOutlineRenderer extends SlopedPrismOutlineRenderer
{
    @Override
    public void draw(BlockState state, PoseStack pstack, VertexConsumer builder)
    {
        // Base edges
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 0, 0, 0, 1);
        OutlineRenderer.drawLine(builder, pstack, 1, 0, 0, 1, 0, 1);
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 0, 1, 0, 0);
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 1, 1, 0, 1);

        // Top edges
        OutlineRenderer.drawLine(builder, pstack, 0, 1, 0, 0, 1, 1);
        OutlineRenderer.drawLine(builder, pstack, 1, 1, 0, 1, 1, 1);
        OutlineRenderer.drawLine(builder, pstack, 0, 1, 0, 1, 1, 0);

        // Vertical edges
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 0, 0, 1, 0);
        OutlineRenderer.drawLine(builder, pstack, 1, 0, 0, 1, 1, 0);
        OutlineRenderer.drawLine(builder, pstack, 0, 0, 1, 0, 1, 1);
        OutlineRenderer.drawLine(builder, pstack, 1, 0, 1, 1, 1, 1);

        // Back triangle
        OutlineRenderer.drawLine(builder, pstack, 0, 1, 1, .5F, .5F, 1);
        OutlineRenderer.drawLine(builder, pstack, .5F, .5F, 1, 1, 1, 1);

        // Center line
        OutlineRenderer.drawLine(builder, pstack, .5F, .5F, .5F, .5F, .5F, 1);

        // Front sloped triangle
        OutlineRenderer.drawLine(builder, pstack, 0, 1, 0, .5F, .5F, .5F);
        OutlineRenderer.drawLine(builder, pstack, .5F, .5F, .5F, 1, 1, 0);
    }
}
