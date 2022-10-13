package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.client.OutlineRender;

public final class InnerSlopedPrismOutlineRenderer extends SlopedPrismOutlineRenderer
{
    @Override
    public void draw(BlockState state, PoseStack pstack, VertexConsumer builder)
    {
        // Base edges
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 0, 0, 1);
        OutlineRender.drawLine(builder, pstack, 1, 0, 0, 1, 0, 1);
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 1, 0, 0);
        OutlineRender.drawLine(builder, pstack, 0, 0, 1, 1, 0, 1);

        // Top edges
        OutlineRender.drawLine(builder, pstack, 0, 1, 0, 0, 1, 1);
        OutlineRender.drawLine(builder, pstack, 1, 1, 0, 1, 1, 1);
        OutlineRender.drawLine(builder, pstack, 0, 1, 0, 1, 1, 0);

        // Vertical edges
        OutlineRender.drawLine(builder, pstack, 0, 0, 0, 0, 1, 0);
        OutlineRender.drawLine(builder, pstack, 1, 0, 0, 1, 1, 0);
        OutlineRender.drawLine(builder, pstack, 0, 0, 1, 0, 1, 1);
        OutlineRender.drawLine(builder, pstack, 1, 0, 1, 1, 1, 1);

        // Back triangle
        OutlineRender.drawLine(builder, pstack, 0, 1, 1, .5F, .5F, 1);
        OutlineRender.drawLine(builder, pstack, .5F, .5F, 1, 1, 1, 1);

        // Center line
        OutlineRender.drawLine(builder, pstack, .5F, .5F, .5F, .5F, .5F, 1);

        // Front sloped triangle
        OutlineRender.drawLine(builder, pstack, 0, 1, 0, .5F, .5F, .5F);
        OutlineRender.drawLine(builder, pstack, .5F, .5F, .5F, 1, 1, 0);
    }
}
