package io.github.xfacthd.framedblocks.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.client.render.block.state.FramedTankRenderState;
import io.github.xfacthd.framedblocks.client.render.util.ExtFaceInfo;
import io.github.xfacthd.framedblocks.client.render.util.FramedRenderTypes;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedTankBlockEntity;
import io.github.xfacthd.framedblocks.common.capability.fluid.TankFluidResourceHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jspecify.annotations.Nullable;

public class FramedTankRenderer implements BlockEntityRenderer<FramedTankBlockEntity, FramedTankRenderState> {
    private static final Direction[] HOR_FACES = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
    private static final Direction[] VERT_FACES = Direction.Plane.VERTICAL.stream().toArray(Direction[]::new);
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
            FluidModel model = renderState.fluidModel;
            boolean lighterThanAir = renderState.lighterThanAir;
            boolean gaseous = renderState.gaseous;
            int tint = renderState.tint;
            int lightCoords = renderState.lightCoords;
            int fluidLight = renderState.fluidLightEmission;
            submitContents(poseStack, submitNodeCollector, model, fluidAmount, lighterThanAir, gaseous, tint, lightCoords, fluidLight);
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
        if (fluid.isEmpty() || !blockEntity.hasLevel()) {
            return;
        }

        FluidState fluidState = fluid.getFluid().defaultFluidState();
        FluidType fluidType = fluid.getFluidType();

        FluidModel fluidModel = fluidModels.get(fluidState);
        renderState.fluidModel = fluidModel;
        renderState.fluidAmount = fluid.getAmount();
        renderState.lighterThanAir = fluidType.isLighterThanAir();
        renderState.gaseous = fluid.is(Tags.Fluids.GASEOUS);
        FluidTintSource tintSource = fluidModel.fluidTintSource();
        renderState.tint = tintSource != null ? tintSource.colorAsStack(fluid) : -1;
        renderState.fluidLightEmission = fluidType.getLightLevel(fluid);
    }

    public static void submitContents(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            FluidModel fluidModel,
            int fluidAmount,
            boolean lighterThanAir,
            boolean gaseous,
            int tint,
            int light,
            int fluidLight
    ) {
        TextureAtlasSprite stillTex = fluidModel.stillMaterial().sprite();
        TextureAtlasSprite flowTex = fluidModel.flowingMaterial().sprite();
        float fillState = fluidAmount / (float) TankFluidResourceHandler.CAPACITY;
        float height;
        int finalTint;
        if (gaseous) {
            height = 1F - OFFSET;
            finalTint = ARGB.multiplyAlpha(tint, fillState);
        } else {
            height = Mth.clamp(fillState, OFFSET, 1F - OFFSET);
            finalTint = tint;
        }
        int finalLight = LightCoordsUtil.lightCoordsWithEmission(light, fluidLight);
        boolean sameTex = stillTex == flowTex;

        // FIXME: translucent fluids (regardless of no-shade) break outlines with Improved Translucency and break translucent chunk geometry without Improved Translucency
        RenderType bufferType = FramedRenderTypes.getEntityRenderType(gaseous ? ChunkSectionLayer.TRANSLUCENT : fluidModel.layer(), fluidLight > 0);
        collector.submitCustomGeometry(poseStack, bufferType, (pose, consumer) -> {
            float minY;
            float maxY;
            if (lighterThanAir && !gaseous) {
                minY = 1F - height;
                maxY = 1F - OFFSET;
            } else {
                minY = OFFSET;
                maxY = height;
            }

            float minU = flowTex.getU(MIN_XZ);
            float maxU = sameTex ? flowTex.getU(MAX_XZ) : flowTex.getU(8F / 16F - OFFSET);
            float minV = sameTex ? flowTex.getV(1F - height) : flowTex.getV(8F / 16F * (1F - height));
            float maxV = sameTex ? flowTex.getV(MAX_XZ) : flowTex.getV(8F / 16F - OFFSET);

            for (Direction face : HOR_FACES) {
                renderFace(pose, consumer, face, minY, maxY, minU, minV, maxU, maxV, finalTint, finalLight);
            }

            minU = stillTex.getU(MIN_XZ);
            maxU = stillTex.getU(MAX_XZ);
            minV = stillTex.getV(MIN_XZ);
            maxV = stillTex.getV(MAX_XZ);

            for (Direction face : VERT_FACES) {
                renderFace(pose, consumer, face, minY, maxY, minU, minV, maxU, maxV, finalTint, finalLight);
            }
        });
    }

    private static void renderFace(PoseStack.Pose pose, VertexConsumer buffer, Direction face, float minY, float maxY, float minU, float minV, float maxU, float maxV, int tint, int light) {
        ExtFaceInfo faceInfo = ExtFaceInfo.of(face);
        for (int i = 0; i < 4; i++) {
            ExtFaceInfo.ExtVertexInfo vertex = faceInfo.vertex(i);
            float x = vertex.xFace().select(MIN_XZ, minY, MIN_XZ, MAX_XZ, maxY, MAX_XZ);
            float y = vertex.yFace().select(MIN_XZ, minY, MIN_XZ, MAX_XZ, maxY, MAX_XZ);
            float z = vertex.zFace().select(MIN_XZ, minY, MIN_XZ, MAX_XZ, maxY, MAX_XZ);
            float u = vertex.uFace().select(minU, minV, maxU, maxV);
            float v = vertex.vFace().select(minU, minV, maxU, maxV);
            buffer.addVertex(pose, x, y, z).setColor(tint).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, face.getUnitVec3f());
        }
    }

    @Override
    public boolean shouldRender(FramedTankBlockEntity be, Vec3 camera) {
        return !be.getBlockState().getValue(FramedProperties.SOLID) && BlockEntityRenderer.super.shouldRender(be, camera);
    }
}
