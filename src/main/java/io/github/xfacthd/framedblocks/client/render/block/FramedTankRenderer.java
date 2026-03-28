package io.github.xfacthd.framedblocks.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.client.render.block.state.FramedTankRenderState;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedTankBlockEntity;
import io.github.xfacthd.framedblocks.common.capability.fluid.TankFluidResourceHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

public class FramedTankRenderer implements BlockEntityRenderer<FramedTankBlockEntity, FramedTankRenderState> {
    private static final float OFFSET = .01F;
    private static final float MIN_XZ = OFFSET;
    private static final float MAX_XZ = 1F - OFFSET;

    private final FluidStateModelSet fluidModels;

    public FramedTankRenderer(@SuppressWarnings("unused") BlockEntityRendererProvider.Context ctx) {
        this.fluidModels = Minecraft.getInstance().getModelManager().getFluidStateModelSet();
    }

    @Override
    public void submit(FramedTankRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        int fluidAmount = renderState.fluidAmount;
        if (fluidAmount != 0) {
            submitContents(poseStack, submitNodeCollector, renderState.fluidModel, fluidAmount, renderState.tint, renderState.lightCoords);
        }
    }

    @Override
    public FramedTankRenderState createRenderState() {
        return new FramedTankRenderState();
    }

    @Override
    public void extractRenderState(
            FramedTankBlockEntity blockEntity,
            FramedTankRenderState renderState,
            float partialTick,
            Vec3 cameraPos,
            ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);

        FluidStack fluid = blockEntity.getContents();
        if (fluid.isEmpty()) {
            return;
        }

        FluidModel fluidModel = fluidModels.get(fluid.getFluid().defaultFluidState());
        renderState.fluidModel = fluidModel;
        renderState.fluidAmount = fluid.getAmount();
        FluidTintSource tintSource = fluidModel.fluidTintSource();
        renderState.tint = tintSource != null ? tintSource.colorAsStack(fluid) : -1;
    }

    public static void submitContents(PoseStack poseStack, SubmitNodeCollector collector, FluidModel fluidModel, int fluidAmount, int tint, int light) {
        TextureAtlasSprite stillTex = fluidModel.stillMaterial().sprite();
        TextureAtlasSprite flowTex = fluidModel.flowingMaterial().sprite();
        float height = Mth.clamp(fluidAmount / (float) TankFluidResourceHandler.CAPACITY, OFFSET, 1F - OFFSET);
        boolean sameTex = stillTex == flowTex;

        RenderType bufferType = ClientUtils.getEntityRenderType(fluidModel.layer());
        collector.submitCustomGeometry(poseStack, bufferType, (pose, consumer) -> {
            float minU = flowTex.getU(MIN_XZ);
            float maxU = sameTex ? flowTex.getU(MAX_XZ) : flowTex.getU(8F / 16F - OFFSET);
            float minV = sameTex ? flowTex.getV(1F - height) : flowTex.getV(8F / 16F * (1F - height));
            float maxV = sameTex ? flowTex.getV(MAX_XZ) : flowTex.getV(8F / 16F - OFFSET);

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

            minU = stillTex.getU(MIN_XZ);
            maxU = stillTex.getU(MAX_XZ);
            minV = stillTex.getV(MIN_XZ);
            maxV = stillTex.getV(MAX_XZ);

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
        });
    }

    @Override
    public boolean shouldRender(FramedTankBlockEntity be, Vec3 camera) {
        return !be.getBlockState().getValue(FramedProperties.SOLID) && BlockEntityRenderer.super.shouldRender(be, camera);
    }
}
