package xfacthd.framedblocks.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("deprecation")
public final class ItemRenderHelper
{
    private static final RenderType TRANSLUCENT = RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);

    public static void renderFakeItemTransparent(ItemStack stack, int x, int y, int alpha)
    {
        if (stack.isEmpty()) { return; }

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

        BakedModel model = renderer.getModel(stack, null, Minecraft.getInstance().player, 0);
        renderer.blitOffset += 50;
        renderItemModel(stack, x, y, alpha, model, renderer);
        renderer.blitOffset -= 50;
    }

    /**
     * {@link ItemRenderer::renderGuiItem} but with alpha
     */
    public static void renderItemModel(ItemStack stack, int x, int y, int alpha, BakedModel model, ItemRenderer renderer)
    {
        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);

        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.translate(x, y, 100.0F + renderer.blitOffset);
        modelViewStack.translate(8.0D, 8.0D, 0.0D);
        modelViewStack.scale(1.0F, -1.0F, 1.0F);
        modelViewStack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();

        boolean flatLight = !model.usesBlockLight();
        if (flatLight)
        {
            Lighting.setupForFlatItems();
        }

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        renderer.render(
                stack,
                ItemTransforms.TransformType.GUI,
                false,
                new PoseStack(),
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

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    private static MultiBufferSource wrapBuffer(MultiBufferSource buffer, int alpha, boolean forceTranslucent)
    {
        return renderType -> new GhostVertexConsumer(buffer.getBuffer(forceTranslucent ? TRANSLUCENT : renderType), alpha);
    }



    private ItemRenderHelper() { }
}