package xfacthd.framedblocks.client.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import xfacthd.framedblocks.common.compat.rubidium.RubidiumCompat;

@SuppressWarnings("deprecation")
public final class ItemRenderHelper
{
    private static final RenderType TRANSLUCENT = RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);
    private static final Matrix4f SCALE_INVERT_Y = new Matrix4f().scaling(1F, -1F, 1F);

    public static void renderFakeItemTransparent(PoseStack poseStack, ItemStack stack, int x, int y, int alpha)
    {
        if (stack.isEmpty() || RubidiumCompat.isLoaded())
        {
            return;
        }

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

        BakedModel model = renderer.getModel(stack, null, Minecraft.getInstance().player, 0);
        renderItemModel(poseStack, stack, x, y, alpha, model, renderer);
    }

    /**
     * {@link ItemRenderer::renderGuiItem} but with alpha
     */
    public static void renderItemModel(
            PoseStack poseStack, ItemStack stack, int x, int y, int alpha, BakedModel model, ItemRenderer renderer
    )
    {
        poseStack.pushPose();
        poseStack.translate(x + 8F, y + 8F, 150F);
        poseStack.mulPoseMatrix(SCALE_INVERT_Y);
        poseStack.scale(16.0F, 16.0F, 16.0F);

        boolean flatLight = !model.usesBlockLight();
        if (flatLight)
        {
            Lighting.setupForFlatItems();
        }

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        renderer.render(
                stack,
                ItemDisplayContext.GUI,
                false,
                poseStack,
                wrapBuffer(buffer, alpha, alpha < 255),
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                model
        );
        buffer.endBatch();

        RenderSystem.enableDepthTest();

        if (flatLight)
        {
            Lighting.setupFor3DItems();
        }

        poseStack.popPose();
    }

    private static MultiBufferSource wrapBuffer(MultiBufferSource buffer, int alpha, boolean forceTranslucent)
    {
        return renderType -> new GhostVertexConsumer(buffer.getBuffer(forceTranslucent ? TRANSLUCENT : renderType), alpha);
    }



    private ItemRenderHelper() { }
}