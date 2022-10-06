package xfacthd.framedblocks.client.render.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.client.OutlineRender;

public class NoopOutlineRenderer implements OutlineRender
{
    public static final NoopOutlineRenderer INSTANCE = new NoopOutlineRenderer();

    private NoopOutlineRenderer() { }

    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        //NO-OP
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        // NO-OP
    }
}
