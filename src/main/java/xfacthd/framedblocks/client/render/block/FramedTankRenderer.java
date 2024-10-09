package xfacthd.framedblocks.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import net.neoforged.neoforge.fluids.FluidStack;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.blockentity.special.FramedTankBlockEntity;
import xfacthd.framedblocks.common.capability.TankFluidHandler;

public class FramedTankRenderer implements BlockEntityRenderer<FramedTankBlockEntity>
{
    private static final float OFFSET = .01F;
    private static final float MIN_XZ = OFFSET;
    private static final float MAX_XZ = 1F - OFFSET;

    public FramedTankRenderer(@SuppressWarnings("unused") BlockEntityRendererProvider.Context ctx) { }

    @Override
    public void render(FramedTankBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        FluidStack fluid = be.getContents();
        if (fluid.isEmpty() || be.getLevel() == null) return;

        IClientFluidTypeExtensions fluidExt = IClientFluidTypeExtensions.of(fluid.getFluid());
        FluidState fluidState = fluid.getFluid().defaultFluidState();
        int tint = fluidExt.getTintColor(fluidState, be.getLevel(), be.getBlockPos());
        ResourceLocation stillTex = fluidExt.getStillTexture(fluidState, be.getLevel(), be.getBlockPos());
        ResourceLocation flowTex = fluidExt.getFlowingTexture(fluidState, be.getLevel(), be.getBlockPos());
        RenderType renderType = ItemBlockRenderTypes.getRenderLayer(fluidState);

        renderContents(poseStack, buffer, renderType, light, fluid.getAmount(), stillTex, flowTex, tint);
    }

    public static void renderContents(
            PoseStack poseStack,
            MultiBufferSource buffer,
            RenderType renderType,
            int light,
            int fluidAmount,
            ResourceLocation stillTex,
            ResourceLocation flowTex,
            int tint
    )
    {
        float height = Mth.clamp(fluidAmount / (float) TankFluidHandler.CAPACITY, OFFSET, 1F - OFFSET);
        boolean sameTex = stillTex.equals(flowTex);

        RenderType bufferType = RenderTypeHelper.getEntityRenderType(renderType, true);
        VertexConsumer consumer = buffer.getBuffer(bufferType);
        PoseStack.Pose pose = poseStack.last();

        TextureAtlasSprite sprite = FluidSpriteCache.getSprite(flowTex);
        float minU = sprite.getU(MIN_XZ);
        float maxU = sameTex ? sprite.getU(MAX_XZ) : sprite.getU(8F / 16F - OFFSET);
        float minV = sameTex ? sprite.getV(1F - height) : sprite.getV(8F / 16F * (1F - height));
        float maxV = sameTex ? sprite.getV(MAX_XZ) : sprite.getV(8F / 16F - OFFSET);

        // West
        consumer.addVertex(pose, MIN_XZ, height, MIN_XZ).setColor(tint).setUv(minU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, -1F, 0F, 0F);
        consumer.addVertex(pose, MIN_XZ, OFFSET, MIN_XZ).setColor(tint).setUv(minU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, -1F, 0F, 0F);
        consumer.addVertex(pose, MIN_XZ, OFFSET, MAX_XZ).setColor(tint).setUv(maxU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, -1F, 0F, 0F);
        consumer.addVertex(pose, MIN_XZ, height, MAX_XZ).setColor(tint).setUv(maxU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, -1F, 0F, 0F);

        // East
        consumer.addVertex(pose, MAX_XZ, height, MAX_XZ).setColor(tint).setUv(maxU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 1F, 0F, 0F);
        consumer.addVertex(pose, MAX_XZ, OFFSET, MAX_XZ).setColor(tint).setUv(maxU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 1F, 0F, 0F);
        consumer.addVertex(pose, MAX_XZ, OFFSET, MIN_XZ).setColor(tint).setUv(minU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 1F, 0F, 0F);
        consumer.addVertex(pose, MAX_XZ, height, MIN_XZ).setColor(tint).setUv(minU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 1F, 0F, 0F);

        // North
        consumer.addVertex(pose, MAX_XZ, height, MIN_XZ).setColor(tint).setUv(maxU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, 0F, -1F);
        consumer.addVertex(pose, MAX_XZ, OFFSET, MIN_XZ).setColor(tint).setUv(maxU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, 0F, -1F);
        consumer.addVertex(pose, MIN_XZ, OFFSET, MIN_XZ).setColor(tint).setUv(minU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, 0F, -1F);
        consumer.addVertex(pose, MIN_XZ, height, MIN_XZ).setColor(tint).setUv(minU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, 0F, -1F);

        // South
        consumer.addVertex(pose, MIN_XZ, height, MAX_XZ).setColor(tint).setUv(minU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, 0F, 1F);
        consumer.addVertex(pose, MIN_XZ, OFFSET, MAX_XZ).setColor(tint).setUv(minU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, 0F, 1F);
        consumer.addVertex(pose, MAX_XZ, OFFSET, MAX_XZ).setColor(tint).setUv(maxU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, 0F, 1F);
        consumer.addVertex(pose, MAX_XZ, height, MAX_XZ).setColor(tint).setUv(maxU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, 0F, 1F);

        if (!sameTex)
        {
            sprite = FluidSpriteCache.getSprite(stillTex);
        }
        minU = sprite.getU(MIN_XZ);
        maxU = sprite.getU(MAX_XZ);
        minV = sprite.getV(MIN_XZ);
        maxV = sprite.getV(MAX_XZ);

        // Up
        consumer.addVertex(pose, MAX_XZ, height, MAX_XZ).setColor(tint).setUv(maxU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, 1F, 0F);
        consumer.addVertex(pose, MAX_XZ, height, MIN_XZ).setColor(tint).setUv(maxU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, 1F, 0F);
        consumer.addVertex(pose, MIN_XZ, height, MIN_XZ).setColor(tint).setUv(minU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, 1F, 0F);
        consumer.addVertex(pose, MIN_XZ, height, MAX_XZ).setColor(tint).setUv(minU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, 1F, 0F);

        // Down
        consumer.addVertex(pose, MIN_XZ, OFFSET, MAX_XZ).setColor(tint).setUv(minU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, -1F, 0F);
        consumer.addVertex(pose, MIN_XZ, OFFSET, MIN_XZ).setColor(tint).setUv(minU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, -1F, 0F);
        consumer.addVertex(pose, MAX_XZ, OFFSET, MIN_XZ).setColor(tint).setUv(maxU, minV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, -1F, 0F);
        consumer.addVertex(pose, MAX_XZ, OFFSET, MAX_XZ).setColor(tint).setUv(maxU, maxV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0F, -1F, 0F);
    }

    @Override
    public boolean shouldRender(FramedTankBlockEntity be, Vec3 camera)
    {
        return !be.getBlockState().getValue(FramedProperties.SOLID) && BlockEntityRenderer.super.shouldRender(be, camera);
    }
}
