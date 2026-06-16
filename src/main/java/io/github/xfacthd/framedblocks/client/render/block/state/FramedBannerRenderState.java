package io.github.xfacthd.framedblocks.client.render.block.state;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

public final class FramedBannerRenderState extends BlockEntityRenderState {
    private static final Matrix4fc IDENTITY = new Matrix4f();

    public final BlockModelRenderState modelRenderState = new BlockModelRenderState();
    @UnknownNullability
    public BannerModel bannerModel = null;
    public Transformation bannerTransform = Transformation.IDENTITY;
    public Matrix4fc flagTransform = IDENTITY;
    public float swing;

    public static final class ModelRenderState extends BlockModelRenderState {
        // TODO: upstream the breaking model submission
        public void submit(
                PoseStack poseStack,
                SubmitNodeCollector collector,
                int lightCoords,
                int overlayCoords,
                int outlineColor,
                ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
        ) {
            submitMultiLayer(poseStack, collector, lightCoords, overlayCoords, outlineColor);
            if (breakProgress != null && modelParts != null) {
                collector.submitBreakingBlockModel(poseStack, modelParts, breakProgress.progress());
            }
        }
    }
}
