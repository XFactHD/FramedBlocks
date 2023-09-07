package xfacthd.framedblocks.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import xfacthd.framedblocks.common.block.sign.AbstractFramedSignBlock;

public class FramedHangingSignRenderer extends FramedSignRenderer
{
    private static final float TEXT_RENDER_SCALE = 0.9F;
    private static final Vector3f TEXT_OFFSET = new Vector3f(0.1F/16F, -5.12F/16F, 1.024F/16F);

    public FramedHangingSignRenderer(BlockEntityRendererProvider.Context ctx)
    {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack poseStack, float yRot, BlockState state)
    {
        poseStack.translate(0.5D, 0.9375D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.translate(0.0F, -0.3125F, 0.0F);
    }

    @Override
    protected float getSignTextRenderScale()
    {
        return TEXT_RENDER_SCALE;
    }

    @Override
    protected Vector3f getTextOffset(AbstractFramedSignBlock signBlock)
    {
        return TEXT_OFFSET;
    }
}
